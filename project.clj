(defproject excel-exporter "0.1.0-SNAPSHOT"
  :description "mini service to export excel data"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 ;[compojure "1.1.5"]
                 [org.apache.poi/poi "3.9"]
                 [org.apache.poi/poi-ooxml "3.9"]
                 [org.clojure/tools.trace "0.7.3"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.2.1"]
                 [org.clojure/data.csv "0.1.2"]
                 [hiccup "1.0.2"]
                 [liberator "0.9.0"]
                 ]
  :plugins [[lein-midje "2.0.3"]
            [lein-ring "0.7.1" :exclusions [org.clojure/clojure]]]
  :ring {:handler examples.server/handler
         :adapter {:port 8000}}
  :profiles
  {:dev {:dependencies [[ring/ring-jetty-adapter "1.1.8"]
                        [ring-mock "0.1.5"]
                        [ring/ring-devel "1.1.8"]
                        [compojure "1.0.2" :exclusions [org.clojure/tools.macro]] ;; only for examples
                        [midje "1.5.0"]
                        [bultitude "0.2.2"]
                        [org.clojure/clojurescript "0.0-1450"]]}})
