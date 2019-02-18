package ukpmc.scala

import monq.jfa.Xml
import java.util.Map
import scala.util.Try

case class GrantMwtAtts(tagName: String, content: String, db: String, valMethod: String, domain: String, ctx: String, wsize: Integer, negate: String, abbrcontext: String)

class GrantMwtParser(val map: Map[String, String]) {

  // e.g., <template><ack_fund db="%1" valmethod="%2" domain="%3" context="%4" wsize="%5" negate="%6">%0</ack_fund></template>
  def parse() = {
    val tagName = map.get(Xml.TAGNAME)
    val content = map.get(Xml.CONTENT)

    val db = map.get("db")
    val valMethod = map.get("valmethod")
    val domain = map.get("domain")
    val ctx = map.get("context")
    val wSize = Try(map.get("wsize").toInt).toOption.getOrElse(0)
    val negate = map.get("negate")
    val abbrcontext = map.get("abbrcontext")

    GrantMwtAtts(tagName, content, db, valMethod, domain, ctx, wSize, negate, abbrcontext)
  }
}
