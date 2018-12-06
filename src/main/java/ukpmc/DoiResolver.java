package ukpmc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import ukpmc.scala.Resolvable;

public class DoiResolver extends Resolver implements Resolvable {
   private final String HOST;
   private static Properties prop = new Properties();
   private static Map<String, String> BlacklistDoiPrefix = new HashMap<>();

   public DoiResolver() {
      HOST = "api.datacite.org/works/";
      // example: https://api.datacite.org/works/10.5061/dryad.pk045
   }

   public boolean isValid(String sem_type, String doi) {
	  boolean ret=false;
      if (BlacklistDoiPrefix.containsKey(prefixDOI(doi))) {
    	  ret=false;
      } else if ("10.2210/".equals(doi.substring(0, 8))) { // exception rule for PDB data center
         ret= true;
      } else {
    	  ret = isDOIValid("doi", doi);
      }
      
      if (ret) {
     	 System.err.println(String.format("Datacite validation : Accession Number %s for database doi is VALID", doi));
      }else {
     	 System.err.println(String.format("Datacite validation : Accession Number %s for database doi is NOT VALID", doi));
      }
      
      return ret;
   }

   /**
    * return a prefix of a DOI
    */
   String prefixDOI(String doi) {
      String prefix = "";
      int bsIndex = doi.indexOf("/");
      if (bsIndex != -1) prefix = doi.substring(0, bsIndex);
      return prefix;
   }

   private boolean isDOIValid(String domain, String doi) {
	  boolean ret=false;
      try {
         URL url = toURL(URLEncoder.encode(doi,"UTF-8"), HOST);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         String response = connection.getResponseMessage();
         connection.disconnect();
         ret=response.equals("OK");
      } catch (Exception e) {
    	  ret=false;
      } 
      
      return ret;
   }

   private static void loadDOIPrefix() throws IOException {
      URL url = DoiResolver.class.getResource("/validate.properties");
      if (url == null) throw new RuntimeException("can not find validate.properties!");
      prop.load(url.openStream());

      String doiPrefixFilename = prop.getProperty("doiblacklist");
      URL pURL = DoiResolver.class.getResource("/" + doiPrefixFilename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(pURL.openStream()));

      String line;
      while ((line = reader.readLine()) != null) {
         if (line.indexOf("#") != 0) {
            int firstSpace = line.indexOf(" ");
            String prefix = line.substring(0, firstSpace);
            BlacklistDoiPrefix.put(prefix, "Y");
         }
      }
      reader.close();
   }

  static {
	   try {
        loadDOIPrefix();
      } catch (Exception e) {
         System.err.println("Error loading DOI blacklist file");
         e.printStackTrace();
      }
   }
}
