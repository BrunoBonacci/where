(ns where.core-test-check
  (:use midje.sweet)
  (:require [midje.util :refer [testable-privates]])
  (:require [where.core :refer :all]
            [where.operation :as op])
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))



(def TC_NUM (or (some-> (System/getenv "TC_NUM") (#(Long/parseLong %))) 10))
(println "Generating" TC_NUM "tests for each property to test.")


(def gen-strings-or-nil
  (gen/frequency [[50 gen/string]
                  [50 (gen/return nil)]]))


(def gen-pattern-or-nil
  (gen/frequency [[50 (gen/such-that #(try (re-pattern %) true (catch Exception x false)) gen/string)]
                  [50 (gen/return nil)]]))


(def gen-num
  (gen/frequency [[20 gen/nat]
                  [20 gen/large-integer]
                  [20 gen/double]
                  [20 gen/ratio]
                  [20 (gen/return nil)]]))


(def gen-tuple-num
  (gen/frequency [[80 (gen/tuple gen-num gen-num)]
                  [20 (gen/return nil)]]))


(def gen-any
  (gen/frequency [[80 gen/any]
                  [20 (gen/return nil)]]))

(def gen-list-of-any
  (gen/list gen-any))


(def gen-list-of-strings
  (gen/list gen-strings-or-nil))


(def ops
  ;; operation                input1                input2               negation               insensitive-op
  [[:CONTAINS?               gen-strings-or-nil    gen-strings-or-nil   :NOT-CONTAINS?          nil               ]
   [:ENDS-WITH?              gen-strings-or-nil    gen-strings-or-nil   :NOT-ENDS-WITH?         nil               ]
   [:IN?                     gen-strings-or-nil    gen-list-of-strings  :NOT-IN?                nil               ]
   [:IS-NOT?                 gen-strings-or-nil    gen-strings-or-nil   :NOT-IS-NOT?            nil               ]
   [:IS?                     gen-strings-or-nil    gen-strings-or-nil   :IS-NOT?                nil               ]
   [:MATCHES?                gen-strings-or-nil    gen-pattern-or-nil   :NOT-MATCHES?           nil               ]
   [:MATCHES-EXACTLY?        gen-strings-or-nil    gen-pattern-or-nil   :NOT-MATCHES-EXACTLY?   nil               ]
   [:STARTS-WITH?            gen-strings-or-nil    gen-strings-or-nil   :NOT-STARTS-WITH?       nil               ]
   [:contains?               gen-strings-or-nil    gen-strings-or-nil   :not-contains?          :CONTAINS?        ]
   [:ends-with?              gen-strings-or-nil    gen-strings-or-nil   :not-ends-with?         :ENDS-WITH?       ]
   [:in?                     gen-strings-or-nil    gen-list-of-any      :not-in?                :IN?              ]
   [:is?                     gen-any               gen-any              :is-not?                :IS?              ]
   [:matches?                gen-strings-or-nil    gen-pattern-or-nil   :not-matches?           :MATCHES?         ]
   [:matches-exactly?        gen-strings-or-nil    gen-pattern-or-nil   :not-matches-exactly?   :MATCHES-EXACTLY? ]
   [:starts-with?            gen-strings-or-nil    gen-strings-or-nil   :not-starts-with?       :STARTS-WITH?     ]
   [:range?                  gen-num               gen-tuple-num        :not-range?             nil               ]
   [:between?                gen-num               gen-tuple-num        :not-between?           nil               ]
   [:strictly-between?       gen-num               gen-tuple-num        :not-strictly-between?  nil               ]
   [:NOT-CONTAINS?           gen-strings-or-nil    gen-strings-or-nil   :CONTAINS?              nil               ]
   [:NOT-ENDS-WITH?          gen-strings-or-nil    gen-strings-or-nil   :ENDS-WITH?             nil               ]
   [:NOT-IN?                 gen-strings-or-nil    gen-list-of-strings  :IN?                    nil               ]
   [:NOT-IS-NOT?             gen-strings-or-nil    gen-strings-or-nil   :IS-NOT?                nil               ]
   [:IS-NOT?                 gen-strings-or-nil    gen-strings-or-nil   :IS?                    nil               ]
   [:NOT-MATCHES?            gen-strings-or-nil    gen-pattern-or-nil   :MATCHES?               nil               ]
   [:NOT-MATCHES-EXACTLY?    gen-strings-or-nil    gen-pattern-or-nil   :MATCHES-EXACTLY?       nil               ]
   [:NOT-STARTS-WITH?        gen-strings-or-nil    gen-strings-or-nil   :STARTS-WITH?           nil               ]
   [:not-contains?           gen-strings-or-nil    gen-strings-or-nil   :contains?              :NOT-CONTAINS?    ]
   [:not-ends-with?          gen-strings-or-nil    gen-strings-or-nil   :ends-with?             :NOT-ENDS-WITH?   ]
   [:not-in?                 gen-strings-or-nil    gen-list-of-any      :in?                    :NOT-IN?          ]
   [:is-not?                 gen-any               gen-any              :is?                    :IS-NOT?          ]
   [:not-matches?            gen-strings-or-nil    gen-pattern-or-nil   :matches?               :NOT-MATCHES?     ]
   [:not-matches-exactly?    gen-strings-or-nil    gen-pattern-or-nil   :matches-exactly?       :NOT-MATCHES-EXACTLY?     ]
   [:not-starts-with?        gen-strings-or-nil    gen-strings-or-nil   :starts-with?           :NOT-STARTS-WITH? ]
   [:not-range?              gen-num               gen-tuple-num        :range?                 nil               ]
   [:not-between?            gen-num               gen-tuple-num        :between?               nil               ]
   [:not-strictly-between?   gen-num               gen-tuple-num        :strictly-between?      nil               ]
   ])



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                     ---==| N I L   S A F E T Y |==----                     ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def prop-all-ops-are-nil-safe
  (prop/for-all
    [[v1 op v2 op-not] (gen/bind (gen/elements ops)
                         (fn [[op input1 input2 op-not]]
                           (gen/tuple
                             input1
                             (gen/return op)
                             input2
                             (gen/return op-not)))) ]

    (try
      ((where identity op v2) v1)
      true
      (catch NullPointerException x
        false))))



(fact "All operations are nil safe" :test-check
  (tc/quick-check TC_NUM prop-all-ops-are-nil-safe
    :max-size 50)
  => (contains {:result true}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;               ---==| C A S E   I N S E N S I T I V E |==----               ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- lower
  [^String v]
  (when v
    (if (string? v)
      (.toLowerCase v)
      v)))


(def prop-all-string-operation-support-case-insensitiveness
  (prop/for-all
    [[v1 op v2 insentive-op] (gen/bind (gen/elements
                                         (filter (fn [[_ _ _ _ insensitive]]
                                                   insensitive)
                                           ops))

                               (fn [[op input1 input2 _ insentive-op]]
                                 (gen/tuple
                                   (cond
                                     (= op :in?)     gen-strings-or-nil
                                     (= op :not-in?) gen-strings-or-nil
                                     (= op :is?)     gen-strings-or-nil
                                     (= op :is-not?) gen-strings-or-nil
                                     :else           input1)
                                   (gen/return op)
                                   (cond
                                     (= op :in?)     gen-list-of-strings
                                     (= op :not-in?) gen-list-of-strings
                                     (= op :is?)     gen-strings-or-nil
                                     (= op :is-not?) gen-strings-or-nil
                                     :else           input2)
                                   (gen/return insentive-op)))) ]

    (=
      ((where identity insentive-op v2) v1)
      ((where identity op (lower v2)) (lower v1)))))



(fact "All string operations must support case insenstiveness" :test-check

  (tc/quick-check TC_NUM prop-all-string-operation-support-case-insensitiveness
    :max-size 50)
  => (contains {:result true}))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                  ---==| N E G A T I O N   T E S T |==----                  ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



(def prop-all-NOT-operators-are-just-the-negation-of-the-positive-operator
  (prop/for-all
    [[v1 op v2 op-not] (gen/bind (gen/elements ops)
                         (fn [[op input1 input2 op-not]]
                           (gen/tuple
                             input1
                             (gen/return op)
                             input2
                             (gen/return op-not)))) ]

    (=
      (boolean ((where identity op v2) v1))
      (boolean (not ((where identity op-not v2) v1))))))



(fact "Negation are just the complement of the forward operation" :test-check

  (tc/quick-check
    TC_NUM
    prop-all-NOT-operators-are-just-the-negation-of-the-positive-operator
    :max-size 50)
  => (contains {:result true}))
