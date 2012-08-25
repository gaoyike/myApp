package code.view

import _root_.scala.xml.{NodeSeq, Node, Elem, PCData, Text}
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import S._
import SHtml._
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.json._
import scala.util.parsing.json.JSONArray

object DataTable {
  def apply(
             columns: List[String],
             fun: (DataTableParams) => DataTableSource,
             idOpt: Option[String],
             jsonOptions: List[(String, String)],
             attrs: (String, String)*) = new DataTable().render(columns, fun, idOpt, jsonOptions, attrs: _*)

  def apply(
             columns: List[String],
             fun: (DataTableParams) => DataTableSource,
             idOpt: Option[String],
             attrs: (String, String)*) = new DataTable().render(columns, fun, idOpt, attrs: _*)

  def apply(
             columns: List[String],
             fun: (DataTableParams) => DataTableSource,
             id: String,
             attrs: (String, String)*) = new DataTable().render(columns, fun, id, attrs: _*)

  def apply(
             columns: List[String],
             fun: (DataTableParams) => DataTableSource,
             id: String,
             jsonOptions: List[(String, String)],
             attrs: (String, String)*) = new DataTable().render(columns, fun, id, jsonOptions, attrs: _*)
}

abstract class DataTableSource(
                                val totalRecords: Long,
                                val totalDisplayRecords: Long) {
  def jsonData: JValue
}

/**
 * Class for holding data to be return to DataTable
 *
 * Use this class to return data as simple JSON array of arrays
 */
class DataTableArraySource(
                            totalRecords: Long,
                            totalDisplayRecords: Long,
                            val data: List[List[String]])
  extends DataTableSource(totalRecords, totalDisplayRecords) {
  def jsonData = JArray(data.map(r => JArray(r.map(JString(_)))))
}


/**
 * Class for holding data to be return to DataTable.
 *
 * Use this class to return data as JSON array of objects, so you can set DT_RowId and DT_RowClass
 */
class DataTableObjectSource(
                             totalRecords: Long,
                             totalDisplayRecords: Long,
                             val data: List[List[(String, String)]])
  extends DataTableSource(totalRecords, totalDisplayRecords) {
  def jsonData = JArray(data.map(r => JObject(r.map(rr => JField(rr._1, JString(rr._2))))))
}

/**
 * Class for holding params sent by DataTable
 */
class DataTableParams(
                       val displayStart: Long,
                       val displayLength: Long,
                       val columns: Int,
                       val search: String,
                       val regex: Boolean,
                       val searchable: List[Boolean],
                       val searches: List[String],
                       val regexes: List[Boolean],
                       val sortables: List[Boolean],
                       val sortingCols: Int,
                       val sortCols: List[Int],
                       val sortDirs: List[String],
                       val dataProps: List[String])

class DataTable {

  /**
   * Render a DataTable
   *
   * @param columns - Column names
   * @param fun - function that will provide data
   * @param id - Table id
   * @param jsonOptions - additional options for DataTable configuration
   * @param attrs - the attributes that can be added to the table
   */
  def render(
              columns: List[String],
              fun: (DataTableParams) => DataTableSource,
              id: String,
              jsonOptions: List[(String, String)],
              attrs: (String, String)*): Elem = {

    val idOpt = Some(id)
    render(columns, fun, idOpt, jsonOptions, attrs: _*)
  }

  /**
   * Render a DataTable
   *
   * @param columns - Column names
   * @param fun - function that will provide data
   * @param id - Optional table id
   * @param attrs - the attributes that can be added to the table
   */
  def render(
              columns: List[String],
              fun: (DataTableParams) => DataTableSource,
              id: String,
              attrs: (String, String)*): Elem = {

    val jsonOptions: List[(String, String)] = List()
    val idOpt = Some(id)
    render(columns, fun, idOpt, jsonOptions, attrs: _*)
  }

