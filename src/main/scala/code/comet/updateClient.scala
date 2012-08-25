package code.comet

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/11/12
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */
import scala.xml._

import net.liftweb._
import http._
import js.JsCmds.Alert
import util._
import Helpers._
import code.model.Post

class updateClient extends CometActor with CometListener{

  protected def registerWith = updateServer
  private var posts:List[Post] = Nil
  private var total:Long = 0
  override def lowPriority = {
    case data: updatePosts => try {
      posts = data.recentPosts
      total = data.total
      reRender()
    }
  }
  def dataExtractor(p:Post):NodeSeq = {
    var mail = "mailto:"+p.replyEmail.toString()+"?Subject="+p.title
    <li><a href="#" class="mypopover" rel="popover"  data-content={p.contents.toString()}>{p.title} <a class="icon-envelope" href= {mail}>
      </a></a></li>
  }
  def render:RenderOut =
    "#cometdata" #> <div>{posts.flatMap(dataExtractor _)}</div> &
    "#totalpost" #> <p>Total posts: {total}</p>

}
