package ukpmc;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.util.regex.Matcher;

import ukpmc.scala.Resolvable;

public class AccResolver extends Resolver implements Resolvable {


   private final String HOST = "www.ebi.ac.uk";
	private final String CATH_HOST_SUPER_FAMILY = "http://www.cathdb.info/version/v4_2_0/api/rest/superfamily/";
	private final String CATH_HOST_DOMAIN_FAMILY = "http://www.cathdb.info/version/v4_2_0/api/rest/domain_summary/";
	private final String MINT_HOST = "http://www.ebi.ac.uk/Tools/webservices/psicquic/mint/webservices/current/search/query/";
	private final String EBI_HOST_SHORT = "ebisearch/ws/rest/";
  
	public boolean isValid(String domain, String accno) {
		
		boolean ret;
       try {
    	   if(domain.matches("cath|mint")){
    		   fixForPKIXError();
    		   ret = isOtherAccValid(domain, accno);
    	   } else { 
    		   ret = isAccValid(domain, accno);
    	   }
       }catch(Exception e) {
		   ret=false;
	   }
	   return ret;
   }

   private boolean isAccValid(String domain, String accno) {
	 boolean ret = false;  
     if ("efo".equals(domain)) {
        accno = extractNumbers(accno);
         // System.err.println(accno);
     } else if ("reactome".equals(domain)) {
         accno = extractNumbers(accno);
     }
    
    String query = "";
	
		
     BufferedReader in=null;
     try {
       if(domain.equalsIgnoreCase("biomodels")) {
    		 query = EBI_HOST_SHORT + domain + "?query="+ URLEncoder.encode("id:\"" + accno + "\" OR submissionid:\"" + accno + "\"","UTF-8");
       } else if(domain.equalsIgnoreCase("omim")) {
    		 query = EBI_HOST_SHORT + domain + "?query="+ URLEncoder.encode("id:\"" + accno + "\"" ,"UTF-8");
       } else {			
    		 query = EBI_HOST_SHORT + domain + "?query=" + URLEncoder.encode("acc:\"" + accno + "\" OR id:\"" + accno + "\"","UTF-8");
       }
       URL url = toURL(query, HOST);
       in = new BufferedReader(new InputStreamReader(url.openStream()));
       String line;
       while ((line = in.readLine()) != null) {
	     if (line.contains("<hitCount>0</hitCount>")==false) {
	    	 ret=true;
	     }
       }
     } catch (Exception e) {
         System.err.println(e);
         ret=false;
     }finally {
    	 if (in!=null) {
    		 try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
     }
     
     if (ret) {
    	 System.err.println(String.format("EBI validation: Accession Number %s for database %s is VALID", accno, domain));
     }else {
    	 System.err.println(String.format("EBI validation: Accession Number %s for database %s is NOT VALID", accno, domain));
     }
     
     return ret;
   }

   private boolean isOtherAccValid(String domain, String accno) {
		URL url  = null; Boolean isOtherValid = false; 

		try {
			if(domain.equalsIgnoreCase("cath")) {
				url = accno.contains(".") ? new URL(CATH_HOST_SUPER_FAMILY+accno) : new URL(CATH_HOST_DOMAIN_FAMILY+accno);
			} else if(domain.equalsIgnoreCase("mint")) {
				url = new URL(MINT_HOST+accno);
			} 

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");
			if(domain.equalsIgnoreCase("cath") &&(accno.toUpperCase().contains("HPA") || accno.toUpperCase().contains("CAB"))) {
				conn.setRequestProperty("content-type", "application/gzip");
			}

			if(conn.getResponseCode() == 200) {				
				try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
					String line;

					while ((line = in.readLine()) != null) {
						if(domain.equalsIgnoreCase("cath")) {							
							isOtherValid = line.contains("\"success\":true") ;
						} else if(domain.equalsIgnoreCase("mint")) {
							isOtherValid =  line.contains(accno);
						}else if(domain.equalsIgnoreCase("proteinatlas")) {

						}
					}
				} catch (IOException e) {
					System.err.println(e);
					isOtherValid = false;
				}
			} else {
				isOtherValid = false;
				System.err.println("&&&&&&&&&&&&  Accession Validation = FALSE  BECAUSE Response Code is not 200. Actual respone code is "+ conn.getResponseCode());
			}


			conn.disconnect();

		}catch(Exception e) {
			isOtherValid = false;
			System.err.println(String.format("External validation: Accession Number %s for database %s is NOT VALID", accno, domain));
		}
		
		 if (isOtherValid) {
	    	 System.err.println(String.format("OtherDomain validation: Accession Number %s for database %s is VALID", accno, domain));
	     }else {
	    	 System.err.println(String.format("OtherDomain validation: Accession Number %s for database %s is NOT VALID", accno, domain));
	     }

		return isOtherValid;
	}

   private String extractNumbers(String accno) {
       Pattern p = Pattern.compile("\\d+");
       Matcher m = p.matcher(accno);
       if (m.find()) {
           return m.group();
       }
       return accno;
   }
   /**
	 * Fix for Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	 * sun.security.validator.ValidatorException: PKIX path building failed:
	 * sun.security.provider.certpath.SunCertPathBuilderException: unable to find
	 * valid certification path to requested target
	 */

	private void fixForPKIXError() {
		try {
			/* Start of Fix */
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
				public void checkClientTrusted(X509Certificate[] certs, String authType) { }
				public void checkServerTrusted(X509Certificate[] certs, String authType) { }

			} };

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) { return true; }
			};
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			/* End of the fix*/
		}catch(Exception e) {
			System.err.println("Failed to ignore the certificate validation  " + e.getMessage());
		}
	}
}

