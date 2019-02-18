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

/*
 *@Jyothi Katuri 
 */

public class AccessionNumberFileterTest {

	private static final String ACCESSION = "automata/acc181210.mwt";


	@Test
	public void testBlackList() {
		String input =  "<SENT sid=\"1\"><plain>Recently, protein levels of important glycosylation enzymes, L8H4F8, B3GNT8 and MGAT4A, were found decreased in the prefrontal cortex in schizophrenia (12 control pairs),33 whereas in our study B3GNT1, B3GNT3 and MGAT1 transcripts were downregulated in schizophrenia cases (Supplementary Table S1). </plain></SENT>";
		String output = "<SENT sid=\"1\"><plain>Recently, protein levels of important glycosylation enzymes, <z:acc db=\"uniprot\" ids=\"L8H4F8\">L8H4F8</z:acc>, B3GNT8 and MGAT4A, were found decreased in the prefrontal cortex in schizophrenia (12 control pairs),33 whereas in our study B3GNT1, B3GNT3 and MGAT1 transcripts were downregulated in schizophrenia cases (Supplementary Table S1). </plain></SENT>";
		testAccessionNumberFilter(input,output);
	}
	
	
	@Test
	public void testExcludeSections() {
			String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>Recently, protein levels of important glycosylation enzymes, L8H4F8 and MGAT4A, were found decreased in the prefrontal cortex in schizophrenia (12 control pairs),33 whereas in our study B3GNT1, B3GNT3 and MGAT1 transcripts were downregulated in schizophrenia cases (Supplementary Table S1). </plain></SENT></SecTag>"+
					"<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>Recently, protein levels of important glycosylation enzymes, L8H4F8 and MGAT4A, were found decreased in the prefrontal cortex in schizophrenia (12 control pairs),33 whereas in our study B3GNT1, B3GNT3 and MGAT1 transcripts were downregulated in schizophrenia cases (Supplementary Table S1). </plain></SENT></SecTag>";
			String outputExpected = "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>Recently, protein levels of important glycosylation enzymes, L8H4F8 and MGAT4A, were found decreased in the prefrontal cortex in schizophrenia (12 control pairs),33 whereas in our study B3GNT1, B3GNT3 and MGAT1 transcripts were downregulated in schizophrenia cases (Supplementary Table S1). </plain></SENT></SecTag>"+
					"<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>Recently, protein levels of important glycosylation enzymes, <z:acc db=\"uniprot\" ids=\"L8H4F8\">L8H4F8</z:acc> and MGAT4A, were found decreased in the prefrontal cortex in schizophrenia (12 control pairs),33 whereas in our study B3GNT1, B3GNT3 and MGAT1 transcripts were downregulated in schizophrenia cases (Supplementary Table S1). </plain></SENT></SecTag>";
				testAccessionNumberFilter(input,outputExpected);
				
				
				input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>Recently, disease 103580 was downregulated</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS,REF\"><SENT sid=\"1\"><plain>Recently, disease 103580 was downregulated</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS,CONCL\"><SENT sid=\"1\"><plain>Recently, disease 103580 was downregulated</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS\"><SENT sid=\"1\"><plain>Recently, disease 103580 was downregulated</plain></SENT></SecTag>"+
						"<SENT sid=\"1\"><plain>Recently, disease 103580 was downregulated</plain></SENT>";
				outputExpected = "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>Recently, disease 103580 was downregulated</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS,REF\"><SENT sid=\"1\"><plain>Recently, disease 103580 was downregulated</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS,CONCL\"><SENT sid=\"1\"><plain>Recently, disease <z:acc db=\"omim\" ids=\"103580\">103580</z:acc> was downregulated</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS\"><SENT sid=\"1\"><plain>Recently, disease <z:acc db=\"omim\" ids=\"103580\">103580</z:acc> was downregulated</plain></SENT></SecTag>"+
						"<SENT sid=\"1\"><plain>Recently, disease <z:acc db=\"omim\" ids=\"103580\">103580</z:acc> was downregulated</plain></SENT>";
				testAccessionNumberFilter(input,outputExpected);
				
				
				input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>allele rs3852144 is ok</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS,REF\"><SENT sid=\"1\"><plain>allele rs3852144 is ok</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS,CONCL\"><SENT sid=\"1\"><plain>allele rs3852144 is ok</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS\"><SENT sid=\"1\"><plain>allele rs3852144 is ok</plain></SENT></SecTag>"+
						"<SENT sid=\"1\"><plain>allele rs1 is ok</plain></SENT>";
				 outputExpected = "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>allele rs3852144 is ok</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS,REF\"><SENT sid=\"1\"><plain>allele rs3852144 is ok</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS,CONCL\"><SENT sid=\"1\"><plain>allele <z:acc db=\"refsnp\" ids=\"rs3852144\">rs3852144</z:acc> is ok</plain></SENT></SecTag>"+
						"<SecTag type=\"ABS\"><SENT sid=\"1\"><plain>allele <z:acc db=\"refsnp\" ids=\"rs3852144\">rs3852144</z:acc> is ok</plain></SENT></SecTag>"+
						"<SENT sid=\"1\"><plain>allele <z:acc db=\"eva\" ids=\"rs1\">rs1</z:acc> is ok</plain></SENT>";
				testAccessionNumberFilter(input,outputExpected);
				
	}
	
	@Test
	public void testDoi() {
		String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>  repository 10.6084/m9.figshare.7312310.v2 </plain></SENT><SENT sid=\"1\"><plain> no tag 10.6084/m9.figshare.7312310.v2 </plain></SENT><SENT sid=\"3\"><plain> repository 10.1016/m9.figshare.7312310.v2 </plain></SENT></SecTag>";
		String outputExpected =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>  repository <z:acc db=\"doi\" ids=\"10.6084/m9.figshare.7312310.v2\">10.6084/m9.figshare.7312310.v2</z:acc> </plain></SENT><SENT sid=\"1\"><plain> no tag 10.6084/m9.figshare.7312310.v2 </plain></SENT><SENT sid=\"3\"><plain> repository 10.1016/m9.figshare.7312310.v2 </plain></SENT></SecTag>";
		testAccessionNumberFilter(input,outputExpected);
	}
	
	@Test 
	public void testEva() {
		String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>archive rs1</plain></SENT><SENT sid=\"2\"><plain>no tagging rs1</plain></SENT></SecTag>"+
				        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>archive rs1</plain></SENT><SENT sid=\"2\"><plain>no tagging rs1</plain></SENT></SecTag>"+
				        "<SENT sid=\"1\"><plain>archive rs1</plain></SENT><SENT sid=\"2\"><plain>no tagging rs1</plain></SENT>";
		String outputExpected = "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>archive rs1</plain></SENT><SENT sid=\"2\"><plain>no tagging rs1</plain></SENT></SecTag>"+
		        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>archive <z:acc db=\"eva\" ids=\"rs1\">rs1</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging rs1</plain></SENT></SecTag>"+
		        "<SENT sid=\"1\"><plain>archive <z:acc db=\"eva\" ids=\"rs1\">rs1</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging rs1</plain></SENT>";		
		testAccessionNumberFilter(input,outputExpected);
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
