package code.snippet

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/10/12
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.java.util.Date
import code.lib._
import Helpers._
import _root_.scala.xml.{NodeSeq,Text}
import net.liftweb.http.{Templates, SHtml, S, DispatchSnippet}
import net.liftweb.http.js.JsCmds.Alert
import net.liftweb.http.js.jquery.JqJsCmds.ModalDialog
import net.liftweb.http.js.JE.{Str, JsVar, JsVal, JsObj}
import net.liftweb.http.js.{JsCmds, JsExp}

class redirectUser extends DispatchSnippet{
  def dispatch = {
    case "view" => view
    case "post" => post
  }

  def post = "#post" #> redirectTOpost
  def view = "#view [onclick]" #> SHtml.ajaxInvoke(() => {JsCmds.RedirectTo("/view.html")} )


  def redirectTOpost =SHtml.ajaxButton("Post My Stuff",() => try {
    Templates(List("templates-hidden", "post")).map(html => ModalDialog(html, JsObj(("top" -> Str("30%"))))).openOr(Alert("missing template"))
  } catch {
    case error: Exception => Alert("exception: "+error.getMessage)
  } )



}
