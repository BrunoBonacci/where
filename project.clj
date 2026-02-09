(defproject com.brunobonacci/where "0.5.6"
  :description "Human readable conditions and `filter` best companion."
  :url "https://github.com/BrunoBonacci/where"

  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies []

  :aliases {"test" "midje"
            "build-all-clj"  ["with-profile" "+clj7:+clj8:+clj9:+clj10:+clj11:+clj12"
                              "do" "clean," "test," "jar"]
            "build-all-cljs" ["with-profile" "+cljs7:+cljs8:+cljs9:+cljs10:+cljs11:+cljs12"
                              "do" "clean," "cljsbuild" "once"]
            "build-all"      ["do" "build-all-clj," "build-all-cljs"]}

  :profiles {:dev {:resource-paths ["test-data"]
                   :dependencies [[midje "1.10.10"]
                                  [org.clojure/test.check "0.9.0"]]
                   :plugins [[lein-midje "3.2.1"]
                             [lein-cljsbuild "1.1.8"]]}
             :repl {:dependencies [[org.clojure/clojure "1.12.4"]]}
             :clj7  {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :clj8  {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :clj9  {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :clj10 {:dependencies [[org.clojure/clojure "1.10.3"]]}
             :clj11 {:dependencies [[org.clojure/clojure "1.11.4"]]}
             :clj12 {:dependencies [[org.clojure/clojure "1.12.4"]]}

             :cljs7 {:dependencies [[org.clojure/clojure "1.7.0"]
                                     [org.clojure/clojurescript "1.7.228"]]}
             :cljs8 {:dependencies [[org.clojure/clojure "1.8.0"]
                                     [org.clojure/clojurescript "1.8.51"]]}
             :cljs9 {:dependencies [[org.clojure/clojure "1.9.0"]
                                     [org.clojure/clojurescript "1.9.946"]]}
             :cljs10 {:dependencies [[org.clojure/clojure "1.10.3"]
                                      [org.clojure/clojurescript "1.10.914"]]}
             :cljs11 {:dependencies [[org.clojure/clojure "1.11.4"]
                                      [org.clojure/clojurescript "1.11.132"]]}
             :cljs12 {:dependencies [[org.clojure/clojure "1.12.4"]
                                      [org.clojure/clojurescript "1.12.134"]]}}
  :cljsbuild
  {:builds
   [{:source-paths   ["src"]
     :compiler
     {:output-to "./target/where.js"
      :optimizations :whitespace
      :pretty-print true}}]}
  )
