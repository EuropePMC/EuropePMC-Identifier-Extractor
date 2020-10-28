package ukpmc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ukpmc.scala.Resolvable;

/**
 * Created by jee on 27/06/17.
 */
public abstract class Resolver implements Resolvable {
	
	static {
		fixForPKIXError();
	}
	
    public String normalizeID(String db, String id) {
        int dotIndex;
        dotIndex = id.indexOf(".");
        if (dotIndex != -1 && (!"doi".equals(db)) && (!"cath".equals(db))) id = id.substring(0, dotIndex);
        if (id.endsWith(")")) id = id.substring(0, id.length() - 1);
        return id.toUpperCase();
    }
    
    protected URL toURL(String params, String host) {
        try {
      	  String request="https://"+host+"/"+params;
  	       URL url = new URL(request);
  	       return url;
        } catch (MalformedURLException e) {
          throw new IllegalArgumentException();
        }
     }
    
    /**
	 * Fix for Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	 * sun.security.validator.ValidatorException: PKIX path building failed:
	 * sun.security.provider.certpath.SunCertPathBuilderException: unable to find
	 * valid certification path to requested target
	 */
    private static void fixForPKIXError() {
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
				public boolean verify(String hostname, SSLSession session) { 
					return true;
				}
			};
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			/* End of the fix*/
		}catch(Exception e) {
			System.err.println("Verified host error");
			//AccResolver.logOutput("Failed to ignore the certificate validation  " + e.getMessage());
		}
	}
    
    
     protected String getResponseApi(URL url) throws Exception{
		
		BufferedReader rd=null;
		 StringBuilder res= new StringBuilder();
		try{

		    HttpURLConnection con = (HttpURLConnection)url.openConnection();
		    con.setDoOutput(true);
		    con.setRequestMethod("GET");
		
		    rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		    String line;
		   
		    while ((line = rd.readLine()) != null) {
		        res.append(line);
		    }
		    
		}finally{
			if (rd!=null){
				rd.close();
			}

		}
		
		return res.toString();
	}

}
