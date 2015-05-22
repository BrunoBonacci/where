(ns where.core-test
  (:use midje.sweet)
  (:use where.test-util)
  (:require [midje.util :refer [testable-privates]])
  (:require [where.core :refer :all]))

(testable-privates where.core f-and f-or)

;; Bootstrapping testing data
(bootstrap)

(facts "about `where`: with three paraters extractor, comparator, value"

       ((where :a = 1) {:a 1})                =>      truthy
       ((where :a = 1) {:a 2})                =>      falsey
       ((where :a = 1) {:b 1})                =>      falsey
       ((where :a not= 1) {:b 1})             =>      truthy

       )



(facts "about `where`: with nested maps"

       ((where (comp :b :a) = 1) {:a {:b 1}})           =>      truthy
       ((where (comp :b :a) = 1) {:a {:b 2}})           =>      falsey
       ((where (comp :b :a) = 1) {:c {:b 1}})           =>      falsey
       ((where (comp :b :a) = 1) {:a {:c 1}})           =>      falsey

       )



(facts "about `where`: with two paraters comparator, value"

       ((where = 1) 1)                        =>      truthy
       ((where not= 5) 1)                     =>      truthy
       ((where > 5) 3)                        =>      falsey

       )



(facts "about `where`: stuff you can do with maps"

       ;; simple map filtering
       (->> (filter (where :country = "USA") users)
            (map :country)
            (into #{}))
       => #{"USA"}


       (->> (filter (where :country not= "USA") users)
            (map :country)
            (into #{}))
       =not=> (contains #{"USA"})


       (->> (filter (where :age > 18) users)
            (map :age)
            (reduce min))
       => 19


       (->> (filter (where (comp :high :scores) > 9000) users)
            count)
       => 134
       )



(facts "about `where`: stuff you can do with lists"

       ;; simple list filtering
       (filter (where > 5) (range 10))
       => [6 7 8 9]


       (let [square #(* % %)]
         (filter (where square < 50) (range 10)))
       => [0 1 2 3 4 5 6 7]
       )



(facts "about `where`: stuff you can do with sets"

       ;; simple list filtering
       (filter (where > 5) #{0 1 2 3 4 5 6 7 8 9} )
       => (contains [6 7 8 9] :in-any-order)

       )
