package ukpmc;

import static ukpmc.util.TaggerUtils.reEmbedContent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import monq.jfa.AbstractFaAction;
import monq.jfa.Dfa;
import monq.jfa.DfaRun;
import monq.jfa.Nfa;
import monq.jfa.ReaderCharSource;
import monq.jfa.Xml;
import monq.net.FilterServiceFactory;
import monq.net.TcpServer;
import ukpmc.scala.GrantMwtParser;
import ukpmc.scala.GrantMwtAtts;

/*
 *@Jyothi Katuri
 */

public class GrantAnnotationFilter {

	private static final Logger LOGGER = Logger.getLogger(GrantAnnotationFilter.class.getName());

	public static Dfa dfa_boundary;
	private static Dfa dfa_plain;
	private static Dfa dfa_entity;
	private static String dfa_content;
	private static  String actual_sentence;

	private static ContextAndPatternValidator validator = new ContextAndPatternValidator();

	private static int TEXT_AFTER_SIZE = 60;
	private static int ABBR_CONTEXT_SIZE = 50;
	private static int ERC_ABBR_CONTEXT_SIZE = 40;
	
	private InputStream in;
	private OutputStream out;

	public GrantAnnotationFilter(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	private static AbstractFaAction procBoundary = new AbstractFaAction() {
		public void invoke(StringBuilder yytext, int start, DfaRun runner) {

			try {
				Map <String, String> map = Xml.splitElement(yytext, start);
				String content = map.get(Xml.CONTENT);
				dfa_content = content;
				actual_sentence = extractNoramlSentence(dfa_content);

				String newoutput;

				if ("SENT".equals(map.get(Xml.TAGNAME))) {
					DfaRun dfaRunPlain = new DfaRun(dfa_plain);
					dfaRunPlain.clientData = map.get(Xml.TAGNAME);
					newoutput = dfaRunPlain.filter(content);
				} else {
					DfaRun dfaRunPlain = new DfaRun(dfa_plain);
					dfaRunPlain.clientData = map.get("type");
					newoutput = dfaRunPlain.filter(content);
				}

				String embedContent = reEmbedContent(newoutput, yytext, map, start);
				yytext.replace(start, yytext.length(), embedContent);
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "context", e);
			}
		}
	};

