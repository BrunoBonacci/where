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


       (->> (filter (where (comp :high :scores) > 9950) users)
            count)
       => 4
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
       => (just [6 7 8 9] :in-any-order)

       )



(facts "about `where`: wrapping with brachets has the same behaviour of unwrapped"

       ((where [:a = 1]) {:a 1})                =>      truthy
       ((where [:a = 1]) {:a 2})                =>      falsey
       ((where [:a = 1]) {:b 1})                =>      falsey
       ((where [:a not= 1]) {:b 1})             =>      truthy
       ((where [= 1]) 1)                        =>      truthy
       ((where [not= 5]) 1)                     =>      truthy
       ((where [> 5]) 3)                        =>      falsey

       )



(facts "about `where`: composing predicates with logical and"

       ((where [:and [:a = 1] [:b = 2]])   {:a 1 :b 2})    =>      truthy
       ((where [:and [:a = 1] [:b = 2]])   {:a 1 :b 1})    =>      falsey
       ((where [:and [:a = 1] [:b = 2]])   {:a 2 :b 2})    =>      falsey
       ((where [:and [:a = 1] [:b > 1]])   {:a 1 :b 2})    =>      truthy

       ((where [:and [:a = 1] [:b > 1] [:b < 6]])   {:a 1 :b 2})    =>      truthy
       ((where [:and [:a = 1] [:b > 1] [:b < 6]])   {:a 1 :b 6})    =>      falsey


       (count
        (filter (where  [:and
                         [:country = "Russia"]
                         [:age     > 35]
                         [:age     < 60]]) users)) => 15
       )



(facts "about `where`: composing predicates with logical or"

       ((where [:or [:a = 1] [:b = 2]])   {:a 1 :b 2})    =>      truthy
       ((where [:or [:a = 1] [:b = 2]])   {:a 1 :b 1})    =>      truthy
       ((where [:or [:a = 1] [:b = 2]])   {:a 3 :b 2})    =>      truthy

       ((where [:or [:a = 1] [:b = 2]])   {:a 0 :b 0})    =>      falsey

       ((where [:or [:a = 1] [:b < 1] [:b > 6]])   {:a 1 :b 2})    =>      truthy
       ((where [:or [:a = 1] [:b < 1] [:b > 6]])   {:a 2 :b 0})    =>      truthy
       ((where [:or [:a = 1] [:b < 1] [:b > 6]])   {:a 2 :b 9})    =>      truthy
       ((where [:or [:a = 1] [:b < 1] [:b > 6]])   {:a 2 :b 2})    =>      falsey

       )


