package ukpmc;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *@Jyothi Katuri
 */

public class ContextAndPatternValidator {

	private static final Logger LOGGER = Logger.getLogger(ContextAndPatternValidator.class.getName());


	private static GrantIDResolver gr = new GrantIDResolver();
	private static final String PATTERN_BOUNDARY_MATCH1 = "\\(|[|#|,|;|\\)|]|\\s|&|-";
	private static final String PRE_PATTERN_BOUNDARY_MATCH = "no|nr|No|Nr|no.|nr.|No.|Nr.|N°|n°|N°.|n°.";
	private static final String PATTERN_BOUNDARY_MATCH = "\\(|\\)|\\[|\\]|#|,|;|\\s|&|-|_";//(,),[,],,,;, ,&,-,_
	private static final String PATTERN_END_MATCH = ".|.\\s|\\).|].|\\).\\s|#.|/\\s|/\\) ";

	public ContextAndPatternValidator() {
		super();
	}
	/*
	 * check if the context exist before the pattern. Avoid any negative contexts if any and see any abbreviation context
	 */
	public boolean isContextExist(String textBeforeEntity, String textBeforeEntityForAbbr, String fullContext, String abbrContext, String negateContext, String db) {
		Boolean hasFunderName = false; Boolean hasNegateContext = false;

		if(negateContext != null && !negateContext.isEmpty()) {	
			hasNegateContext = findPattern(negateContext, textBeforeEntity);
		}
		if(hasNegateContext) {
			return false;
		}else {
			if(db.equalsIgnoreCase("llr")){
				hasFunderName =  gr.isLLRContextContains(textBeforeEntity);		
			}else {
				hasFunderName = findPattern(fullContext, textBeforeEntity);
			}
			if(!hasFunderName && abbrContext != null && !abbrContext.isEmpty()) {	
				hasFunderName = findPattern(abbrContext, textBeforeEntityForAbbr);				
			}				
		}

		return hasFunderName;
	}

	public boolean isContextExistOld(String textBeforeEntity, String textBeforeEntityForAbbr, String fullContext, String abbrContext, String negateContext, String db) {
		Boolean hasFunderName = false; Boolean hasNegateContext = false; Boolean checkContext = false;

		if(negateContext != null && !negateContext.isEmpty()) {			
			hasNegateContext = findPattern(negateContext, textBeforeEntity);	
		}


		if(db.equalsIgnoreCase("llr")){
			hasFunderName =  gr.isLLRContextContains(textBeforeEntity);		
		}else {
			hasFunderName = findPattern(fullContext, textBeforeEntity);
		}

		if(!hasFunderName && abbrContext != null && !abbrContext.isEmpty()) {	
			hasFunderName = findPattern(abbrContext, textBeforeEntityForAbbr);
		}		
		if(negateContext != null && !negateContext.isEmpty()) {			
			hasNegateContext = findPattern(negateContext, textBeforeEntity);	
		}

		if(hasFunderName && hasNegateContext) {
			checkContext = false;
		}else if(hasFunderName && !hasNegateContext) {
			checkContext = true;
		}else if(!hasFunderName && hasNegateContext) {
			checkContext = false;
		}else if(!hasFunderName && !hasNegateContext) {
			checkContext = false;
		}

		return checkContext;
	}

	private boolean findPattern(String context, String text) {
		Pattern p = Pattern.compile(context);
		Matcher m = p.matcher(text);
		return m.find();		
	}
	/*
	 * check if the context exist after the pattern. i just camp up with few scenarios where the pattern is preceded by words.
	 */
	public boolean isContextAfterPattern(String textAfterEntity, String context) {
		if(		textAfterEntity.startsWith(" by")||textAfterEntity.startsWith(" from") ||
				textAfterEntity.startsWith("] from") || textAfterEntity.startsWith(" ] by")||
				textAfterEntity.startsWith(") from") || textAfterEntity.startsWith(") by") ||				
				textAfterEntity.startsWith("), funded by")||textAfterEntity.startsWith("], funded by") ||
				textAfterEntity.startsWith(" funded by")||textAfterEntity.startsWith(" granted by") ||
				textAfterEntity.startsWith(", funded by")||textAfterEntity.startsWith(", granted by")) {

			Pattern p = Pattern.compile(context);
			Matcher m = p.matcher(textAfterEntity);
			return m.find();
		} else 
			return false;
	}

