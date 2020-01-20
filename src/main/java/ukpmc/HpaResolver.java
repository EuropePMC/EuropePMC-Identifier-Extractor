package ukpmc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import uk.ac.ebi.utility.file.FileUtility;

public class HpaResolver extends Resolver {

	 public boolean isValid(String domain, String accno) {
		 boolean ret=false;
		 BufferedReader in=null;
		 InputStream inputStream;
		 
		 try {
			 String query = "https://www.proteinatlas.org/search/"+URLEncoder.encode(accno,"UTF-8")+"?format=tsv";
		     URL url = new URL(query);
		     inputStream = url.openStream();
		     if (FileUtility.isGZipped(inputStream)) {
		    	 inputStream = new GZIPInputStream(url.openStream());
		     }
		     
		     in = new BufferedReader(new InputStreamReader(inputStream));
		    
		     int numLines=0;
		     while (in.readLine() != null) {
		    	 numLines++;
		     }
		     
		     ret = numLines>1;
	      
		 }catch(Exception e) {
			 ret=false;
			 AccResolver.logOutput(e.getMessage());
	    	 if (in!=null) {
	    		  try {
					in.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	 }
	     }
		 
		 if (ret) {
			 AccResolver.logOutput(String.format("Hpa validation : Accession Number %s for database %s is VALID", accno, domain));
	      }else {
	    	  AccResolver.logOutput(String.format("Hpa validation : Accession Number %s for database %s is NOT VALID", accno, domain));
	      }
		 
		 return ret;
	  }
}
