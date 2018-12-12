package ukpmc;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ukpmc.scala.Resolvable;

public class NcbiResolver extends Resolver implements Resolvable {

   private final String HOST = "eutils.ncbi.nlm.nih.gov";

   public boolean isValid(String domain, String accno) {
       return isAccValid(domain, accno);
   }

  

   private boolean isAccValid(String domain, String accno) {
	 boolean ret=true;
	 BufferedReader in = null;
     try{
       String query = "entrez/eutils/esearch.fcgi?api_key=59fdb1e97d6c82c703206a006ad284447208&db=" + domain + "&term=" +  URLEncoder.encode(accno,"UTF-8");
       URL url = toURL(query, HOST);
       in = new BufferedReader(new InputStreamReader(url.openStream()));
       String line;
       while ((line = in.readLine()) != null) {
        
	     if (line.contains("<Count>0</Count>")) {
	    	 ret=false;
	     }
       }
     } catch (Exception e) {
    	 AccResolver.logOutput(e);
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
    	 AccResolver.logOutput(String.format("NCBI validation : Accession Number %s for database %s is VALID", accno, domain));
      }else {
    	  AccResolver.logOutput(String.format("NCBI validation : Accession Number %s for database %s is NOT VALID", accno, domain));
      }
     
     return ret;
   }
}
