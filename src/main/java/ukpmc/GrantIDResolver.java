package ukpmc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.*;

import ukpmc.scala.Resolvable;

public class GrantIDResolver implements Resolvable {
	
		
	private Pair<String, String> MRCP = Pair.with("mrc","medical research council");
	private Pair<String, String> WTP = Pair.with("wt","wellcome trust");
	private Pair<String, String> ALZSP = Pair.with("alzs","alzheimer's society");
	private Pair<String, String> BBSRCP = Pair.with("bbsrc","biotechnology and biological sciences research council");
	private Pair<String, String> LLRP = Pair.with("llr","(?i)(Bloodwise|Leukaemia Research( Fund)?( Foundation)?|Leukaemia \\& Lymphoma Research( Fund)?( Foundation)?|Leukaemia and Lymphoma Research|Leukaemia Lymphoma Research)");
	private Pair<String, String> ERCP = Pair.with("erc","european research council");
//Versus Arthritis|Arthritis Research UK|Arthritis Research Campaign
	
	private Triplet<String, String, String> MSST = Triplet.with("mss","ms society","multiple sclerosis society");
	private Quartet<String, String, String, String> NC3RSQ = Quartet.with("nc3rs","NC3Rs","national centre for the 3rs","national centre for the replacement, refinement and reduction of animals in research");
	private Quintet<String, String, String, String, String> ARUKQ = Quintet.with("va","ARUK","arthritis research uk","arthritis research campaign","versus arthritis");
	private Sextet<String, String, String,String,String,String> SNSFS = Sextet.with("snsf","SNF","FNS","swiss national science foundation","fonds national suisse","fondo nazionale svizzero");

	public boolean isValid(String domain, String id) {
		return false;
	}

