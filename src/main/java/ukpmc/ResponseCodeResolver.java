package ukpmc;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ResponseCodeResolver extends Resolver {
	public boolean isValid(String domain, String accno) {
		boolean ret = false; 
		try {
			URL url  = null; 
			if(domain.equalsIgnoreCase("empiar")) {
				url = new URL("https://www.ebi.ac.uk/pdbe/emdb/empiar/api/entry/"+URLEncoder.encode(accno,"UTF-8"));
			} else if(domain.equalsIgnoreCase("ebisc")) {
				url = new URL("https://cells.ebisc.org/"+URLEncoder.encode(accno,"UTF-8"));
			} else if(domain.equalsIgnoreCase("rrid")) {
				url = new URL("https://scicrunch.org/resolver/"+URLEncoder.encode(accno,"UTF-8"));
			} else if(domain.equalsIgnoreCase("nct")) {
				url = new URL("https://clinicaltrials.gov/ct2/show/"+URLEncoder.encode(accno,"UTF-8"));
			}

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			ret = conn.getResponseCode() == 200;
		}catch(Exception e) {
			ret=false;
			System.err.println(e.getMessage());
		}
		
		if (ret) {
	     	 System.err.println(String.format("ResponseCode validation : Accession Number %s for database %s is VALID", accno, domain));
	      }else {
	     	 System.err.println(String.format("ResponseCode validation : Accession Number %s for database %s is NOT VALID", accno, domain));
	      }
		
		return ret;
	}
}
