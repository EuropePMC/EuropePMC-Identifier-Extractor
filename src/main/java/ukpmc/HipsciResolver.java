package ukpmc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class HipsciResolver extends Resolver{

	public boolean isValid(String domain, String accno) {
	 boolean ret=false;
	 BufferedReader in = null;
     try{
       URL url = new URL("http://www.hipsci.org/lines/api/cellLine/"+URLEncoder.encode(accno,"UTF-8")); 
       in = new BufferedReader(new InputStreamReader(url.openStream()));
       String line;
       while ((line = in.readLine()) != null) {
	     if (line.contains("\"found\":true")) {
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
				
			}
    	 }
     }
     
     if (ret) {
     	 System.err.println(String.format("Hipsci validation : Accession Number %s for database %s is VALID", accno, domain));
      }else {
     	 System.err.println(String.format("Hipsci validation : Accession Number %s for database %s is NOT VALID", accno, domain));
      }
     
     return ret;
	}
	
}