	/*
	 * isConflictFunderNameExitInText useful to avoid bit of false positives occurred during pattern conflicts between funders.
	 * 
	 * In some case, both conflict patterns appears in one sentence. In that case, monq blindly assign both patterns to one name because </z:acc> isZacc and is Type both are TRUE before come to this step.  
	 * To avoid that, manually check that other conflicted pattern's funder names appears in text. If so,  isConflictFunderNotExist = false then the valid will be false.
	 * Ex: This work has been supported by grants from the Swiss National Science Foundation (SNSF, 156393), the European Research Council (ERC-2009-AdG, DHISP 250128) and the Hartmann Müller Foundation to H.U.Z.
	 * Before fix: This work has been supported by grants from the Swiss National Science Foundation (SNSF, <z:acc db="snsf" ids="156393">156393</z:acc>), the European Research Council (ERC-2009-AdG, DHISP <z:acc db="snsf" ids="250128">250128</z:acc>) and the Hartmann Müller Foundation to H.U.Z.
	 * After fix: This work has been supported by grants from the Swiss National Science Foundation (SNSF, <z:acc db="snsf" ids="156393">156393</z:acc>), the European Research Council (ERC-2009-AdG, DHISP 250128) and the Hartmann Müller Foundation to H.U.Z.
	 *
	 *Acknowledgments Research was supported by The Wellcome Trust (to S.A.J. and N.T., Ref. 065961, 069630, 079044), Arthritis Research UK (to S.A.J. and G.W.J.; Ref. 20305, 19796, 19381, 18286), Kidney Research UK (to C.A.F.; Ref. CDF2/2006), the Kenyon Gilson EPS Research Fund (to C.A.F., S.A.J., and N.T.), and an MRC capacity-building PhD Studentship (to S.A.J. and V.B.O’D.).
	 *
	 */
	public boolean isConflictFunderNameExitInText(String text,String db, int grantIdLength) {
		boolean isConflictFunderNotExist = false  ;

		//String text = textBeforeEntity.toLowerCase();
		/* AA/A999999/9 pattern conflict with  NC3RS, BBSRC, MRC*/
		if(db.equals(NC3RSQ.getValue0()) &&  grantIdLength == 12) {
			//isConflictFunderNotExist = isBBSRCContains(text)  || text.contains(MRCP.getValue1()) ;
			isConflictFunderNotExist = isBBSRCContains(text) ;
		}else if(db.equals(BBSRCP.getValue0()) &&  grantIdLength == 12) {
			isConflictFunderNotExist = isNC3RSTcontains(text);
			//isConflictFunderNotExist = isNC3RSTcontains(text) || text.contains(MRCP.getValue1());
		}else if(db.equals(MRCP.getValue0()) &&  grantIdLength == 12) {
			isConflictFunderNotExist = isNC3RSTcontains(text) || isBBSRCContains(text) ;
		}else 
			/* 999999 pattern conflict with  WT, SNSF, ERC */	
			if (db.equals(WTP.getValue0()) &&  grantIdLength == 6) {
				isConflictFunderNotExist = isSNSFContains(text)||isERCContains(text) ;	
			}else if (db.equals(SNSFS.getValue0()) &&  grantIdLength == 6) {
				isConflictFunderNotExist = text.contains(WTP.getValue1())||isERCContains(text) ;
			}else if (db.equals(ERCP.getValue0()) &&  grantIdLength == 6) {
				isConflictFunderNotExist = isSNSFContains(text)||text.contains( WTP.getValue1()) ;	
			} else
				/* 99999 pattern conflict with  WT, ARC, LLR */
				if (db.equals(WTP.getValue0()) &&  grantIdLength == 5) {
					isConflictFunderNotExist = isARUKContains(text)|| isLLRContextContains(text) ;		
				}else if (db.equals(ARUKQ.getValue0()) &&  grantIdLength == 5) {
					isConflictFunderNotExist = text.contains(WTP.getValue1()) || isLLRContextContains(text) ;		
				}else if (db.equals(LLRP.getValue0()) &&  grantIdLength == 5) {
					isConflictFunderNotExist = isARUKContains(text)||text.contains( WTP.getValue1()) ;
				
				} /* 99 and 999 pattern conflict with  ALZS, MSS */
				else
					if (db.equals(ALZSP.getValue0()) &&  (grantIdLength == 2 || grantIdLength ==3)) {
						isConflictFunderNotExist = text.contains(MSST.getValue1())||text.contains(MSST.getValue2()) ;			
					}else if (db.equals(MSST.getValue0()) &&  (grantIdLength == 2 || grantIdLength ==3)) {
						isConflictFunderNotExist = text.contains(ALZSP.getValue1()) ;
					}
		//System.out.println(isConflictFunderNotExist);
		return isConflictFunderNotExist;
	}
	
	private boolean isNC3RSTcontains(String content) {
		return (content.contains( NC3RSQ.getValue0())||content.contains( NC3RSQ.getValue1())
				||content.contains( NC3RSQ.getValue2())||content.contains( NC3RSQ.getValue3()));
	}

	public boolean isLLRContextContains(String content) {
		Pattern p = Pattern.compile(LLRP.getValue1());
		Matcher m = p.matcher(content);
		
		return  m.find();
	}
	private boolean isARUKContains(String content) {
		return (content.contains( ARUKQ.getValue1()) ||content.contains( ARUKQ.getValue2())
				 ||content.contains( ARUKQ.getValue3()) ||content.contains( ARUKQ.getValue4()));
	}
	private boolean isBBSRCContains(String content) {
		return (content.contains( BBSRCP.getValue0().toUpperCase())||content.contains( BBSRCP.getValue1()));
	}
	private boolean isERCContains(String content) {
		return (content.contains(ERCP.getValue1()));
	}
	private boolean isSNSFContains(String content) {

		return (content.contains( SNSFS.getValue0().toUpperCase())||content.contains( SNSFS.getValue1())||content.contains( SNSFS.getValue2())
				||content.contains( SNSFS.getValue3())||content.contains( SNSFS.getValue4())||content.contains( SNSFS.getValue5()));
		
	}
	
}
