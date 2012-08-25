package code.snippet

import code.view._

class DemoTable {
  def table = {
    val cols = "Col1" :: "Col2" :: Nil

    // this function returns data to populate table based on parameters passed in
    val fun = (params: DataTableParams) => {
      val rows = List(("row1_col1", "row1_col2"), ("row2_col1", "row2_col2"))
      val count = 2

      new DataTableObjectSource(count, count, rows.map(r =>
        List(("0", r._1),
          ("1", r._2),
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