	/*
	 * there are cases where the pattern belongs to more than one funder like 999999 belongs to both ERC and WT. 
	 * To avoid that, i have used parallel dictionaries but still we will have conflict with names so check for conflict names.
	 */
	public boolean isConflictFunderNotExist(String textBeforeEntity,String db, String grantId) { 
		int grantIdLength = grantId != null ? grantId.length() : 0;
		boolean isConflictGrantNotExist = gr.isConflictFunderNameExitInText(textBeforeEntity,db, grantIdLength);
		return !isConflictGrantNotExist;
	}

	/*
	 * Valid: Telethon Italy (no. GGP08064)
	 * Not Vlaid: 2006-0779, g0900951
	 * Bad: Leukaemia and Lymphoma Research [12029]; the Medical Research Council [g0900951] -- 00951 mined as bloodwise
	 * 
	 * pattern must not be substring of another string,not having hyphen before/after it,no dot, 
	 * 
	 * Pattern must be surrounded by whitespace,(),[],;, comma, # and semicolon
	 * get one character before and after pattern and check whether the pattern is valid or not
	 * 
	 * Wellcome Trust (grant no. WT082597/Z/07/Z: M.N.O., A.C.;
	 */
	public boolean isValidPattern(String sent, String cntx) {

		boolean validPattern = true;
		try {
			int ctxLength = cntx.length();
			int ctxIndex = sent.indexOf(cntx);
			int sentLength = sent.length();

			int start =ctxIndex > 0 ? ctxIndex-1 : ctxIndex; 
			int end1 = (ctxIndex+ctxLength) < sentLength ? (ctxIndex+ctxLength)+1 : sentLength; //pattern in the middle of sentence

			int end2 = 0;
			if((ctxIndex+ctxLength)+1 == sentLength)//pattern at the end sentence followed by full stop
				end2 = sentLength;
			else if((ctxIndex+ctxLength)+2 == sentLength) //pattern at the end sentence followed by special char and full stop like ). or ].
				end2 = sentLength;

			String onePrefixSuffixPattern = sent.substring(start, end1);
			String onePrefixTwoSuffixPattern = end2 > 0 ? sent.substring(start, end2) : null;

			validPattern = checkSpecialChar(onePrefixSuffixPattern,onePrefixTwoSuffixPattern, ((ctxIndex+ctxLength) < sentLength));

			//boolean validPattern2 = checkSpecialCharsForPattern(sent,cntx);

		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, " something wrong with sent=" + sent + "context= " +cntx , e);
			e.printStackTrace();
		}
		return validPattern;
	}

	boolean checkSpecialCharsForPattern(String sent, String pattern) {
		boolean isValidPattern = false;
		try {
			int end1 =0; int end2=0; String suff2 = null;
			int ctxIndex = sent.indexOf(pattern);
			int fullContextIndex = ctxIndex+(pattern.length());
			int sentLength = sent.length();

			int start =ctxIndex > 0 ? ctxIndex-1 : ctxIndex; 
			int start1 =ctxIndex > 3 ? ctxIndex-3 : ctxIndex; 
			String pre = Character.toString(sent.charAt(start));
			String pre1 = sent.substring(start1, ctxIndex);

			// case1: see if the end of pattern is in the middle of sentence
			if(fullContextIndex < sentLength || ((fullContextIndex+1) < sentLength))
				end1 = fullContextIndex+1;
			// case2: see if the end of pattern is at the end of sentence. Ex; hgriue irui WT084289MA. or hgriue irui WT084289MA).
			if(fullContextIndex+1 == sentLength)
				end2 = sentLength;
			else if(fullContextIndex+2 == sentLength)
				end2 = sentLength;

			boolean isEndPatternValid = false; boolean isPrePatternValid = false;

			isPrePatternValid = findPattern(PATTERN_BOUNDARY_MATCH,pre) || findPattern(PRE_PATTERN_BOUNDARY_MATCH,pre1);
			
			//Case3: only do validation for the end of pattern fit he pattern doesnt end of sentence
			if(fullContextIndex != sentLength) {
				String suff1 = sent.substring(fullContextIndex, end1);
				if(end2 > 0) {
					suff2 = sent.substring(fullContextIndex, end2);
					isEndPatternValid = findPattern(PATTERN_END_MATCH,suff2);
				}else
					isEndPatternValid = findPattern(PATTERN_END_MATCH,suff1);				
			}	
		
			isValidPattern = isPrePatternValid && isEndPatternValid;
			
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, "********* something wrong with sent=" + sent + "context= " +pattern , e);
			e.printStackTrace();
		}
		return isValidPattern;
	}
	/* 
	 * pattern ends rules:
	 * Accept: sent end with pattern ).
	 * 		   sent end with pattern followed by full stop .	
	 */
	private boolean checkSpecialChar(String id, String endid2, boolean hasEndChar) {
		boolean endup = true;
		boolean startup = id.startsWith("(")||id.startsWith("[")||id.startsWith(",")||id.startsWith(";")||id.startsWith("#")||id.startsWith(" ")||
				id.startsWith(")")||id.startsWith("]")||id.startsWith("&") ;

		if(hasEndChar) {
			if(endid2 != null) {
				endup = (id.endsWith("(")||id.endsWith("[")||id.endsWith(",")||id.endsWith(";")||id.endsWith("#")||id.endsWith(" ")||
						id.endsWith(")")||id.endsWith("]")||id.endsWith("&")) || (endid2.endsWith(".")||endid2.endsWith(").")||endid2.endsWith("].")||endid2.endsWith(" .") );
			}else endup = (id.endsWith("(")||id.endsWith("[")||id.endsWith(",")||id.endsWith(";")||id.endsWith("#")||id.endsWith(" ")||
					id.endsWith(")")||id.endsWith("]")||id.endsWith("&"));
		}
		return (startup && endup);

	}
	public static void main(String[] args) {
		ContextAndPatternValidator cv = new ContextAndPatternValidator();
		//cv.findContextWithOnePrefixSufix("Bloodwise 10.13039/501100007903 Biotechnology and Biological Sciences Research Council","13039");
		//cv.findContextWithOnePrefixSufix("Bloodwise 10.13039.501100007903 Biotechnology and Biological Sciences Research Council","13039");
		//	System.out.println(cv.isValidPattern("Bloodwise 10 00951]","00951"));
		//System.out.println(cv.isValidPattern("This work was supported by grants from The Wellcome Trust (076078/Z/04/Z and 07664/Z/05/Z, PK).","076078/Z/04/Z"));
		String s = "This work was supported by grants from The Wellcome Trust (076078/Z/04/Z and 07664/Z/05/Z, PK).".substring(59, 73);
		//System.out.println(s);
		//System.out.println(cv.isValidPattern("DE-AC02-06CH11357.", "DE-AC02-06"));
		//System.out.println(cv.isValidPattern("Acknowledgements This study is funded by the Wellcome Trust, UK, HCPC Latin American Centres of Excellence Programme (ref 072405/Z/03/Z).(", "072405/Z/03/Z"));
		//System.out.println("072405/Z/03/Z).".endsWith(")."));
		//System.out.println("072405/Z/03/Z).".indexOf("072"));
		int a ="072".length(); 
		//System.out.println(cv.isValidPattern("This work was supported by the Wellcome Trust [073109/Z/03/Z].", "073109/Z/03/Z"));
		System.out.println(cv.isValidPattern("European Research Council Starting grant (640004).", "640004"));
		

		//System.out.println("072405/Z/03/Z).".charAt(index), (0+a)+1));
	}
}