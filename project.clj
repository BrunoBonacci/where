(defproject com.brunobonacci/where "0.5.5"
  :description "Human readable conditions and `filter` best companion."
  :url "https://github.com/BrunoBonacci/where"

  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies []

  :aliases {"build-all-clj"  ["with-profile" "+clj17:+clj18:+clj19:+clj110" "do" "clean," "midje," "jar"]
            "build-all-cljs" ["with-profile" "+cljs17:+cljs18:+cljs19:+cljs110" "do" "clean," "cljsbuild" "once"]
            "build-all"      ["do" "build-all-clj," "build-all-cljs"]}

  :profiles {:dev {:resource-paths ["test-data"]
                   :dependencies [[midje "1.9.4"]
                                  [org.clojure/test.check "0.9.0"]]
                   :plugins [[lein-midje "3.2"]
                             [lein-cljsbuild "1.1.4"]]}
             :repl {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :clj17  {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :clj18  {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :clj19  {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :clj110 {:dependencies [[org.clojure/clojure "1.10.1"]]}
             :cljs17 {:dependencies [[org.clojure/clojure "1.7.0"]
                                     [org.clojure/clojurescript "1.7.228"]]}
             :cljs18 {:dependencies [[org.clojure/clojure "1.8.0"]
                                     [org.clojure/clojurescript "1.8.51"]]}
             :cljs19 {:dependencies [[org.clojure/clojure "1.9.0"]
                                     [org.clojure/clojurescript "1.9.946"]]}
             :cljs110 {:dependencies [[org.clojure/clojure "1.10.1"]
                                     [org.clojure/clojurescript "1.10.520"]]}}
  :cljsbuild
  {:builds
   [{:source-paths   ["src"]
     :compiler
     {:output-to "./target/where.js"
      :optimizations :whitespace
      :pretty-print true}}]}
  )
