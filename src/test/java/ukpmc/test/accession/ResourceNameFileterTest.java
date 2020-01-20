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
	public void testExcludeSections() {
			String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with an image from EMPIAR</plain></SENT><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with no context from EMPIAR</plain></SENT></SecTag>"+
					 "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with an image from EMPIAR</plain></SENT><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with no context from EMPIAR</plain></SENT></SecTag>"+
					 "<SENT sid=\"1\"><plain> Resource name Protein Databank toghether with an image from EMPIAR</plain></SENT><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with no context from EMPIAR</plain></SENT>";
					
			String outputExpected = "<SecTag type=\"REF\"><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with an image from EMPIAR</plain></SENT><SENT sid=\"1\"><plain> Resource name Protein Databank toghether with no context from EMPIAR</plain></SENT></SecTag>"+
					 "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain> Resource name <z:acc db=\"Rpdb\" ids=\"Protein Databank\">Protein Databank</z:acc> toghether with an image from <z:acc db=\"Rempiar\" ids=\"EMPIAR\">EMPIAR</z:acc></plain></SENT><SENT sid=\"1\"><plain> Resource name <z:acc db=\"Rpdb\" ids=\"Protein Databank\">Protein Databank</z:acc> toghether with no context from EMPIAR</plain></SENT></SecTag>"+
					 "<SENT sid=\"1\"><plain> Resource name <z:acc db=\"Rpdb\" ids=\"Protein Databank\">Protein Databank</z:acc> toghether with an image from <z:acc db=\"Rempiar\" ids=\"EMPIAR\">EMPIAR</z:acc></plain></SENT><SENT sid=\"1\"><plain> Resource name <z:acc db=\"Rpdb\" ids=\"Protein Databank\">Protein Databank</z:acc> toghether with no context from EMPIAR</plain></SENT>";
			testResourceNameFilter(input,outputExpected);
		
	}

	@Test 
	public void testEva() {	
		testDBTagging("Reva","EVA","archive");
	}
	
	@Test 
	public void testArrayExpress() {	
		testDBTagging("Rarrayexpress","ArrayExpress","");
		testDBTagging("Rarrayexpress","Array Express","");
	}
	
	@Test 
	public void testBioModels() {	
		testDBTagging("Rbiomodels","BioModels","");
	}
	
	@Test 
	public void testBioSamples() {	
		testDBTagging("Rbiosamples","BioSamples","");
		testDBTagging("Rbiosamples","Biosamples","");
		testDBTagging("Rbiosamples","Biosd","");
		testDBTagging("Rbiosamples","BioSD","");
	}
	
	@Test 
	public void testBioStudies() {	
		testDBTagging("Rbiostudies","BioStudies","");
	}
	
	@Test 
	public void testCath() {	
		testDBTagging("Rcath","CATH","");
		testDBTagging("Rcath","Cath","");
		testDBTagging("Rcath","CATH Gene3D","");
		testDBTagging("Rcath","Cath Gene3D","");
		testDBTagging("Rcath","cath gene3D","");
		testDBTagging("Rcath","CATH-Gene3D","");
		testDBTagging("Rcath","Cath-Gene3D","");
		testDBTagging("Rcath","C.A.T.H","");
	}
	
	@Test 
	public void testChebi() {	
		testDBTagging("Rchebi","ChEBI","");
		testDBTagging("Rchebi","Chemical Entities of Biological Interest","");
	}
	
	@Test 
	public void testChembl() {	
		testDBTagging("Rchembl","ChEMBL","");
		testDBTagging("Rchembl","UniChem","");
	}
	
	@Test 
	public void testComplexPortal() {	
		testDBTagging("Rcomplexportal","Complex Portal","");
	}
	
	@Test 
	public void testMetagenomics() {	
		testDBTagging("Rmetagenomics","EBI Metagenomics","");
	}
	
	@Test 
	public void testEfo() {	
		testDBTagging("Refo","Experimental Factor Ontology","");
		testDBTagging("Refo","EFO","experimental");
	}
	
	@Test 
	public void testEga() {	
		testDBTagging("Rega","European Genome-phenome Archive","");
		testDBTagging("Rega","EGA","genome");
	}
	
	@Test 
	public void testEmdb() {	
		testDBTagging("Remdb","EMDataBank","");
		testDBTagging("Remdb","Electron Microscopy Data Bank","");
	}
	
	@Test 
	public void testEmpiar() {	
		testDBTagging("Rempiar","Electron Microscopy Public Image Archive","");
		testDBTagging("Rempiar","EMPIAR","image");
	}
	
	@Test 
	public void testEna() {	
		testDBTagging("Rena","European Nucleotide Archive","");
		testDBTagging("Rena","EMBL Nucleotide Sequence Database","");
		testDBTagging("Rena","ENA","nucleotide");
	}
	
	@Test 
	public void testEnsembl() {	
		testDBTagging("Rensembl","Ensembl","");
	}
	
	@Test 
	public void testEnsemblGenomes() {	
		testDBTagging("Rensemblgenomes","Ensembl Genomes","");
		testDBTagging("Rensemblgenomes","Ensembl genomes","");
		testDBTagging("Rensemblgenomes","Ensembl Bacteria","");
		testDBTagging("Rensemblgenomes","Ensembl bacteria","");
		testDBTagging("Rensemblgenomes","Ensembl Fungi","");
		testDBTagging("Rensemblgenomes","Ensembl fungi","");
		testDBTagging("Rensemblgenomes","Ensembl Metazoa","");
		testDBTagging("Rensemblgenomes","Ensembl metazoa","");
		testDBTagging("Rensemblgenomes","Ensembl Plants","");
		testDBTagging("Rensemblgenomes","Ensembl plants","");
		testDBTagging("Rensemblgenomes","Ensembl Protists","");
		testDBTagging("Rensemblgenomes","Ensembl protists","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes Bacteria","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes bacteria","");
		testDBTagging("Rensemblgenomes","Ensembl genomes Bacteria","");
		testDBTagging("Rensemblgenomes","Ensembl genomes bacteria","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes Fungi","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes fungi","");
		testDBTagging("Rensemblgenomes","Ensembl genomes Fungi","");
		testDBTagging("Rensemblgenomes","Ensembl genomes fungi","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes Metazoa","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes metazoa","");
		testDBTagging("Rensemblgenomes","Ensembl genomes Metazoa","");
		testDBTagging("Rensemblgenomes","Ensembl genomes metazoa","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes Plants","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes plants","");
		testDBTagging("Rensemblgenomes","Ensembl genomes Plants","");
		testDBTagging("Rensemblgenomes","Ensembl genomes plants","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes Protists","");
		testDBTagging("Rensemblgenomes","Ensembl Genomes protists","");
		testDBTagging("Rensemblgenomes","Ensembl genomes Protists","");
		testDBTagging("Rensemblgenomes","Ensembl genomes protists","");
	}
	
	@Test 
	public void testEnzymePortal() {	
		testDBTagging("Renzymeportal","Enzyme Portal","");
	}
	
	@Test 
	public void testEpmc() {	
		testDBTagging("Repmc","EuropePMC","");
		testDBTagging("Repmc","Europe PMC","");
		testDBTagging("Repmc","Europe PubMed Central","");
		testDBTagging("Repmc","europepmc.org","");
	}
	
	@Test 
	public void testExpressionAtlas() {	
		testDBTagging("Rgxa","Expression Atlas","");
	}
	
	@Test 
	public void testGo() {	
		testDBTagging("Rgo","Gene Ontology","");
	}
	
	@Test 
	public void testGwas() {	
		testDBTagging("Rgwas","GWAS Catalog","");
	}
	
	@Test 
	public void testHGNC() {	
		testDBTagging("Rhgnc","HGNC","hugo");
		testDBTagging("Rhgnc","HUGO Gene Nomenclature Committee","");
	}
	
	@Test 
	public void testHPA() {	
		testDBTagging("Rhpa","Human Protein Atlas","");
		testDBTagging("Rhpa","Human Protein atlas","");
		testDBTagging("Rhpa","Human protein Atlas","");
		testDBTagging("Rhpa","Human protein atlas","");
		testDBTagging("Rhpa","human Protein Atlas","");
		testDBTagging("Rhpa","human Protein atlas","");
		testDBTagging("Rhpa","human protein Atlas","");
		testDBTagging("Rhpa","human protein atlas","");
		testDBTagging("Rhpa","HPA","human");
		testDBTagging("Rhpa","hpa","atlas");
		testDBTagging("Rhpa","Proteinatlas","");
		testDBTagging("Rhpa","proteinatlas","");
		testDBTagging("Rhpa","proteinatlas.org","");
		testDBTagging("Rhpa","Protein Atlas","");
		testDBTagging("Rhpa","Protein atlas","");
		testDBTagging("Rhpa","protein Atlas","");
		testDBTagging("Rhpa","protein atlas","");
	}
	
	@Test 
	public void testIdentifiers() {	
		testDBTagging("Ridentifiers","identifiers.org","");
	}
	
	@Test 
	public void testIGSR() {	
		testDBTagging("Rigsr","International Genome Sample Resource","");
		testDBTagging("Rigsr","1000 Genomes","");
	}
	
	@Test 
	public void testIntact() {	
		testDBTagging("Rintact","IntAct","");
		testDBTagging("Rintact","IntAct database","");
		testDBTagging("Rintact","Intact database","");
		testDBTagging("Rintact","intAct database","");
		testDBTagging("Rintact","intact database","");
	}
	
	@Test 
	public void testMint() {	
		testDBTagging("Rmint","Molecular INTeraction Database","");
		testDBTagging("Rmint","Molecular INTeraction database","");
		testDBTagging("Rmint","Molecular INteraction Database","");
		testDBTagging("Rmint","Molecular INteraction database","");
		testDBTagging("Rmint","Molecular InTeraction Database","");
		testDBTagging("Rmint","Molecular InTeraction database","");
		testDBTagging("Rmint","Molecular Interaction Database","");
		testDBTagging("Rmint","Molecular Interaction database","");
		testDBTagging("Rmint","Molecular iNTeraction Database","");
		testDBTagging("Rmint","Molecular iNTeraction database","");
		testDBTagging("Rmint","Molecular iNteraction Database","");
		testDBTagging("Rmint","Molecular iNteraction database","");
		testDBTagging("Rmint","Molecular inTeraction Database","");
		testDBTagging("Rmint","Molecular inTeraction database","");
		testDBTagging("Rmint","Molecular interaction Database","");
		testDBTagging("Rmint","Molecular interaction database","");
		testDBTagging("Rmint","molecular INTeraction Database","");
		testDBTagging("Rmint","molecular INTeraction database","");
		testDBTagging("Rmint","molecular INteraction Database","");
		testDBTagging("Rmint","molecular INteraction database","");
		testDBTagging("Rmint","molecular InTeraction Database","");
		testDBTagging("Rmint","molecular InTeraction database","");
		testDBTagging("Rmint","molecular Interaction Database","");
		testDBTagging("Rmint","molecular Interaction database","");
		testDBTagging("Rmint","molecular iNTeraction Database","");
		testDBTagging("Rmint","molecular iNTeraction database","");
		testDBTagging("Rmint","molecular iNteraction Database","");
		testDBTagging("Rmint","molecular iNteraction database","");
		testDBTagging("Rmint","molecular inTeraction Database","");
		testDBTagging("Rmint","molecular inTeraction database","");
		testDBTagging("Rmint","molecular interaction Database","");
		testDBTagging("Rmint","molecular interaction database","");
		testDBTagging("Rmint","MINT","interaction");
		testDBTagging("Rmint","MINt","database");
		testDBTagging("Rmint","MInT","interaction");
		testDBTagging("Rmint","MInt","database");
		testDBTagging("Rmint","MiNT","interaction");
		testDBTagging("Rmint","MiNt","database");
		testDBTagging("Rmint","MinT","interaction");
		testDBTagging("Rmint","Mint","database");
		testDBTagging("Rmint","mINT","interaction");
		testDBTagging("Rmint","mINt","database");
		testDBTagging("Rmint","mInT","interaction");
		testDBTagging("Rmint","mInt","database");
		testDBTagging("Rmint","miNT","interaction");
		testDBTagging("Rmint","miNt","database");
		testDBTagging("Rmint","minT","interaction");
		testDBTagging("Rmint","mint","database");
	}
	
	@Test 
	public void testIntenz() {	
		testDBTagging("Rintenz","IntEnz","Enzyme");
		testDBTagging("Rintenz","Integrated relational Enzyme database","");
	}
	
	@Test 
	public void testInterpro() {	
		testDBTagging("Rinterpro","InterPro","");
		testDBTagging("Rinterpro","InterProScan","");
	}
	
	@Test 
	public void testMetabolights() {	
		testDBTagging("Rmetabolights","MetaboLights","");
	}
	
	@Test 
	public void testOLS() {	
		testDBTagging("Rols","Ontology Lookup Service","");
		testDBTagging("Rols","OLS","Ontology Lookup");
	}
	
	@Test 
	public void testPDB() {	
		testDBTagging("Rpdb","PDB","data");
		testDBTagging("Rpdb","PDBe","bank");
		testDBTagging("Rpdb","Protein Databank","");
		testDBTagging("Rpdb","Protein DataBank","");
		testDBTagging("Rpdb","Protein Data Bank","");
		testDBTagging("Rpdb","Protein Databank in Europe","");
		testDBTagging("Rpdb","Protein DataBank in Europe","");
		testDBTagging("Rpdb","Protein Data Bank in Europe","");
		testDBTagging("Rpdb","RCSB PDB","");
	}
	
	@Test 
	public void testPfam() {	
		testDBTagging("Rpfam","Pfam","family");
		testDBTagging("Rpfam","Protein Family Database","");
	}
	
	@Test 
	public void testPride() {	
		testDBTagging("Rpride","PRIDE","identifications");
		testDBTagging("Rpride","PRoteomics IDEntifications database","");
	}
	
	@Test 
	public void testReactome() {	
		testDBTagging("Rreactome","Reactome","");
	}
	
	@Test 
	public void testRfam() {	
		testDBTagging("Rrfam","Rfam","families");
	}
	
	@Test 
	public void testRNACentral() {	
		testDBTagging("Rrnacentral","RNAcentral","");
	}
	
	@Test 
	public void testStringDB() {	
		testDBTagging("Rstringdb","STRING","interaction");
		testDBTagging("Rstringdb","STRING-DB","");
		testDBTagging("Rstringdb","string-db","");
		testDBTagging("Rstringdb","String-DB","");
		testDBTagging("Rstringdb","String-db","");
		testDBTagging("Rstringdb","String DB","");
		testDBTagging("Rstringdb","string db","");
		testDBTagging("Rstringdb","string DB","");
		testDBTagging("Rstringdb","string DB","");
		testDBTagging("Rstringdb","STRING-Database","");
		testDBTagging("Rstringdb","STRING-database","");
		testDBTagging("Rstringdb","STRING Database","");
		testDBTagging("Rstringdb","String Database","");
		testDBTagging("Rstringdb","string Database","");
		testDBTagging("Rstringdb","STRING database","");
		testDBTagging("Rstringdb","String database","");
		testDBTagging("Rstringdb","string database","");
		testDBTagging("Rstringdb","STRING Interaction Database","");
		testDBTagging("Rstringdb","String Interaction Database","");
		testDBTagging("Rstringdb","string Interaction Database","");
		testDBTagging("Rstringdb","STRING Interaction database","");
		testDBTagging("Rstringdb","String Interaction database","");
		testDBTagging("Rstringdb","string Interaction database","");
		testDBTagging("Rstringdb","STRING interaction Database","");
		testDBTagging("Rstringdb","String interaction Database","");
		testDBTagging("Rstringdb","string interaction Database","");
		testDBTagging("Rstringdb","STRING interaction database","");
		testDBTagging("Rstringdb","String interaction database","");
		testDBTagging("Rstringdb","string interaction database","");
		testDBTagging("Rstringdb","STRING Network","");
		testDBTagging("Rstringdb","String Network","");
		testDBTagging("Rstringdb","string Network","");
		testDBTagging("Rstringdb","STRING network","");
		testDBTagging("Rstringdb","String network","");
		testDBTagging("Rstringdb","string network","");
		testDBTagging("Rstringdb","database STRING","");
		testDBTagging("Rstringdb","database String","");
		testDBTagging("Rstringdb","database string","");
		testDBTagging("Rstringdb","https://string-db.org","");
		testDBTagging("Rstringdb","http://string-db.org","");
		testDBTagging("Rstringdb","string-db.org","");
		testDBTagging("Rstringdb","string.embl.de","");
		testDBTagging("Rstringdb","database(STRING)","");
		testDBTagging("Rstringdb","database(String)","");
		testDBTagging("Rstringdb","database(string)","");
		testDBTagging("Rstringdb","Database(STRING)","");
		testDBTagging("Rstringdb","Database(String)","");
		testDBTagging("Rstringdb","Database(string)","");
	}
	
	@Test 
	public void testSureChembl() {	
		testDBTagging("Rsurechembl","SureChEMBL","");
	}
	
	@Test 
	public void testUniprot() {	
		testDBTagging("Runiprot","UniProt","");
		testDBTagging("Runiprot","Uniprot","");
		testDBTagging("Runiprot","uniprot","");
		testDBTagging("Runiprot","uniProt","");
		testDBTagging("Runiprot","UNIPROT","");
		testDBTagging("Runiprot","Uniref","");
		testDBTagging("Runiprot","UniRef","");
		testDBTagging("Runiprot","uniref","");
		testDBTagging("Runiprot","uniRef","");
		testDBTagging("Runiprot","UNIREF","");
		testDBTagging("Runiprot","Uniparc","");
		testDBTagging("Runiprot","UniParc","");
		testDBTagging("Runiprot","uniParc","");
		testDBTagging("Runiprot","uniparc","");
		testDBTagging("Runiprot","UNIPARC","");
		testDBTagging("Runiprot","UNIPaRC","");
		testDBTagging("Runiprot","Trembl","");
		testDBTagging("Runiprot","trembl","");
		testDBTagging("Runiprot","TREMBL","");
		testDBTagging("Runiprot","TrEMBL","");
		testDBTagging("Runiprot","TrEmbl","");
		testDBTagging("Runiprot","UniProtKB","");
		testDBTagging("Runiprot","Uniprotkb","");
		testDBTagging("Runiprot","UniprotKB","");
		testDBTagging("Runiprot","UNIPROTKB","");
		testDBTagging("Runiprot","uniProtKB","");
		testDBTagging("Runiprot","uniprotkb","");
		testDBTagging("Runiprot","uniprotKB","");
		testDBTagging("Runiprot","UNIPROTKB","");
		testDBTagging("Runiprot","SwissProt","");
		testDBTagging("Runiprot","swissProt","");
		testDBTagging("Runiprot","swissprot","");
		testDBTagging("Runiprot","Swissprot","");
		testDBTagging("Runiprot","SWISSPROT","");
		testDBTagging("Runiprot","Swiss-Prot","");
		testDBTagging("Runiprot","Swiss-prot","");
		testDBTagging("Runiprot","swiss-Prot","");
		testDBTagging("Runiprot","swiss-prot","");
		testDBTagging("Runiprot","SWISS-PROT","");
	}
	
	@Test 
	public void testVectorBase() {	
		testDBTagging("Rvectorbase","VectorBase","");
	}
	
	@Test 
	public void testWormBase() {	
		testDBTagging("Rwormbase","WormBase","");
	}
	
	
	private void testDBTagging(String dbName, String validId, String context) {
		String input =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
		        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
		        "<SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT>";
		
		String outputExpected="";
		boolean contextChecked="".equalsIgnoreCase(context)==false;
		if (contextChecked) {

			outputExpected =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
		        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>"+context+" <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
		        "<SENT sid=\"1\"><plain>"+context+" <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT>";
		}else {
			outputExpected =  "<SecTag type=\"REF\"><SENT sid=\"1\"><plain>"+context+" "+validId+"</plain></SENT><SENT sid=\"2\"><plain>no tagging "+validId+"</plain></SENT></SecTag>"+
			        "<SecTag type=\"INTRO\"><SENT sid=\"1\"><plain>"+context+" <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT></SecTag>"+
			        "<SENT sid=\"1\"><plain>"+context+" <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT><SENT sid=\"2\"><plain>no tagging <z:acc db=\""+dbName+"\" ids=\""+validId+"\">"+validId+"</z:acc></plain></SENT>";
		}
		testResourceNameFilter(input,outputExpected);
	}
	

	private void testResourceNameFilter(String input, String output) {

		try {
			File testFile = new File("text.txt");
			File testFileFinal = new File("text_final.txt");
			String patternmapped =  patternMatch(input, RESOURCE_DICTIONARY);
			
			InputStream in = new ByteArrayInputStream(patternmapped.getBytes("UTF-8"));
			PrintStream outpw = new PrintStream(new FileOutputStream(testFile));
			AnnotationFilter annotationFilter = new AnnotationFilter(in,outpw, true);
			
			annotationFilter.run();		
			
			outpw = new PrintStream(new FileOutputStream(testFileFinal));
			annotationFilter = new AnnotationFilter(new FileInputStream(testFile),outpw, false);
		    annotationFilter.run();		
			
			String sent = getFileContent(new FileInputStream(testFileFinal));
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
}
