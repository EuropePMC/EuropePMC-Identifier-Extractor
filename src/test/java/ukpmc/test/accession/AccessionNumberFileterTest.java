package ukpmc.test.accession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.logging.Level;

import org.junit.Test;

import monq.jfa.DfaRun;
import monq.jfa.ReaderCharSource;
import monq.net.Service;
import monq.programs.DictFilter;
import ukpmc.AccResolver;
import ukpmc.AnnotationFilter;
import ukpmc.BioStudiesResolver;
import ukpmc.DoiResolver;
import ukpmc.HipsciResolver;
import ukpmc.HpaResolver;
import ukpmc.NcbiResolver;
import ukpmc.ResponseCodeResolver;

/*
 *@Jyothi Katuri
 */

public class AccessionNumberFileterTest {

	private DfaRun dfaRun = new DfaRun(AnnotationFilter.dfa_boundary);
	private static final String ACCESSION = "automata/acc170731.mwt";


	@Test
	public void testOnlineValidationPDBe() {
		String test =  "<SENT sid=\"1\" pm=\".\"><plain>Recently, protein levels of important glycosylation enzymes, B3GNT8 and MGAT4A, were found decreased in the prefrontal cortex in schizophrenia (12 case–control pairs),33 whereas in our study B3GNT1, B3GNT3 and MGAT1 transcripts were downregulated in schizophrenia cases (Supplementary Table S1). </plain></SENT>";
		
		testAccessionNumberFilter(test,"");
	}

	private void testAccessionNumberFilter(String input, String output) {

		try {
			String patternmapped =  patternMatch(input, ACCESSION);
			
			InputStream in = new ByteArrayInputStream(patternmapped.getBytes("UTF-8"));
			dfaRun.setIn(new ReaderCharSource(in));
			FileOutputStream out = new FileOutputStream(new File("text.txt"));
			
			PrintStream outpw = new PrintStream(out);
			dfaRun.filter(outpw);
			
			String sent = getFileContent(new FileInputStream("text.txt"));
			System.out.println(sent);
			
		} catch (IOException e) {
			System.err.println( e);
		}
	}
	
	private String patternMatch(String text, String dict) {
		String  sent =  null;
		try {

			Reader rin = new FileReader(dict);
			DictFilter df = new DictFilter(rin, "raw", null, false);
			df.setInputEncoding("UTF-8");
			df.setOutputEncoding("UTF-8");
			InputStream in = new ByteArrayInputStream(text.getBytes("UTF-8"));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Service svc = df.createService(in, out, null);
			svc.run();
			sent = out.toString("UTF-8");
			System.out.println(sent);

		}catch(Exception e) {
			System.out.println(e.getMessage());
		} 
		return sent;
	}
	
	public String getFileContent( FileInputStream fis ) {
		StringBuilder sb = new StringBuilder();
		try {			
			Reader r = new InputStreamReader(fis, "UTF-8");  //or whatever encoding
			int ch = r.read();
			while(ch >= 0) {
				sb.append(ch);
				ch = r.read();
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		} 
		return sb.toString();
	}

}
