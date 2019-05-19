(defproject pixcell-clj "0.1.0-SNAPSHOT"
  :description "PixCell: simple PixelArt editing web-app"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler pixcell-clj.handler/app}
  :profiles
  {:uberjar {:aot :all}})
