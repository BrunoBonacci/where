(defproject where "0.1.0-SNAPSHOT"
  :description "Human readable conditions and `filter` best companion."
  :url "https://github.com/BrunoBonacci/where"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]]

  :profiles {:uberjar {:aot :all}
             :dev {:resource-paths ["test-data"]
                   :dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}})
