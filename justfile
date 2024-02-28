# Compile, package, serve and open the browser - all in one command
default: package serve open-browser

# Compile the Scala code to JavaScript
package:
  mill mygame.servable

# Continuously compile the Scala code to JavaScript
packageW:
  mill show mygame.servable -w

# Serve the directory (a background task) out onport 8000
serve:
  $JAVA_HOME/bin/jwebserver -d {{invocation_directory()}}/out/mygame/servable.dest -p 8001 &

setup-ide:
  scala-cli setup-ide .

# Might open a browser in a platform independent way.
open-browser:
  if [[ "$(uname)" == "Darwin" ]]; then \
      open -a Safari http://localhost:8001; \
  elif [[ "$(expr substr $(uname -s) 1 5)" == "Linux" ]]; then \
      google-chrome http://localhost:8001; \
  elif [[ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ]]; then \
      start chrome ; \
  fi

