package code.snippet

import code.model.Post
import net.liftweb.util.Helpers._
import net.liftweb._
import http._
import mapper.{Descending, MaxRows, Ascending, OrderBy}
import util.FieldError
import util.Helpers._
import js._
import JsCmds._
import code.comet.{updateServer,  updatePosts}
import net.liftweb._
/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/11/12
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */

class postExtractor {
  var title = ""
  var email = ""
  var text = ""

  def render ={
    var p:Post = Post.create
    def process()={
      val p_ex=p.title(title)
      p_ex.validate match{
        case Nil => Noop
        case e:List[FieldError] => S.error("Title can not be more than 140 words " +e.toString())
      }
      p.contents(text)
      p.replyEmail(email)
      p.postedDate(now)
      val saved: Boolean = p.save()
      S.notice(p.toString())

      if(title.isEmpty || text.isEmpty || email.isEmpty)
        Noop
      else
      {
        val recentposts:List[Post] = Post.findAll(OrderBy(Post.postedDate,Descending),MaxRows(15))
        val maxNum = Post.count
        updateServer ! updatePosts(maxNum, recentposts)
      }






    }

    "#title" #> SHtml.text(title, title = _)&
      "#email" #> SHtml.email(email, (v: String) => email = v) &
      "#text" #> SHtml.textarea(text, text = _)&
      "#cancel [onclick]" #> SHtml.ajaxInvoke(() => {JsCmds.RedirectTo("/")} ) &
      "#hidden" #> SHtml.hidden(process)


  }

}
