package ukpmc.test.accession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ukpmc.AccResolver;
import ukpmc.BioStudiesResolver;
import ukpmc.DoiResolver;
import ukpmc.HipsciResolver;
import ukpmc.HpaResolver;
import ukpmc.NcbiResolver;
import ukpmc.ResponseCodeResolver;

public class AccessionNumbersTest {
	
	private AccResolver ar = new AccResolver();
	private NcbiResolver nr = new NcbiResolver();
	private DoiResolver dr = new DoiResolver();
	private BioStudiesResolver bioStudiesr = new BioStudiesResolver();
	private HpaResolver hpar = new HpaResolver();
	private ResponseCodeResolver responseCoder= new ResponseCodeResolver();
	private HipsciResolver hipscir = new HipsciResolver();
	
	@Test
	public void testOnlineValidationPDBe() {
		testAccessionNumberOnlineValidation("pdb","pdbe","3VI4",true);
		testAccessionNumberOnlineValidation("pdb","pdbe","5AAA",false);
		testAccessionNumberOnlineValidation("pdb","pdbe","5JHM",true); //it should be valid (EBI search problem that it is not uptodate with PDB data)
		
	}
	
	@Test
	public void testOnlineValidationArrayExpress() {
		testAccessionNumberOnlineValidation("arrayexpress", "geneExpression","E-MTAB-6937",true);
		testAccessionNumberOnlineValidation("arrayexpress", "geneExpression","E-MTAB-6911",true);
		testAccessionNumberOnlineValidation("arrayexpress", "geneExpression","E-MTAB-0000",false);
		
	}
	
	
	@Test
	public void testOnlineValidationBiomodels() {
		testAccessionNumberOnlineValidation("biomodels","biomodels","BIOMD0000000163",true);
		testAccessionNumberOnlineValidation("biomodels","biomodels","BIOMD0000000107",true);
		testAccessionNumberOnlineValidation("biomodels","biomodels","BIOMD0000000370",true);
		testAccessionNumberOnlineValidation("biomodels","biomodels","BIOMD0000000144",true);
		testAccessionNumberOnlineValidation("biomodels","biomodels","MODEL1612020000",true);
		testAccessionNumberOnlineValidation("biomodels","biomodels","BMID123456789123",false);
		
	}
	
	@Test
	public void testOnlineValidationComplexPortal() {
		
		testAccessionNumberOnlineValidationResponseCode("complexportal","intact-complexes","CPX-2267",true);
		testAccessionNumberOnlineValidationResponseCode("complexportal","intact-complexes","CPX-2158",true);
		testAccessionNumberOnlineValidationResponseCode("complexportal","intact-complexes","CPX-0000",false);
		
	}
	
	@Test
	public void testOnlineValidationBiosamples() {
		testAccessionNumberOnlineValidation("biosample", "biosamples","SAMN06251630",true);
		testAccessionNumberOnlineValidation("biosample", "biosamples","SAMEA2397676",true);
		testAccessionNumberOnlineValidation("biosample", "biosamples","SAMD00005257",true);
		testAccessionNumberOnlineValidation("biosample", "biosamples","SAMD09010125",false);
	}
	
	@Test
	public void testOnlineValidationBiostudies() {
		
		testAccessionNumberOnlineValidationBioStudies("biostudies","biostudies","S-EPMC2485412",true);
		testAccessionNumberOnlineValidationBioStudies("biostudies","biostudies","S-DIXA-012",true);
		testAccessionNumberOnlineValidationBioStudies("biostudies","biostudies","S-EPMC4704494",true);
		testAccessionNumberOnlineValidationBioStudies("biostudies","biostudies","S-EPMC3044716",true);
		testAccessionNumberOnlineValidationBioStudies("biostudies","biostudies","S-EPMC0000000",false);
	}

	@Test
	public void testOnlineValidationCath() {
		testAccessionNumberOnlineValidation("cath","cath","3.20.20.150",true);
		testAccessionNumberOnlineValidation("cath","cath","2.60.120.10",true);
		testAccessionNumberOnlineValidation("cath","cath","3.40.140.10",true);
		testAccessionNumberOnlineValidation("cath","cath","3.00.000.00",false);
		//assertTrue("Cath db validation is missing", false);
	}
	
