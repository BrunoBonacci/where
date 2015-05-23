(defproject com.brunobonacci/where "0.1.0"
  :description "Human readable conditions and `filter` best companion."
  :url "https://github.com/BrunoBonacci/where"

  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies [[org.clojure/clojure "1.6.0"]]

  :profiles {:dev {:resource-paths ["test-data"]
                   :dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}}

  :deploy-repositories[["clojars" {:url "https://clojars.org/repo/"
                                   :sign-releases false}]])
