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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 *@Jyothi Katuri 
 */

public class ResourceNameFileterTest {

	private static final String RESOURCE_DICTIONARY = "automata/resources180904.mwt";


	@Test 
	public void testEva() {
		String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>archive EVA</plain></SENT><SENT sid=\"2\"><plain>no tagging EVA</plain></SENT><SENT sid=\"3\"><plain>no tagging EVA European Variation Archive</plain></SENT></SecTag>"+
				        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>archive EVA</plain></SENT><SENT sid=\"2\"><plain>no tagging EVA</plain></SENT><SENT sid=\"3\"><plain>no tagging European Variation Archive</plain></SENT></SecTag>"+
				        "<SENT sid=\"1\"><plain>archive EVA</plain></SENT><SENT sid=\"2\"><plain>no tagging EVA</plain></SENT><SENT sid=\"3\"><plain>no tagging European Variation Archive</plain></SENT>";
		String outputExpected = "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>archive EVA</plain></SENT><SENT sid=\"2\"><plain>no tagging EVA</plain></SENT><SENT sid=\"3\"><plain>no tagging EVA European Variation Archive</plain></SENT></SecTag>"+
		        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>archive <z:acc db=\"Reva\" ids=\"EVA\">EVA</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging EVA</plain></SENT><SENT sid=\"3\"><plain>no tagging <z:acc db=\"Reva\" ids=\"European Variation Archive\">European Variation Archive</z:acc></plain></SENT></SecTag>"+
		        "<SENT sid=\"1\"><plain>archive <z:acc db=\"Reva\" ids=\"EVA\">EVA</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging EVA</plain></SENT><SENT sid=\"3\"><plain>no tagging <z:acc db=\"Reva\" ids=\"European Variation Archive\">European Variation Archive</z:acc></plain></SENT>";		
		testResourceNameFilter(input,outputExpected);
	}
	
	
	@Test
	public void testExcludeSections() {
			String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with an image from EMPIAR</plain></SENT><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with no context from EMPIAR</plain></SENT></SecTag>"+
					 "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with an image from EMPIAR</plain></SENT><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with no context from EMPIAR</plain></SENT></SecTag>"+
					 "<SENT sid=\"1\"><plain> Resource name Protein Databank toghether with an image from EMPIAR</plain></SENT><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with no context from EMPIAR</plain></SENT>";
					
			String outputExpected = "<SecTag type=\"REF\"><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with an image from EMPIAR</plain></SENT><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with no context from EMPIAR</plain></SENT></SecTag>"+
					 "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain> Resource name <z:acc db=\"Rpdb\" ids=\"Protein Databank\">Protein Databank</z:acc> toghether with an image from <z:acc db=\"Rempiar\" ids=\"EMPIAR\">EMPIAR</z:acc></plain></SENT><SENT sid=\"1\"><plain> Resource name <z:acc db=\"Rpdb\" ids=\"Protein Databank\">Protein Databank</z:acc> toghether with no context from EMPIAR</plain></SENT></SecTag>"+
					 "<SENT sid=\"1\"><plain> Resource name <z:acc db=\"Rpdb\" ids=\"Protein Databank\">Protein Databank</z:acc> toghether with an image from <z:acc db=\"Rempiar\" ids=\"EMPIAR\">EMPIAR</z:acc></plain></SENT><SENT sid=\"1\"><plain> Resource name <z:acc db=\"Rpdb\" ids=\"Protein Databank\">Protein Databank</z:acc> toghether with no context from EMPIAR</plain></SENT>";
			testResourceNameFilter(input,outputExpected);
		
	}
	

	private void testResourceNameFilter(String input, String output) {

		try {
			File testFile = new File("text.txt");
			String patternmapped =  patternMatch(input, RESOURCE_DICTIONARY);
			
			
			InputStream in = new ByteArrayInputStream(patternmapped.getBytes("UTF-8"));
			FileOutputStream out = new FileOutputStream(testFile);
			PrintStream outpw = new PrintStream(out);
			
			AnnotationFilter annotationFilter = new AnnotationFilter(in,outpw);
			
			annotationFilter.run();		
			
			String sent = getFileContent(new FileInputStream(testFile));
			System.out.println(sent);
			System.out.println(output);
			
			assertEquals(output,sent);
			
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
	        	if (line.trim().isEmpty()==false) {
	        		if (stringBuilder.toString().equalsIgnoreCase("")==false) {
	        			stringBuilder.append(ls);
	        		}
		            stringBuilder.append(line);
	        	}
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}
}
