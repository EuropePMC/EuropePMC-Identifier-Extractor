package ukpmc.scala

import monq.jfa.Xml
import java.util.Map

case class MwtAtts(tagname: String, content: String, db: String, valmethod: String, domain: String, ctx: String, wsize: Integer, sec: String)

class MwtParser(val map: java.util.Map[String, String]) {

  // <template><z:acc db="%1" valmethod="%2" domain="%3" context="%4" wsize="%5" sec="%6">%0</z:acc></template>
  def parse() = {
    val tagname = map.get(Xml.TAGNAME);
    val content = map.get(Xml.CONTENT);

    val db = map.get("db");
    val valmethod = map.get("valmethod");
    val domain = map.get("domain");
    val ctx = map.get("context");
    val wsize = map.get("wsize").toInt;
    val sec = map.get("sec");

    MwtAtts(tagname, content, db, valmethod, domain, ctx, wsize, sec)
  }
}
