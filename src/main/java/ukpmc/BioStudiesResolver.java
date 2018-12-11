package ukpmc;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ukpmc.scala.Resolvable;

public class BioStudiesResolver extends Resolver implements Resolvable {

   public boolean isValid(String domain, String accno) {
       return isAccValid(domain, accno);
   }

  

   private boolean isAccValid(String domain, String accno) {
	 boolean ret=true;
	 BufferedReader in = null;
     try{
       String query = "https://www.ebi.ac.uk/biostudies/api/v1/search?query=accession:" +  URLEncoder.encode(accno,"UTF-8");
       URL url = new URL(query);
       in = new BufferedReader(new InputStreamReader(url.openStream()));
       String line;
       while ((line = in.readLine()) != null) {
         // TODO make the check more robust
	     if (line.contains("\"totalHits\":0")) {
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
    	 AccResolver.logOutput(String.format("BioStudies validation : Accession Number %s for database %s is VALID", accno, domain));
      }else {
    	 AccResolver.logOutput(String.format("BioStudies validation : Accession Number %s for database %s is NOT VALID", accno, domain));
      }
     
     return ret;
   }
}
