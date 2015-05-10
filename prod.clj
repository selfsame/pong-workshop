(require 'cljs.build.api)

(cljs.build.api/build "src"
	{:main 'pong.core
	:output-to "out/main.js"
   :optimizations :advanced})

(System/exit 0)
