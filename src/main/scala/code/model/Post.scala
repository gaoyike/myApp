package code.model

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/11/12
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */

import net.liftweb.mapper._

import net.liftweb.http.SHtml
import net.liftweb.common._
import java.sql.Date
import net.liftweb.util.{FieldError, FieldIdentifier}
import scala.Predef._
import net.liftweb.common.Full

class Post extends LongKeyedMapper[Post] with IdPK {
  def getSingleton = Post


  object title extends MappedString(this, 140)  {
    override def validations = valMaxLen(140, "Title must be no longer than 140 characters!") _ :: valMinLen(2, "Title must be at least 2 characters!") _:: super.validations

  }
  object replyEmail extends MappedEmail (this, 100)
  object contents extends MappedText(this)

  object postedDate extends MappedDateTime(this)


}

object Post extends Post with LongKeyedMetaMapper[Post]

