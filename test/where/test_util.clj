(ns where.test-util
  (:require [clojure.pprint :refer [print-table]])
  (:require [clojure.java.io :as io]))


(defn load-data []
  (map (partial zipmap [:name :user :age :country :active])
       (read-string (slurp (io/resource "users.edn")))))


(defn bootstrap []
  (println "Loading test data...")
  (def users (load-data)))


(def ptable
  (partial print-table
   [:name :user :age :country :active]))
