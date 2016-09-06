(defproject com.brunobonacci/where "0.3.0-SNAPSHOT"
  :description "Human readable conditions and `filter` best companion."
  :url "https://github.com/BrunoBonacci/where"

  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies [[org.clojure/clojure "1.7.0"]]

  :profiles {:dev {:resource-paths ["test-data"]
                   :dependencies [[midje "1.8.3"]]
                   :plugins [[lein-midje "3.2"]]}}
)
