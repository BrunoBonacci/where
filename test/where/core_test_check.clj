(ns where.core-test-check
  (:use midje.sweet)
  (:require [midje.util :refer [testable-privates]])
  (:require [where.core :refer :all]
            [where.operation :as op])
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))



(def case-sensitive-string-ops
  [:contains? :starts-with? :ends-with? :is? :is-not?])

(def case-insensitive-string-ops
  [:CONTAINS? :STARTS-WITH? :ENDS-WITH? :IS? :IS-NOT?])

(def string-ops
  (concat case-sensitive-string-ops case-insensitive-string-ops))

(def gen-strings-or-nil
  (gen/frequency [[50 gen/string]
                  [50 (gen/return nil)]]))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;                     ---==| N I L   S A F E T Y |==----                     ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def prop-all-string-ops-are-nil-safe
  (prop/for-all
   [v1 gen-strings-or-nil
    op (gen/elements string-ops)
    v2 gen-strings-or-nil]

   (fact "prop all string ops are nil safe"
         ((where identity op v2) v1)
         =not=> (throws NullPointerException))))



(def prop-all-string-ops-are-nil-safe-special-in
  (prop/for-all
   [v1 gen-strings-or-nil
    op (gen/elements [:in? :IN?])
    v2 (gen/frequency [[50 (gen/list gen-strings-or-nil)]
                       [50 (gen/return nil)]])]

   (fact "prop all string ops are nil safe special in"
         ((where identity op v2) v1)
         =not=> (throws NullPointerException))))



(fact "All string operations are nil safe" :test-check
      (tc/quick-check 1000 prop-all-string-ops-are-nil-safe)
      => (contains {:result true})

      (tc/quick-check 1000 prop-all-string-ops-are-nil-safe-special-in)
      => (contains {:result true}))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;               ---==| C A S E   I N S E N S I T I V E |==----               ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn- lower
  [^String v]
  (when v
    (.toLowerCase v)))


(def case-op-map
  (zipmap case-insensitive-string-ops case-sensitive-string-ops))


(def prop-all-string-operation-support-case-insensitiveness
  (prop/for-all
   [v1 gen-strings-or-nil
    op (gen/elements case-insensitive-string-ops)
    v2 gen-strings-or-nil]

   (fact "prop all string operation support case insensitiveness"
         ((where identity op v2) v1)
         =>
         ((where identity (case-op-map op) (lower v2)) (lower v1)))))



(def prop-all-string-operation-support-case-insensitiveness-special-in
  (prop/for-all
   [v1 gen-strings-or-nil
    op (gen/return :IN?)
    v2 (gen/frequency [[50 (gen/list gen-strings-or-nil)]
                       [50 (gen/return nil)]])]

   (fact "prop all string operation support case insensitiveness special in"
         ((where identity op v2) v1)
         =>
         ((where identity :in? (when v2 (map lower v2))) (lower v1)))))



(fact "All string operations must support case insenstiveness" :test-check

      (tc/quick-check 1000 prop-all-string-operation-support-case-insensitiveness)
      => (contains {:result true})

      (tc/quick-check 1000 prop-all-string-operation-support-case-insensitiveness-special-in)
      => (contains {:result true}))
