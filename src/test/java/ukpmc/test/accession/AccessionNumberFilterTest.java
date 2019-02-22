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

public class AccessionNumberFilterTest {

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
						"<SENT sid=\"1\"><plain>allele <z:acc db=\"refsnp\" ids=\"rs1\">rs1</z:acc> is ok</plain></SENT>";
				testAccessionNumberFilter(input,outputExpected);
				
				
	}
	
	@Test
	public void testDoi() {
		String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>  repository 10.6084/m9.figshare.7312310.v2 </plain></SENT><SENT sid=\"1\"><plain> no tag 10.6084/m9.figshare.7312310.v2 </plain></SENT><SENT sid=\"3\"><plain> repository 10.1016/m9.figshare.7312310.v2 </plain></SENT></SecTag>";
		String outputExpected =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>  repository <z:acc db=\"doi\" ids=\"10.6084/m9.figshare.7312310.v2\">10.6084/m9.figshare.7312310.v2</z:acc> </plain></SENT><SENT sid=\"1\"><plain> no tag 10.6084/m9.figshare.7312310.v2 </plain></SENT><SENT sid=\"3\"><plain> repository 10.1016/m9.figshare.7312310.v2 </plain></SENT></SecTag>";
		testAccessionNumberFilter(input,outputExpected);
	}
	
	/**@Test 
	public void testEva() {
		testDBTagging("eva","rs1","ss0","archive");
	}*/
	
	@Test
	public void testArrayExpress() {
		testDBTagging("arrayexpress","E-MTAB-6937","E-MTAB-0000","accession");
	}
	
	@Test
	public void testBioModels() {
		testDBTagging("biomodels","BIOMD0000000144","BMID123456789123","biomodels");
	}
	
	@Test
	public void testBioSamples() {
		testDBTagging("biosample","SAMN06251630","SAMD09010125","biosample");	
	    testDBTagging("biosample","SAMEA2397676","SAMD09010125","biosample");	
		testDBTagging("biosample","SAMD00005257","SAMD09010125","biosample");	
		
	}
	
	@Test
	public void testBioStudies() {
		testDBTagging("biostudies","S-EPMC2485412","S-EPMC0000000","");
	}
	
	@Test
	public void testCath() {
		testDBTagging("cath","3.20.20.150","3.00.000.00","cath");
	}
	
	@Test
	public void testChebi() {
		testDBTagging("chebi","CHEBI:37820","CHEBI:3782011111","compound");
	}
	
	@Test
	public void testChembl() {
		testDBTagging("chembl","CHEMBL3770443","CHEMBL3782011111","chembl");
	}
	
	@Test
	public void testComplexportal() {
		testDBTagging("complexportal","CPX-2158","CPX-000","complex");
	}
	
	@Test
	public void testMetagenomics() {
		testDBTagging("metagenomics","SRS259434","SRS000001","metagenomics");
	}
	
	@Test
	public void testEfo() {
		testDBTagging("efo","EFO_0001185","EFO_0000000","");
		testDBTagging("efo","EFO:0001185","EFO_0000000","");
	}
	
	@Test
	public void testEga() {
		testDBTagging("ega.study","EGAS00000000132","EGAS00000000000","ega");
		testDBTagging("ega.dataset","EGAD00000000001","EGAD00000000000","validation sets");
		testDBTagging("ega.dac","EGAC00000000001","EGAC00000000000","accession");
		testDBTagging("ega.study","EGAS00000000083","EGAC00000000000","accession");
		testDBTagging("ega.dataset","EGAD00000000021","EGAC00000000000","accession");
		
	}
	
	@Test
	public void testEmdb() {
		testDBTagging("emdb","EMD-6393","EMD-0000","emdb");
	}
	
	@Test
	public void testEmpiar() {
		testDBTagging("empiar","EMPIAR-10045","EMPIAR-00000","code");
	}
	
	@Test
	public void testGen() {
		  
		testDBTagging("gen","L16912","L00000","ena");
		testDBTagging("gen","MF033306","MF000000","genbank");
		testDBTagging("gen","SRX658505","SRX000000","experiment");
		testDBTagging("gen","ERP108822","ERP000000","study");
		testDBTagging("gen","ERS1545050","ERS0000000","sample");
		testDBTagging("gen","AAR04849","AAR00000","sequence");
		testDBTagging("gen","SRR3998997","SRR0000000","run");
		testDBTagging("gen","SRA389707","SRA000000","submission");
		testDBTagging("gen","CZSB02000000","CZSB00000000","assembled");
		testDBTagging("gen","ERZ486920","ERZ000000","analysis");
		//testDBTagging("gen","TI34678","ERZ000000","trace");	
	}
	
	@Test
	public void testEnsembl() {
		testDBTagging("ensembl","ENSBTAG00000016573","ENSBTAG00000000000","ensembl");
		testDBTagging("ensembl","ENSG00000139618","ENSBTAG00000000000","ensembl");
		testDBTagging("ensembl","ENST00000252723","ENSBTAG00000000000","ensembl");
		testDBTagging("ensembl","ENSBTAG00000021523","ENSBTAG00000000000","ensembl");	                     
	}
	
	@Test
	public void testGo() {
		testDBTagging("go","GO:0015031","GO:0000000","gene ontology");
	}
	
	@Test
	public void testHgnc() {
		testDBTagging("hgnc","HGNC:10971","HGNC:00000","hugo");
	}
	
	@Test
	public void testHpa() {
		testDBTagging("hpa","HPA007415","HPA000000","");
		testDBTagging("hpa","CAB016307","CAB000000","");
	}
	
	@Test
	public void testIgsr() {
		testDBTagging("igsr","GM01869","","donor");
		testDBTagging("igsr","NA01869","","iPSCs");
		testDBTagging("igsr","HG01111","","lymphoblastoid");
	}
	
	@Test
	public void testIntact() {
		testDBTagging("intact","EBI-11165886","EBI-00000000","interaction");
	}
	
	@Test
	public void testMint() {
		testDBTagging("mint","IM-24178","IM-00000","interactions");
		testDBTagging("mint","MINT-7905142","MINT-0000000","interactions");
	}
	
	@Test
	public void testInterpro() {
		testDBTagging("interpro","IPR017667","IPR000000","interpro");
		testDBTagging("interpro","IPr017667","IPR000000","family");
		testDBTagging("interpro","IpR017667","IPR000000","domain");
		testDBTagging("interpro","Ipr017667","IPR000000","motif");
		testDBTagging("interpro","iPR017667","IPR000000","accession");
		testDBTagging("interpro","iPr017667","IPR000000","interpro");
		testDBTagging("interpro","ipR017667","IPR000000","interpro");
		testDBTagging("interpro","ipr017667","IPR000000","interpro");
		
	}
	
	@Test
	public void testMetabolights() {
		testDBTagging("metabolights","MTBLS661","MTBLS000","repository");
	}
	
	@Test
	public void testPDB() {
		testDBTagging("pdb","3VI4","5AAA","structure");
	}
	
	@Test
	public void testPfam() {
		testDBTagging("pfam","PF01535","PF00000","family");
	}
	
	@Test
	public void testPride() {
		testDBTagging("pxd","PXD009799","PXD000000","pride");
		//testDBTagging("pxd","RPXD000665","RPXD000000","pride");
	}
	
	@Test
	public void testReactome() {
		testDBTagging("reactome","R-HSA-913531","R-HSA-000000","regulatory");
	}
	
	@Test
	public void testRfam() {
		testDBTagging("rfam","RF01820","RF00000","family");
	}
	
	@Test
	public void testRnacentral() {
		testDBTagging("rnacentral","URS00001DC04F_9606","URS00001DC04F_0000","sequences");
	}
	
	@Test
	public void testUniprot() {
		testDBTagging("uniprot","P29375","P00000","uniprot");
		testDBTagging("uniprot","C4JC09","C0JC00","uniprot");		
	}
	
	@Test
	public void testUniparc() {
		testDBTagging("uniparc","UPI00026E6E87","UPI0000000000","uniprot");
	}
	
	@Test
	public void testEbisc() {
		testDBTagging("ebisc","STBCi044-A","EURACi004-A","cell");
	}
	
	@Test
	public void testHipsci() {
		testDBTagging("hipsci","HPSI0114i-eipl_1","HPSI0000i-eipl_0","cell");
	}
	
	@Test
	public void testOmim() {
		testDBTagging("omim","103580","795043","disease");
	}
	
	@Test
	public void testRefSeq() {
		testDBTagging("refseq","NM_001101.4","XP_748685.1","genbank");
	}
	
	@Test
	public void testRefSnp() {
		testDBTagging("refsnp","rs1041983","rs000000","allele");
		testDBTagging("refsnp","ss1","ss0","model");
	}
	
	@Test
	public void testRrid() {
		testDBTagging("rrid","RRID:SCR_002798","RRID:SCR_000000","");
		testDBTagging("rrid","RRID:AB_2532057","RRID:SCR_000000","");
		testDBTagging("rrid","RRID:CVCL_ST66","RRID:SCR_000000","");
	}
	
	@Test
	public void testBioProject() {
		testDBTagging("bioproject","PRJNA471919","PRJNA000000","bioproject");
	}
	
	@Test
	public void testGca() {
		testDBTagging("gca","GCA_000006335.3","GCA_000000000.0","");
	}
	
	@Test
	public void testTreefam() {
		testDBTagging("treefam","TF332117","TF000000","tree");
	}
	
	@Test
	public void testEudract() {
		testDBTagging("eudract","2013-000260-28","","trial");
	}
	
	@Test
	public void testNct() {
		testDBTagging("nct","NCT01177111","NCT00000000","trial");
	}
	
	@Test
	public void testDbGap() {
		testDBTagging("dbgap","phs000016","phs000000","database of genotypes and phenotypes");
	}
	
	@Test
	public void testGeo() {
		testDBTagging("geo","GSE26680","GPL0000","geo");
		testDBTagging("geo","GPL3213","GPL0000","gene expression omnibus");
		testDBTagging("geo","GSM26680","GPL0000","genome");
		testDBTagging("geo","GDS1234","GPL0000","gene expression omnibus");
		
	}
	
	private void testDBTagging(String dbName, String validId, String wrongId, String context) {
		String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
		        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
		        "<SENT sid=\"1\"><plain>"+context+" "+wrongId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+wrongId+"</plain></SENT>";
		
		String outputExpected="";
		boolean contextChecked="".equalsIgnoreCase(context)==false;
		if (contextChecked) {

			outputExpected =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
		        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>"+context+" <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
		        "<SENT sid=\"1\"><plain>"+context+" "+wrongId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+wrongId+"</plain></SENT>";
		}else {
			outputExpected =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
			        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>"+context+" <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT></SecTag>"+
			        "<SENT sid=\"1\"><plain>"+context+" "+wrongId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+wrongId+"</plain></SENT>";
		}
		testAccessionNumberFilter(input,outputExpected);
	}
	
	@Test
	public void testMissingAccession() {
	
		String input =  "<SecTag type=\"INTRO\"><SENT sid=\"11341\" pm=\".\"><plain><SecTag type=\"INTRO\"><SENT sid=\"11341\" pm=\".\"><plain>Scale bar 1 mm.</p><p><bold>DOI:</bold><ext-link ext-link-type=\"doi\" xlink:href=\"10.7554/eLife.25835.002\">http://dx.doi.org/10.7554/eLife.25835.002</ext-link></p></SecTag>   <!--wrong secTag position-->  </plain></SENT></SecTag><SecTag type=\"INTRO\"><SENT sid=\"11341\" pm=\".\"><plain><SecTag type=\"INTRO\"><SENT sid=\"11341\" pm=\".\"><plain>Scale bar 1 mm.</p><p><bold>DOI:</bold><ext-link ext-link-type=\"doi\" xlink:href=\"10.7554/eLife.25835.002\">http://dx.doi.org/10.7554/eLife.25835.002</ext-link></p></plain></SENT></SecTag> <!--right secTag position--></SecTag>";
		String outputExpected =  "<SecTag type=\"INTRO\"><SENT sid=\"11341\" pm=\".\"><plain><SecTag type=\"INTRO\"><SENT sid=\"11341\" pm=\".\"><plain>Scale bar 1 mm.</p><p><bold>DOI:</bold><ext-link ext-link-type=\"doi\" xlink:href=\"10.7554/eLife.25835.002\">http://dx.doi.org/10.7554/eLife.25835.002</ext-link></p></SecTag>   <!--wrong secTag position-->  </plain></SENT></SecTag><SecTag type=\"INTRO\"><SENT sid=\"11341\" pm=\".\"><plain><SecTag type=\"INTRO\"><SENT sid=\"11341\" pm=\".\"><plain>Scale bar 1 mm.</p><p><bold>DOI:</bold><ext-link ext-link-type=\"doi\" xlink:href=\"10.7554/eLife.25835.002\">http://dx.doi.org/10.7554/eLife.25835.002</ext-link></p></plain></SENT></SecTag> <!--right secTag position--></SecTag>";
		testAccessionNumberFilter(input,outputExpected);
		
	}
	

	private void testAccessionNumberFilter(String input, String output) {

		try {
			File testFile = new File("text.txt");
			File testFileFinal = new File("text_final.txt");
			String patternmapped =  patternMatch(input, ACCESSION);
			
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
	
	
	private void testAccessionNumberFilterByFile(String filePath, String output) {
		try {
			String input = getFileContent(new FileInputStream(new File(filePath)));
			this.testAccessionNumberFilter(input, output);
			
		} catch (IOException e) {
			System.err.println( e);
		}
	}
}
