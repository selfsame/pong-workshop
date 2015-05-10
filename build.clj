(require 'cljs.repl)
(require 'cljs.closure)

(cljs.closure/watch "src"
	{:main 'pong.core
	:output-to "out/main.js"
	:verbose true})
