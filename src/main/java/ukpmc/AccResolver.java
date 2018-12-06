package ukpmc;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import ukpmc.scala.Resolvable;

public class AccResolver extends Resolver implements Resolvable {

   private final String HOST = "www.ebi.ac.uk";

   public boolean isValid(String domain, String accno) {
       boolean ret;
       try {
    	   ret = isAccValid(domain, accno);
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
    
     BufferedReader in=null;
     try {
       String query = "ebisearch/ws/rest/" + domain + "?query=" +  URLEncoder.encode("acc:\"" + accno + "\" OR id:\"" + accno + "\"","UTF-8");
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

   private String extractNumbers(String accno) {
       Pattern p = Pattern.compile("\\d+");
       Matcher m = p.matcher(accno);
       if (m.find()) {
           return m.group();
       }
       return accno;
   }
}
