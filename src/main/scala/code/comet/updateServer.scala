package code.comet

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/11/12
 * Time: 10:17 PM
 * To change this template use File | Settings | File Templates.
 */
import scala.collection.mutable.{Map, HashMap}
import java.util.Date
import net.liftweb._
import http._
import actor._
import util.StringHelpers.randomString
import code.model.Post

case class updatePosts(total:Long, recentPosts:List[Post])
object updateServer  extends LiftActor with ListenerManager {
  private var posts:List[Post] = Nil
  private var total:Long = 0
  protected def createUpdate:updatePosts ={
    updatePosts(total, posts)

  }
  override def lowPriority = {
    case update:updatePosts => try{
      posts = update.recentPosts
      total = update.total
      updateListeners(updatePosts(total, posts))
    }

  }
}
