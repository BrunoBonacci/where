(ns where.core-test
  (:use midje.sweet)
  (:require [midje.util :refer [testable-privates]])
  (:require [where.core :refer :all]))

(testable-privates where.core f-and f-or)


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