  /**
   * Render a DataTable
   *
   * @param columns - Column names
   * @param fun - function that will provide data
   * @param idOpt - Optional table id
   * @param attrs - the attributes that can be added to the table
   */
  def render(
              columns: List[String],
              fun: (DataTableParams) => DataTableSource,
              idOpt: Option[String],
              attrs: (String, String)*): Elem = {

    val jsonOptions: List[(String, String)] = List()
    render(columns, fun, idOpt, jsonOptions, attrs: _*)
  }

  /**
   * Render a DataTable
   *
   * @param columns - Column names
   * @param fun - function that will provide data
   * @param idOpt - Optional table id
   * @param jsonOptions - additional options for DataTable configuration
   * @param attrs - the attributes that can be added to the table
   */
  def render(
              columns: List[String],
              fun: (DataTableParams) => DataTableSource,
              idOpt: Option[String],
              jsonOptions: List[(String, String)],
              attrs: (String, String)*): Elem = {

    val f = (ignore: String) => {
      val columns = S.param("iColumns").dmap(0)(_.toInt)
      val a = (1 to columns).map(i => S.param("bSearchable_" + i).dmap(false)(_.toBoolean)).toList

      val params = new DataTableParams(
        S.param("iDisplayStart").dmap(0)(_.toInt),
        S.param("iDisplayLength").dmap(0)(_.toInt),
        columns,
        S.param("sSearch").dmap("")(_.toString),
        S.param("bRegex").dmap(false)(_.toBoolean),
        (1 to columns).map(i => S.param("bSearchable_" + i).dmap(false)(_.toBoolean)).toList,
        (1 to columns).map(i => S.param("sSearch_" + i).dmap("")(_.toString)).toList,
        (1 to columns).map(i => S.param("bRegex_" + i).dmap(false)(_.toBoolean)).toList,
        (1 to columns).map(i => S.param("bSortable_" + i).dmap(false)(_.toBoolean)).toList,
        S.param("iSortingCols").dmap(0)(_.toInt),
        (1 to columns).map(i => S.param("iSortCol_" + i).dmap(0)(_.toInt)).toList,
        (1 to columns).map(i => S.param("sSortDir_" + i).dmap("")(_.toString)).toList,
        (1 to columns).map(i => S.param("mDataProp_" + i).dmap("")(_.toString)).toList)

      val source = fun(params)

      val json = ("iTotalRecords" -> source.totalRecords) ~
        ("iTotalDisplayRecords" -> source.totalDisplayRecords) ~
        ("sEcho" -> S.param("sEcho").dmap(0)(_.toInt)) ~
        ("aaData" -> source.jsonData)

      JsonResponse(json)
    }

    fmapFunc(SFuncHolder(f)) {
      func =>
        val where: String = encodeURL(S.contextPath + "/" + LiftRules.ajaxPath + "?" + func + "=foo")

        val id = idOpt getOrElse Helpers.nextFuncName

        val jqOptions = ("bProcessing", "true") ::
          ("bServerSide", "true") ::
          ("sAjaxSource", where.encJs) ::
          Nil ::: jsonOptions
        val json = jqOptions.map(t => t._1 + ":" + t._2).mkString("{", ",", "}")
        val datatableOptions = JsRaw(json)

        val onLoad = JsRaw( """
        $(document).ready(function() {
			$("#""" + id + """").dataTable(""" + datatableOptions.toJsCmd + """);
		});""")

        attrs.foldLeft(
          <table id={id}>
            <head_merge>
              <script type="text/javascript" src="/scripts/jquery.dataTables.js"></script>{Script(onLoad)}
            </head_merge>
            <thead>
              <tr>
                {columns.map(c => <th>
                {c}
              </th>)}
              </tr>
            </thead>
            <tbody>
              <tr>
                {columns.map(_ => <td></td>)}
              </tr>
            </tbody>
          </table>)(_ % _)
    }
  }
}