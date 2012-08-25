package bootstrap.liftweb

import net.liftweb.http.LiftRules
import net.liftweb._
import common.Full
import http.Html5Properties
import mongodb.MongoDB
import sitemap.Loc.LocGroup
import util._
import Helpers._
import common._
import http._
import js.jquery.JqJsCmds.FadeIn
import sitemap._
import Loc._
import mapper._
import net.liftweb.squerylrecord.RecordTypeMode._
import net.liftmodules.FoBo._
import net.liftmodules.{JQueryModule, FoBo}
import code.model._
import code.snippet._
import com.mongodb.Mongo

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {
  def boot {

    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr
            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
          Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    Schemifier.schemify(true, Schemifier.infoF _, Post)
    S.addAround(DB.buildLoanWrapper)


    //URL REWRITE
    LiftRules.rewrite.append {
      case RewriteRequest(
              ParsePath(List("view"),"html",true,false),_,_) =>
             RewriteResponse("view" :: Nil,"")
    }


    //mongodbConfig.init()

    // init auth-mongo
  //  MongoAuth.siteName.default.set("lift-mongo-app")
  //  MongoAuth.systemEmail.default.set("info@") // TODO: Set me
   // MongoAuth.systemUsername.default.set("lift-mongo-app Staff")



    // where to search snippet
    LiftRules.addToPackages("code")
    //We skip the FoBo built in JQuery in favor for the FoBo included lift-jquery-module
    //FoBo.InitParam.JQuery=FoBo.JQuery171  
    FoBo.InitParam.ToolKit=FoBo.Bootstrap210
    FoBo.InitParam.ToolKit=FoBo.PrettifyJun2011
    FoBo.init()





    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index",
      Menu.i("view") / "view",
      Menu.i("A1") / "a1" >> Hidden >> LocGroup("bdd"),
      Menu.i("A2") / "a2" >> Hidden >> LocGroup("bdd"),
      Menu.i("A3") / "a3" >> Hidden >> LocGroup("bdd"),

      Menu.i("Level 1.1.1") / "page111" >> Hidden >> LocGroup("bdd11"),
      Menu.i("Level 1.1.2") / "page112" >> Hidden >> LocGroup("bdd11"),
      Menu.i("Level 1.1.3") / "page113" >> Hidden >> LocGroup("bdd11"),   
      Menu.i("Level 1.1.4") / "page114" >> Hidden >> LocGroup("bdd11"),
      
      Menu.i("Level 1.2.1") / "page121" >> Hidden >> LocGroup("bdd12"),
      Menu.i("Level 1.2.2") / "page122" >> Hidden >> LocGroup("bdd12"),
    
      Menu.i("Level 1.3") / "page13" >> Hidden >> LocGroup("bdd1"),
      Menu.i("Level 1.4") / "page14" >> Hidden >> LocGroup("bdd1"),   
      Menu.i("Level 1.5") / "page15" >> Hidden >> LocGroup("bdd1"),      
                     
      
      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Bootstrap", Link(List("bootstrap-2.0.4"), true, "/bootstrap-2.0.4/index"),
        "Bootstrap")))

    //def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemap /*sitemapMutators(sitemap)*/ )


  //  LiftRules.unloadHooks.append(() => MongoDB.close)

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    //LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    //Modal window using jquery's UIblock

   

    
  }
}
