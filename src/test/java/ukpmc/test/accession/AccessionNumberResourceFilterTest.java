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

public class AccessionNumberResourceFilterTest {

	private static final String ACCESSION = "automata/acc181210.mwt";
	private static final String RESOURCE = "automata/resources180904.mwt";
	
	@Test
	public void testMissingAccession() {
		this.testAccessionNumberFilterByFile("C:\\Users\\ftalo\\Desktop\\test_wrong_ref.xml", "");
	}
	

	private void testAccessionNumberResourceFilter(String input, String output) {

		try {
			File testFile = new File("text.txt");
			File testFileFinal = new File("text_final.txt");
			String patternmapped =  patternMatch(input, ACCESSION);
			
			patternmapped =  patternMatch(patternmapped, RESOURCE);
			
			InputStream in = new ByteArrayInputStream(patternmapped.getBytes("UTF-8"));
			PrintStream outpw = new PrintStream(new FileOutputStream(testFile));
			AnnotationFilter annotationFilter = new AnnotationFilter(in,outpw, true);
			
			annotationFilter.run();		
			
			outpw = new PrintStream(new FileOutputStream(testFileFinal));
			annotationFilter = new AnnotationFilter(new FileInputStream(testFile),outpw, false);
		    annotationFilter.run();		
			
			String sent = getFileContent(new FileInputStream(testFileFinal));
			
			if ("".equalsIgnoreCase(output)==false) {
				assertEquals(output,sent);
			}else {
				assertTrue(sent.contains("wsize")==false);
			}
			
		} catch (IOException e) {
			System.err.println( e);
		}
	}
	


	private String patternMatch(String text, String dict) {
		String  sent =  null;
		try {

			Reader rin = new FileReader(dict);
			DictFilter df = new DictFilter(rin, "elem", "plain", false);
			df.setInputEncoding("UTF-8");
			df.setOutputEncoding("UTF-8");
			InputStream in = new ByteArrayInputStream(text.getBytes("UTF-8"));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Service svc = df.createService(in, out, null);
			svc.run();
			sent = out.toString("UTF-8");
			//System.out.println(sent);

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
	
	
	private void testAccessionNumberFilterByFile(String filePath, String output) {
		try {
			String input = getFileContent(new FileInputStream(new File(filePath)));
			this.testAccessionNumberResourceFilter(input, output);
			
		} catch (IOException e) {
			System.err.println( e);
		}
	}
}