(facts "about `where`: composing predicates with both logical operators"


       (->> (filter (where [:or [:country = "USA"] [:country = "Russia"]]) users)
            (map :country)
            (into #{}))
       => #{"USA" "Russia"}

       (let [result (filter (where [:and [:or [:country = "USA"] [:country = "Russia"]]
                                    [:active = true]]) users)]
         (->> result (map :country) (into #{}))
         => #{"USA" "Russia"}

         (some (complement :active) result)
         => falsey

         )

       ;; outer AND composition
       (count
        (filter (where  [:and
                         [:or
                          [:country = "Russia"]
                          [:country = "USA"]]
                         [:age     > 35]
                         [:age     < 60]])
                users))  => 30


       ;; outer OR composition
       (count
        (filter (where  [:or
                         [:and
                          [:age = 35]
                          [:country = "USA"]]
                         [:and
                          [:age > 35]
                          [:age < 55]
                          [:country = "Russia"]]]
                        ) users))  => 10
       )





(tabular
 (fact "`where`: with DSL operators with strings"

        ((where ?operator ?target) ?value) => ?result)

 ?value        ?operator          ?target        ?result
 "value"       :is?               "value"        truthy
 "value1"      :is?               "value"        falsey

 "value"       :is-not?           "value"        falsey
 "value1"      :is-not?           "value"        truthy

 "value"       :starts-with?      "val"          truthy
 "notval"      :starts-with?      "value"        falsey
 "VALUE"       :starts-with?      "val"          falsey
 nil           :starts-with?      "value"        falsey
 "value"       :starts-with?      nil            falsey

 "value"       :ends-with?        "lue"          truthy
 "notval"      :ends-with?        "value"        falsey
 "VALUE"       :ends-with?        "lue"          falsey
 nil           :ends-with?        "value"        falsey
 "value"       :ends-with?        nil            falsey

 "values"      :contains?         "lue"          truthy
 "notval"      :contains?         "value"        falsey
 "VALUE"       :contains?         "alu"          falsey
 nil           :contains?         "value"        falsey
 "value"       :contains?         nil            falsey

 "values"      :not-contains?     "lue"          falsey
 "notval"      :not-contains?     "value"        truthy
 nil           :not-contains?     "value"        truthy
 "value"       :not-contains?     nil            truthy

 "value 123"   :matches?           #"\w+ \d+"    truthy
 "value 123"   :matches?           #"\d+"        truthy
 "value 123"   :matches?           #"^\d+$"      falsey
 "123"         :matches?           #"^\d+$"      truthy
 nil           :matches?           #"^\d+$"      falsey
 "123"         :matches?           nil           falsey
 )



(tabular
 (fact "`where`: with DSL operators with numbers"

        ((where ?operator ?target) ?value) => ?result)

 ?value        ?operator          ?target        ?result
 42            :between?          [35 45]        truthy
 35            :between?          [35 45]        truthy
 45            :between?          [35 45]        truthy
 34            :between?          [35 45]        falsey
 46            :between?          [35 45]        falsey

 42            :strictly-between? [35 45]        truthy
 35            :strictly-between? [35 45]        falsey
 45            :strictly-between? [35 45]        falsey
 34            :strictly-between? [35 45]        falsey
 46            :strictly-between? [35 45]        falsey

 42            :range?            [35 45]        truthy
 35            :range?            [35 45]        truthy
 45            :range?            [35 45]        falsey
 34            :range?            [35 45]        falsey
 46            :range?            [35 45]        falsey
 )


(tabular
 (fact "`where`: with DSL generic operators"

        ((where ?operator ?target) ?value) => ?result)

 ?value        ?operator          ?target          ?result
 "v1"          :in?               ["v1" "v2" "v3"] truthy
 "v3"          :in?               ["v1" "v2" "v3"] truthy
 "not-present" :in?               ["v1" "v2" "v3"] falsey
 "not-present" :in?               []               falsey
 "not-present" :in?               nil              falsey
 nil           :in?               ["v1" "v2" "v3"] falsey
 nil           :in?               nil              falsey

 )


(tabular
 (fact "`where`: with DSL operators with strings"

        ((where ?operator ?target) ?value) => ?result)

 ?value        ?operator          ?target        ?result
 "value"       :STARTS-WITH?      "val"          truthy
 "notval"      :STARTS-WITH?      "value"        falsey
 "VALUE"       :STARTS-WITH?      "val"          truthy
 nil           :STARTS-WITH?      "value"        falsey
 "value"       :STARTS-WITH?      nil            falsey

 "value"       :ENDS-WITH?        "lue"          truthy
 "notval"      :ENDS-WITH?        "value"        falsey
 "VALUE"       :ENDS-WITH?        "lue"          truthy
 nil           :ENDS-WITH?        "value"        falsey
 "value"       :ENDS-WITH?        nil            falsey

 "values"      :CONTAINS?         "lue"          truthy
 "notval"      :CONTAINS?         "value"        falsey
 "VALUE"       :CONTAINS?         "alu"          truthy
 nil           :CONTAINS?         "value"        falsey
 "value"       :CONTAINS?         nil            falsey

 "values"      :NOT-CONTAINS?     "lue"          falsey
 "values"      :NOT-CONTAINS?     "LUE"          falsey
 "notval"      :NOT-CONTAINS?     "value"        truthy
 nil           :NOT-CONTAINS?     "value"        truthy
 "value"       :NOT-CONTAINS?     nil            truthy

 "value 123"   :MATCHES?           #"VAL\w+ \d+" truthy
 "value 123"   :MATCHES?           #"\d+"        truthy
 "value 123"   :MATCHES?           #"^\d+$"      falsey
 "123"         :MATCHES?           #"^\d+$"      truthy
 nil           :MATCHES?           #"^\d+$"      falsey
 "123"         :MATCHES?           nil           falsey
 )