	private static AbstractFaAction procPlain = new AbstractFaAction() {
		public void invoke(StringBuilder yytext, int start, DfaRun runner) {

			try {
				Map <String, String> map = Xml.splitElement(yytext, start);
				String content = map.get(Xml.CONTENT);

				DfaRun dfaRunEntity = new DfaRun(dfa_entity);
				dfaRunEntity.clientData = runner.clientData; // SENT or SecTag type=xxx

				String newoutput = dfaRunEntity.filter(content);
				String embedContent = reEmbedContent(newoutput, yytext, map, start);
				yytext.replace(start, yytext.length(), embedContent);
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "context", e);
			}
		}
	};

	private static AbstractFaAction procEntity = new AbstractFaAction() {

		public void invoke(StringBuilder yytext, int start, DfaRun runner) {
			try {

				boolean isValid = false;
				Map<String, String> map = Xml.splitElement(yytext, start);

				GrantMwtAtts m = new GrantMwtParser(map).parse();

				String content =  m.content().replace("[", "").replace("]", ""); //sometimes MWt is mining grant ids with [ like [G45. to avoid that remove [ in the content

				if(validator.checkSpecialCharsForPattern(actual_sentence, content)) {//check for the context
					//int size = actual_sentence.contains("NC3Rs") ? 50 : m.wsize();
					String textBeforeEntity = getTextBeforeEntity( m.wsize(),content);
					//	System.out.println(m.db() == "erc" ? ERC_ABBR_CONTEXT_SIZE : ABBR_CONTEXT_SIZE);
					String textBeforeEntityForAbbr= getTextBeforeEntity( m.db().equalsIgnoreCase("erc") ? ERC_ABBR_CONTEXT_SIZE : ABBR_CONTEXT_SIZE,content);
					if ("noval".equals(m.valMethod())) {
						isValid = true;
					}  else if ("context".equals(m.valMethod())) {
						if ( validator.isContextExist(textBeforeEntity, textBeforeEntityForAbbr, m.ctx(),m.abbrcontext(),m.negate(),m.db())) {
							if( validator.isConflictFunderNotExist(textBeforeEntity,m.db(),content))
								isValid = true;
						}else {//check if the context is after pattern							
							String textAfterEntity = getTextAfterEntity(content);
							isValid = validator.isContextAfterPattern(textAfterEntity, m.ctx());													
						}
					} 
				}else {
					LOGGER.info("&&&&& ******* NOT a Valid Pattern = " +  content + " for the funder = \""+ m.domain()+"\" in a sentence = " + actual_sentence);
				}

				if (isValid ) {
					String tagged = "<" + m.tagName() +" abbr=\"" + m.db() + "\" id=\"" + content +  "\" name=\"" + m.domain() + "\" sent=\"" + actual_sentence +"\">"+ content+ "</" + m.tagName() + ">";
					LOGGER.info(tagged);
					yytext.replace(start, yytext.length(), tagged);	
				} else {
					yytext.replace(start, yytext.length(), m.content());
				}
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "context", e);
			}
		}
	};

	/*
	 * TextBeforeEntity =  index of content in actual_sentence - windowsize
	 */
	private static String getTextBeforeEntity( int windowSize, String content) {
		String sent1 = actual_sentence;
		int indexOfcontent = sent1.indexOf(content);
		int start = (indexOfcontent - windowSize )	< 0 ? 0 : (indexOfcontent - windowSize);
		return 	sent1.substring(start, indexOfcontent) ;
	}
	/*
	 * TextAfterEntity =  index of content in actual_sentence +100
	 */
	private static String getTextAfterEntity(String content) {
		String sent2 = actual_sentence;
		int indexOfcontent = sent2.indexOf(content);
		int end = (indexOfcontent + TEXT_AFTER_SIZE )	< sent2.length() ? (indexOfcontent + TEXT_AFTER_SIZE) : (sent2.length());
		return sent2.substring(indexOfcontent, end) ;		 
	}
	/*
	 * m.content() is a sentence before context with annotated tags like <plain>, <FUND or <Z:acc etc. 
	 * So we need fined sentence to avoid false positives and also need to reduce the sentence length for window size. If you have those tags, window size doesnt make any sense.
	 */
	private  static String extractNoramlSentence(String sentPlain) {
		String cleanPlainTag = dfa_content.replace( "<plain>","").replace("</plain>","");
		String cleanZACCTags = cleanPlainTag.replaceAll("<FUND(.+?)>","").replaceAll("</FUND>", "");
		return cleanZACCTags;
	}

	static {
		try {
			Nfa bnfa = new Nfa(Nfa.NOTHING);
			bnfa .or(Xml.GoofedElement("SENT"), procBoundary);
			dfa_boundary = bnfa.compile(DfaRun.UNMATCHED_COPY);

			Nfa snfa = new Nfa(Nfa.NOTHING);
			snfa.or(Xml.GoofedElement("plain"), procPlain);
			dfa_plain = snfa.compile(DfaRun.UNMATCHED_COPY);

			Nfa anfa = new Nfa(Nfa.NOTHING);
			anfa.or(Xml.GoofedElement("FUND"), procEntity);
			dfa_entity = anfa.compile(DfaRun.UNMATCHED_COPY);

		} catch (Exception e) {
			LOGGER.log(Level.INFO, "context", e);
		}
	}

	@SuppressWarnings("deprecation")
	public void run() {
		DfaRun dfaRun = new DfaRun(dfa_boundary);
		dfaRun.setIn(new ReaderCharSource(in));
		PrintStream outpw = new PrintStream(out);
		try {
			dfaRun.filter(outpw);
		} catch (IOException e) {
			LOGGER.log(Level.INFO, "context", e);
		}
	}

	public static void main(String[] arg) throws IOException {		
		int j = 0;
		Boolean stdpipe = false;
/*		int port = 7811;
 		try {
			if (arg.length > 0) {   
				port = Integer.parseInt(arg[0]);
				j = 1;
			}
		} catch (java.lang.NumberFormatException ne) {
			;
		}
*/
		for (int i = j; i < arg.length; i++) {
			if ("-stdpipe".equals(arg[i])) {
				stdpipe = true;
			}
		}
		if (stdpipe) {
			GrantAnnotationFilter validator = new GrantAnnotationFilter(System.in, System.out);
			validator.run();
		}else {			
			LOGGER.warning("Couldn't start server");
			System.exit(1);
		}

		/*
		GrantAnnotationFilter validator = new GrantAnnotationFilter(new FileInputStream("C:\\software\\scala-world\\workspace\\MyAnnotator\\test\\sentencisedict1.xml"), 
				new FileOutputStream("C:\\software\\scala-world\\workspace\\MyAnnotator\\test\\sentencisefilter.xml"));
		validator.run();
		 */
	}
}
