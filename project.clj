(defproject com.brunobonacci/where "0.3.0-SNAPSHOT"
  :description "Human readable conditions and `filter` best companion."
  :url "https://github.com/BrunoBonacci/where"

  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies []

  :aliases {"build-all-clj"  ["with-profile" "+clj17:+clj18:+clj19" "do" "clean," "midje," "jar"]
            "build-all-cljs" ["with-profile" "+cljs17:+cljs18:+cljs19" "do" "clean," "cljsbuild" "once"]
            "build-all"      ["do" "build-all-clj," "build-all-cljs"]}

  :profiles {:dev {:resource-paths ["test-data"]
                   :dependencies [[midje "1.9.0-alpha5"]
                                  [org.clojure/test.check "0.9.0"]]
                   :plugins [[lein-midje "3.2"]
                             [lein-cljsbuild "1.1.4"]]}
             :repl {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :clj17  {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :clj18  {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :clj19  {:dependencies [[org.clojure/clojure "1.9.0-alpha12"]]}
             :cljs17 {:dependencies [[org.clojure/clojure "1.7.0"]
                                     [org.clojure/clojurescript "1.7.228"]]}
             :cljs18 {:dependencies [[org.clojure/clojure "1.8.0"]
                                     [org.clojure/clojurescript "1.8.51"]]}
             :cljs19 {:dependencies [[org.clojure/clojure "1.9.0-alpha12"]
                                     [org.clojure/clojurescript "1.9.229"]]}}
  :cljsbuild
  {:builds
   [{:source-paths   ["src"]
     :compiler
     {:output-to "./target/where.js"
      :optimizations :whitespace
      :pretty-print true}}]}
  )
