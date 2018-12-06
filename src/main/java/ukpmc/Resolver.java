package ukpmc;

import java.net.MalformedURLException;
import java.net.URL;

import ukpmc.scala.Resolvable;

/**
 * Created by jee on 27/06/17.
 */
public abstract class Resolver implements Resolvable {
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

}
