package code.snippet
import code.view._
import code.model._
/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/13/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */

class postTable {
  def table = {
    val cols = "Title" :: "Post Date" :: "Contents" :: "email" :: Nil
    val fun = (params: DataTableParams) => {
      val p:List[Post] = Post.findAll()
      def rowFun(ps:Post) = List((ps.title.is, ps.postedDate.is.getYear.toString+ps.postedDate.is.getMonth.toString+ps.postedDate.is.getDay.toString, ps.contents.is, ps.replyEmail.is))
      var rows:List[(String, String, String, String)] = List.empty
       p.map( a => rows = rowFun(a):::rows)

      val count = p.size

      new DataTableObjectSource(count, count, rows.map(r =>
        List(("0", r._1),
          ("1", r._2),("2", r._3),("3", r._4),
          ("DT_RowId", "rowid_" + r._1))))


    }
    DataTable(
      cols, // columns
      fun, // our data provider
      "my-table", // html table id
      List(("bFilter", "false")), // datatables configuration
      ("class", "table table-striped table-bordered")) // set css class for table
  }

  }


