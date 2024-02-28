import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import $ivy.`io.indigoengine::mill-indigo:0.16.0`, millindigo._

object mygame extends ScalaJSModule with MillIndigo {
  def scalaVersion = "3.3.1"
  def scalaJSVersion = "1.14.0"

  def assetPath = T{ millSourcePath / "assets"}

  val indigoOptions: IndigoOptions =
    IndigoOptions.defaults
      .withTitle("My Game")
      .withWindowSize(720, 720)
      .withAssetDirectory( (millSourcePath / "assets").toString() )

  val indigoGenerators: IndigoGenerators =
    IndigoGenerators.None

  println(indigoOptions)

  def ivyDeps = Agg(
    ivy"io.indigoengine::indigo::0.16.0",
    ivy"io.indigoengine::indigo-extras::0.16.0",
    ivy"io.indigoengine::indigo-json-circe::0.16.0"
  )

  def template: T[PathRef] = T{
    os.makeDir(T.dest / "scripts")
    val pathHtml = T.dest / "index.html"
    os.write.over(
      pathHtml,
      indigoplugin.templates.HtmlTemplate.template("My Game", true, "main.js", "white")
    )

    val cordovaPath = T.dest / "cordova.js"
    os.write.over(cordovaPath, "")

    val indigoSupportPath = T.dest / "scripts" / "indigo-support.js"

    os.write.over(
      indigoSupportPath,
      """window.onload = function () {
    if (typeof history.pushState === "function") {
        history.pushState("jibberish", null, null);
        window.onpopstate = function () {
            history.pushState('newjibberish', null, null);
            // Handle the back (or forward) buttons here
            // Will NOT handle refresh, use onbeforeunload for this.
        };
    }
    else {
        var ignoreHashChange = true;
        window.onhashchange = function () {
            if (!ignoreHashChange) {
                ignoreHashChange = true;
                window.location.hash = Math.random();
                // Detect and redirect change here
                // Works in older FF and IE9
                // * it does mess with your hash symbol (anchor?) pound sign
                // delimiter on the end of the URL
            }
            else {
                ignoreHashChange = false;
            }
        };
    }
}"""
    )

    PathRef(T.dest)
  }

  def servable = T {
    val fl = fastLinkJS()
    val t = template()
    os.copy.over(assetPath(), T.dest / "assets", createFolders = true)
    os.copy.over(fl.dest.path, T.dest / "scripts", createFolders = true)
    os.copy(t.path, T.dest, mergeFolders = true, replaceExisting = true)
    PathRef(T.dest)
  }

}