	@Test
	public void testOnlineValidationChebi() {
		
		
		testAccessionNumberOnlineValidation("chebi","chebi","CHEBI:16587",true);
		testAccessionNumberOnlineValidation("chebi","chebi","CHEBI:6909",true);
		testAccessionNumberOnlineValidation("chebi","chebi","CHEBI:23965",true);
		testAccessionNumberOnlineValidation("chebi","chebi","CHEBI:76227",true);
		testAccessionNumberOnlineValidation("chebi","chebi","CHEBI:37820",true);
		testAccessionNumberOnlineValidation("chebi","chebi","CHEBI:3782011111",false);
	}
	
	@Test
	public void testOnlineValidationChembl() {
		testAccessionNumberOnlineValidation("chembl","chembl-molecule","CHEMBL3770443",true); 
		testAccessionNumberOnlineValidation("chembl","chembl-molecule","CHEMBL53463",true);
		testAccessionNumberOnlineValidation("chembl","chembl-molecule","CHEMBL941",true);
		testAccessionNumberOnlineValidation("chembl","chembl-molecule","CHEMBL3782011111",false);
		testAccessionNumberOnlineValidation("chembl","chembl-molecule","CHEMBL1237042",false);
	}
	
	@Test
	public void testOnlineValidationMetagenomics() {
		testAccessionNumberOnlineValidation("metagenomics","metagenomics_samples","SRS259434",true); 
		testAccessionNumberOnlineValidation("metagenomics","metagenomics_samples","SRS000001",false);
		testAccessionNumberOnlineValidation("metagenomics","metagenomics_samples","SRS355925",false); 
		testAccessionNumberOnlineValidation("metagenomics","metagenomics_samples","SRS355926",false); 
		
	}
	
	@Test
	public void testOnlineValidationEfo() {
		testAccessionNumberOnlineValidation("efo","efo","EFO_0001185",true); 
		testAccessionNumberOnlineValidation("efo","efo","EFO_0000400",true);
		testAccessionNumberOnlineValidation("efo","efo","EFO_0004458",true); 
		testAccessionNumberOnlineValidation("efo","efo","EFO:0001187",true);
		testAccessionNumberOnlineValidation("efo","efo","EFO:0000000",false);
		testAccessionNumberOnlineValidation("efo","efo","EFO_0000000",false);
	}
	
	@Test
	public void testOnlineValidationEga() {
		
		testAccessionNumberOnlineValidation("ega.study","ega","EGAS00000000132",true);
		testAccessionNumberOnlineValidation("ega.study","ega","EGAS00000000000",false);
		testAccessionNumberOnlineValidation("ega.dataset","ega","EGAD00000000001",true);
		testAccessionNumberOnlineValidation("ega.dataset","ega","EGAD00000000000",false);
		testAccessionNumberOnlineValidation("ega.dac","ega","EGAC00000000001",true);
		testAccessionNumberOnlineValidation("ega.dac","ega","EGAC00000000000",false);
	}
	
	@Test
	public void testOnlineValidationEmdb() {
		testAccessionNumberOnlineValidation("emdb","emdb","EMD-6393",true);
		testAccessionNumberOnlineValidation("emdb","emdb","EMD-2984",true);
		testAccessionNumberOnlineValidation("emdb","emdb","EMD-0000",false);
	}
	
	@Test
	public void testOnlineValidationEmpiar() {
		testAccessionNumberOnlineValidationResponseCode("empiar","empiar","EMPIAR-10045",true);
		testAccessionNumberOnlineValidationResponseCode("empiar","empiar","EMPIAR-10090",true);
		testAccessionNumberOnlineValidationResponseCode("empiar","empiar","EMPIAR-10063",true);
		testAccessionNumberOnlineValidationResponseCode("empiar","empiar","EMPIAR-00000",false);
	}
	
