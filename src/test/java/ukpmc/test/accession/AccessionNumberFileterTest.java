package ukpmc.test.accession;

import java.io.BufferedReader;
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

	private static final String ACCESSION = "automata/acc170731.mwt";


	@Test
	public void testOnlineValidationPDBe() {
		String test =  "<SENT sid=\"1\" pm=\".\"><plain>Recently, protein levels of important glycosylation enzymes, B3GNT8 and MGAT4A, were found decreased in the prefrontal cortex in schizophrenia (12 case–control pairs),33 whereas in our study B3GNT1, B3GNT3 and MGAT1 transcripts were downregulated in schizophrenia cases (Supplementary Table S1). </plain></SENT>";
		
		testAccessionNumberFilter(test,"");
	}

	private void testAccessionNumberFilter(String input, String output) {

		try {
			File testFile = new File("text.txt");
			String patternmapped =  patternMatch(input, ACCESSION);
			
			
			InputStream in = new ByteArrayInputStream(patternmapped.getBytes("UTF-8"));
			FileOutputStream out = new FileOutputStream(testFile);
			PrintStream outpw = new PrintStream(out);
			
			AnnotationFilter annotationFilter = new AnnotationFilter(in,outpw);
			
			annotationFilter.run();
			
			
			
			String sent = getFileContent(new FileInputStream(testFile));
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
	
	private String getFileContent(FileInputStream file) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    try {
	        while((line = reader.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}
}
