(ns where.test-util
  (:require [clojure.pprint :refer [print-table]])
  (:require [clojure.java.io :as io]))


(defn load-data []
  (->> (read-string (slurp (io/resource "users.edn")))
    (map (partial zipmap [:name :user :age :country :active :scores]))
    (map #(update-in % [:scores] (partial zipmap [:min :last :high])))))


(defn bootstrap []
  (println "Loading test data...")
  (def users (load-data)))


(def ptable
  (partial print-table
    [:name :user :age :country :active :scores]))