	@Test
	public void testOnlineValidationGen() {
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","L16912",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","MF033306",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","SRX658505",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","ERP108822",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","ERS1545050",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","AAR04849",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","SRR3998997",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","SRA389707",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","CZSB02000000",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","ERZ486920",true);
		testAccessionNumberOnlineValidation("gen","nucleotideSequences","MF000000",false);
	}
	
	@Test
	public void testOnlineValidationEnsembl() {
		testAccessionNumberOnlineValidationResponseCode("ensembl","ensemblRoot","ENSSSCT00000014289",true);
		testAccessionNumberOnlineValidationResponseCode("ensembl","ensemblRoot","ENSBTAG00000016573",true);
		testAccessionNumberOnlineValidationResponseCode("ensembl","ensemblRoot","ENSBTAG00000021523",true);
		testAccessionNumberOnlineValidationResponseCode("ensembl","ensemblRoot","ENST00000252723",true);
		testAccessionNumberOnlineValidationResponseCode("ensembl","ensemblRoot","ENSBTAG00000000000",false);
		
	}
	
	/**@Test
	public void testOnlineValidationEva() {
		testAccessionNumberOnlineValidationNcbi("eva","snp","rs3852144",true);
		testAccessionNumberOnlineValidationNcbi("eva","snp","ss1",true);
		testAccessionNumberOnlineValidationNcbi("eva","snp","rs848",true);
		testAccessionNumberOnlineValidationNcbi("eva","snp","rs7412",true); 
		testAccessionNumberOnlineValidationNcbi("eva","snp","ss0",false);
	}*/
	
	@Test
	public void testOnlineValidationGo() {
		testAccessionNumberOnlineValidation("go","go","GO:0015031",true); 
		testAccessionNumberOnlineValidation("go","go","GO:0005524",true);
		testAccessionNumberOnlineValidation("go","go","GO:0000000",false);
	}
	
	@Test
	public void testOnlineValidationHgnc() {
		testAccessionNumberOnlineValidation("hgnc","hgnc","HGNC:10971",true); 
		testAccessionNumberOnlineValidation("hgnc","hgnc","HGNC:30123",true);
		testAccessionNumberOnlineValidation("hgnc","hgnc","HGNC:00000",false);
		
	}
	
	//TO CHANGE with external
	@Test
	public void testOnlineValidationHpa() {
		testAccessionNumberOnlineValidationHpa("hpa","proteinatlas","CAB016307",true); 
		testAccessionNumberOnlineValidationHpa("hpa","proteinatlas","HPA007415",true);
		testAccessionNumberOnlineValidationHpa("hpa","proteinatlas","HPA001893",true);   
		testAccessionNumberOnlineValidationHpa("hpa","proteinatlas","HPA037646",true);   
		testAccessionNumberOnlineValidationHpa("hpa","proteinatlas","HPA000000",false);
		testAccessionNumberOnlineValidationHpa("hpa","proteinatlas","CAB000000",false);
		
	}
	
	/**@Test
	public void testOnlineValidationIgsr() {
		assertTrue("Igsr is context only in dictionary",false);
	}*/
	
	@Test
	public void testOnlineValidationIntact() {
		testAccessionNumberOnlineValidation("intact","intact-interactions","EBI-11165886",true);   
		testAccessionNumberOnlineValidation("intact","intact-interactions","EBI-11165963",true);
		testAccessionNumberOnlineValidation("intact","intact-interactions","EBI-11165786",true);
		testAccessionNumberOnlineValidation("intact","intact-interactions","EBI-00000000",false);
	}
	
	@Test
	public void testOnlineValidationMint() {
		
		
		testAccessionNumberOnlineValidation("mint","mint","IM-26482",false);
		testAccessionNumberOnlineValidation("mint","mint","IM-24178",true);
		testAccessionNumberOnlineValidation("mint","mint","MINT-7905142",true);
		testAccessionNumberOnlineValidation("mint","mint","MINT-0000",false);
		testAccessionNumberOnlineValidation("mint","mint","IM-25676",false);
		testAccessionNumberOnlineValidation("mint","mint","IM-9",false);  
	}
	
	@Test
	public void testOnlineValidationInterpro() {
		testAccessionNumberOnlineValidation("interpro","interpro","IPR017667",true);   
		testAccessionNumberOnlineValidation("interpro","interpro","IPR003340",true);
		testAccessionNumberOnlineValidation("interpro","interpro","IPR000000",false);
	}
	
	@Test
	public void testOnlineValidationMetabolights() {
		testAccessionNumberOnlineValidation("metabolights","metabolights","MTBLS661",true);   
		testAccessionNumberOnlineValidation("metabolights","metabolights","MTBLS558",true);
		testAccessionNumberOnlineValidation("metabolights","metabolights","MTBLS000",false);
	}
	
	/**
	@Test
	public void testOnlineValidationEmma() {
		assertTrue("Emma On line validation not implemented because it is context only in dictionary. Should we implement it?",false);
	}*/
	
	@Test
	public void testOnlineValidationPfam() {
		testAccessionNumberOnlineValidation("pfam","pfam","PF01535",true); 
		testAccessionNumberOnlineValidation("pfam","pfam","PF00197",true);
		testAccessionNumberOnlineValidation("pfam","pfam","PFAM00000",false);
		testAccessionNumberOnlineValidation("pfam","pfam","PF00000",false);
	}
	
	@Test
	public void testOnlineValidationPride() {
		testAccessionNumberOnlineValidation("pxd","pride","PXD009799",true); 
		testAccessionNumberOnlineValidation("pxd","pride","PXD000000",false);
		testAccessionNumberOnlineValidation("pxd","pride","PXD009809",true);
	}
	
	@Test
	public void testOnlineValidationReactome() {
		testAccessionNumberOnlineValidation("reactome","reactome","R-HSA-913531",true); 
		testAccessionNumberOnlineValidation("reactome","reactome","R-HSA-877300",true);
		testAccessionNumberOnlineValidation("reactome","reactome","R-HSA-000000",false);
	}
	
	@Test
	public void testOnlineValidationRfam() {
		testAccessionNumberOnlineValidation("rfam","rfam","RF01820",true); 
		testAccessionNumberOnlineValidation("rfam","rfam","RF02543",true);
		testAccessionNumberOnlineValidation("rfam","rfam","RF00000",false);
	}
	
	@Test
	public void testOnlineValidationRnacentral() {
		testAccessionNumberOnlineValidation("rnacentral","rnacentral","URS00001DC04F_9606",true); 
		testAccessionNumberOnlineValidation("rnacentral","rnacentral","URS00001DC04F_9606",true);
		testAccessionNumberOnlineValidation("rnacentral","rnacentral","URS00001DC04F_0000",false);
	}
	
	@Test
	public void testOnlineValidationUniprot() {
		
		testAccessionNumberOnlineValidation("uniprot","uniprot","L8H4F8",true);
		testAccessionNumberOnlineValidation("uniprot","uniprot","C4JC09",true);
		testAccessionNumberOnlineValidation("uniprot","uniprot","K7TK04",true);
		testAccessionNumberOnlineValidation("uniprot","uniprot","B4FYF5",true);
		testAccessionNumberOnlineValidation("uniprot","uniprot","P29375",true);
		testAccessionNumberOnlineValidation("uniprot","uniprot","P53BP2",false);
	}
	
	@Test
	public void testOnlineValidationUniparc() {
		
		testAccessionNumberOnlineValidationResponseCode("uniparc","","UPI00026E6E87",true);
		testAccessionNumberOnlineValidationResponseCode("uniparc","","UPI0000000000",false);
	}
	
	@Test
	public void testOnlineValidationEbisc() {
		testAccessionNumberOnlineValidationResponseCode("ebisc","ebisc","STBCi044-A",true);
		testAccessionNumberOnlineValidationResponseCode("ebisc","ebisc","EURACi000-A",false);
		testAccessionNumberOnlineValidationResponseCode("ebisc","ebisc","HIHCNi002-A",false); //false positive to fix
		testAccessionNumberOnlineValidationResponseCode("ebisc","ebisc","EURACi004-A",false); //false positive to fix
	}
	
	@Test
	public void testOnlineValidationHipsci() {
		testAccessionNumberOnlineValidationHipsci("hipsci","hipsci","HPSI0114i-eipl_1",true);
		testAccessionNumberOnlineValidationHipsci("hipsci","hipsci","HPSI0713i-virz_2",true);
		testAccessionNumberOnlineValidationHipsci("hipsci","hipsci","HPSI0713i-darw_1",true);
		testAccessionNumberOnlineValidationHipsci("hipsci","hipsci","HPSI0000i-eipl_0",false);
	}
	 
	@Test
	public void testOnlineValidationOmim() {	
		
		
		testAccessionNumberOnlineValidation("omim","omim","000000",false);   
		testAccessionNumberOnlineValidation("omim","omim","103580",true);
		testAccessionNumberOnlineValidation("omim","omim","174800",true);
		testAccessionNumberOnlineValidation("omim","omim","795043",false);  
		testAccessionNumberOnlineValidation("omim","omim","980374",false); 
		testAccessionNumberOnlineValidation("omim","omim","980374",false); 
		testAccessionNumberOnlineValidation("omim","omim","163219",false); 
	}
	
	@Test
	public void testOnlineValidationRefSeq() {	
		testAccessionNumberOnlineValidationNcbi("refseq","nucleotide","NM_001101.4",true);
		testAccessionNumberOnlineValidationNcbi("refseq","nucleotide","NM_000000.0",false);
		testAccessionNumberOnlineValidationNcbi("refseq","nucleotide","XP_748685.1",false); // false positive to fix
		
	}
	
	@Test
	public void testOnlineValidationRefSnp() {	
		testAccessionNumberOnlineValidationNcbi("refsnp","snp","rs3852144",true);
		testAccessionNumberOnlineValidationNcbi("refsnp","snp","rs1",true);
		testAccessionNumberOnlineValidationNcbi("refsnp","snp","ss1",true);
		testAccessionNumberOnlineValidationNcbi("refsnp","snp","rs570877",true);
		testAccessionNumberOnlineValidationNcbi("refsnp","snp","rs1041983",true);
		testAccessionNumberOnlineValidationNcbi("refsnp","snp","rs000000",false);
		testAccessionNumberOnlineValidationNcbi("refsnp","snp","rs0000000",false);
		
	}
	
	@Test
	public void testOnlineValidationDoi() {	
		testAccessionNumberOnlineValidationDataCite("doi","doi","10.6084/m9.figshare.6827219.v3",true);
		testAccessionNumberOnlineValidationDataCite("doi","doi","10.6084/m9.figshare.c.4202669",true);
		testAccessionNumberOnlineValidationDataCite("doi","doi","10.5195/JMLA.2018.256",true);
		testAccessionNumberOnlineValidationDataCite("doi","doi","10.5195/JMLA.0000.000",false);
		testAccessionNumberOnlineValidationDataCite("doi","doi","10.0000/m9.figshare.c.0000000",false);
		
	}
	
	@Test
	public void testOnlineValidationRrid() {	
		testAccessionNumberOnlineValidationResponseCode("rrid","rrid","RRID:SCR_002798",true);
		testAccessionNumberOnlineValidationResponseCode("rrid","rrid","RRID:AB_2532057",true);
		testAccessionNumberOnlineValidationResponseCode("rrid","rrid","RRID:SCR_002010",true);
		testAccessionNumberOnlineValidationResponseCode("rrid","rrid","RRID:CVCL_ST66",true);
		testAccessionNumberOnlineValidationResponseCode("rrid","rrid","RRID:SCR_000000",false);
		
	}
	
	@Test
	public void testOnlineValidationBioproject() {	
		
		testAccessionNumberOnlineValidation("bioproject","project","PRJNA471919",true);
		testAccessionNumberOnlineValidation("bioproject","project","PRJNA478495",true);
		testAccessionNumberOnlineValidation("bioproject","project","PRJNA000000",false);
	
	}
	 
	@Test
	public void testOnlineValidationGca() {	
		
		testAccessionNumberOnlineValidation("gca","genome_assembly","GCA_000006335.3",true);
		testAccessionNumberOnlineValidation("gca","genome_assembly","GCA_001887795.1",true);
		testAccessionNumberOnlineValidation("gca","genome_assembly","GCA_000000000.0",false);
	
	}
	
	@Test
	public void testOnlineValidationTreefam() {	
		testAccessionNumberOnlineValidation("treefam","treefam","TF332117",true);
		testAccessionNumberOnlineValidation("treefam","treefam","TF332117",true);
		testAccessionNumberOnlineValidation("treefam","treefam","TF000000",false);
	}

	@Test
	public void testOnlineValidationNct() {	
		testAccessionNumberOnlineValidationResponseCode("nct","nct","NCT01177111",true);
		testAccessionNumberOnlineValidationResponseCode("nct","nct","NCT00001372",true);
		testAccessionNumberOnlineValidationResponseCode("nct","nct","NCT02041533",true);
		testAccessionNumberOnlineValidationResponseCode("nct","nct","NCT00000000",false);
	}
	
	@Test
	public void testOnlineValidationDbgap() {	
		testAccessionNumberOnlineValidation("dbgap","dbgap","phs000016",true);
		testAccessionNumberOnlineValidation("dbgap","dbgap","phs000021",true);
		testAccessionNumberOnlineValidation("dbgap","dbgap","phs000000",false);
	}
	
	@Test
	public void testOnlineValidationGeo() {	
		testAccessionNumberOnlineValidationNcbi("geo","gds","GSE26680",true);
		testAccessionNumberOnlineValidationNcbi("geo","gds","GDS1234",true);
		testAccessionNumberOnlineValidationNcbi("geo","gds","GPL3213",true);
		testAccessionNumberOnlineValidationNcbi("geo","gds","GSM26680",true);
		testAccessionNumberOnlineValidationNcbi("geo","gds","GPL0000",false);
	}
	
	private void testAccessionNumberOnlineValidation(String db, String domain, String id, boolean outcomeExpected) {
		if ((("gca").equalsIgnoreCase(db)==false) && (("gen".equalsIgnoreCase(db) && id.matches("^GCA_.+"))==false)){
			id = ar.normalizeID(db, id);
		}
		if (outcomeExpected) {
			assertTrue(String.format("Accession number %s of domain %s is NOT valid!", id, domain), ar.isValid(domain, id));
		}else {
			assertFalse(String.format("Accession number %s of domain %s is valid!", id, domain), ar.isValid(domain, id));
		}
	}
	
    private void testAccessionNumberOnlineValidationNcbi(String db, String domain, String id, boolean outcomeExpected) {
		if (outcomeExpected) {
			assertTrue(String.format("Accession number %s of domain %s is NOT valid!", id, domain), nr.isValid(domain, id));
		}else {
			assertFalse(String.format("Accession number %s of domain %s is valid!", id, domain), nr.isValid(domain, id));
		}
	}
    
    private void testAccessionNumberOnlineValidationDataCite(String db, String domain, String id, boolean outcomeExpected) {
		if (outcomeExpected) {
			assertTrue(String.format("Accession number %s of domain %s is NOT valid!", id, domain), dr.isValid(domain, id));
		}else {
			assertFalse(String.format("Accession number %s of domain %s is valid!", id, domain), dr.isValid(domain, id));
		}
	}
    
    private void testAccessionNumberOnlineValidationBioStudies(String db, String domain, String id, boolean outcomeExpected) {
    	if (outcomeExpected) {
			assertTrue(String.format("Accession number %s of domain %s is NOT valid!", id, domain), bioStudiesr.isValid(domain, id));
		}else {
			assertFalse(String.format("Accession number %s of domain %s is valid!", id, domain), bioStudiesr.isValid(domain, id));
		}
		
	}
    
    private void testAccessionNumberOnlineValidationHpa(String db, String domain, String id, boolean outcomeExpected) {
    	if (outcomeExpected) {
			assertTrue(String.format("Accession number %s of domain %s is NOT valid!", id, domain), hpar.isValid(domain, id));
		}else {
			assertFalse(String.format("Accession number %s of domain %s is valid!", id, domain), hpar.isValid(domain, id));
		}
		
	}
    
    private void testAccessionNumberOnlineValidationHipsci(String db, String domain, String id, boolean outcomeExpected) {
    	if (outcomeExpected) {
			assertTrue(String.format("Accession number %s of domain %s is NOT valid!", id, domain), hipscir.isValid(domain, id));
		}else {
			assertFalse(String.format("Accession number %s of domain %s is valid!", id, domain), hipscir.isValid(domain, id));
		}
		
	}
    
    private void testAccessionNumberOnlineValidationResponseCode(String db, String domain, String id, boolean outcomeExpected) {
    	if (outcomeExpected) {
			assertTrue(String.format("Accession number %s of domain %s is NOT valid!", id, domain), responseCoder.isValid(db, id));
		}else {
			assertFalse(String.format("Accession number %s of domain %s is valid!", id, domain), responseCoder.isValid(db, id));
		}
		
	}

}
