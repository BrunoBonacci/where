(ns where.core
  "Provides a function called `where` which facilitate
  the building of complex predicates for `filter`,
  especially useful with maps."
  (:require [where.operation :as ops]))



(defn- f-and
  "Internal function which applies a logical AND to
  a given list of predicates [f(x)-> true/false].
  It evaluates the predicates one by one and breaks
  the evaluation at the first non truthy response."
  [fs]
  (fn [x]
    (loop [[f1 & fr] fs]
      (if-not f1
        true ;; reached the end
        (if (f1 x) (recur fr) false)))))



(defn- f-or
  "Internal function which applies a logical OR to
  a given list of predicates [f(x)-> true/false].
  It evaluates the predicates one by one and breaks
  the evaluation at the first truthy response."
  [fs]
  (fn [x]
    (loop [[f1 & fr] fs]
      (if-not f1
        false ;; reached the end
        (if (f1 x) true (recur fr))))))



(defn- operator
  [extractor comparator value]
  (if-not (keyword? comparator)
    (fn [item]
      (comparator (extractor item) value))
    (ops/operation extractor comparator value)))



(defn where
  "It builds a predicate function in a more human readable way.

  It takes an `extractor` which applied to the item it will return the
  property you want to compare. Then second argument is the comparator
  which it can be any binary `comparator` function, and it take a
  `value` to compare to.

  For example if you have a collection `users` which contains map with
  the following format:

     [{:name \"Kiayada Wyatt\", :user \"kiayada33\", :age 33,
       :country \"USA\", :active true } ,,,]

  and let' say you want to find all users coming from \"USA\"

     (filter (where :country = \"USA\") users)

  to get the users which are over 18yo you can write.

     (filter (where :age > 18) users)

  `where` can be used not only with maps but also with lists, vectors,
  sets.

     (filter (where > 6) (range 20))

  Additionally you can easily compose condition with logical operators such
  as `:and`, `:or` and `:not`

     (filter (where [:and [:country = \"USA\"] [:age > 18]]) users)


  Finally there are number of built-in operators which provide a easy,
  nil-safe, and expressive way to define common `where` clauses.
  These operators are:

  (*) Strings

  | comparator    | complement (not)  | case-insensitive | insensitive complement |
  |---------------+-------------------+------------------+------------------------|
  | :is?          | :is-not?          | :IS?             | :IS-NOT?               |
  | :starts-with? | :not-starts-with? | :STARTS-WITH?    | :NOT-STARTS-WITH?      |
  | :ends-with?   | :not-ends-with?   | :ENDS-WITH?      | :NOT-ENDS-WITH?        |
  | :contains?    | :not-contains?    | :CONTAINS?       | :NOT-CONTAINS?         |
  | :in?          | :not-in?          | :IN?             | :NOT-IN?               |
  | :matches?     | :not-matches?     | :MATCHES?        | :NOT-MATCHES?          |

  (*) Numbers

  | comparator         | complement (not)       |
  |--------------------+------------------------|
  | :between?          | :not-between?          |
  | :strictly-between? | :not-strictly-between? |
  | :range?            | :not-range?            |
  | :in?               | :not-in?               |


  For more info please visit: https://github.com/BrunoBonacci/where
  "
  ;; it expects a to compose various
  ;; conditions via logical operators
  ;; such as `:and`, `:or`, `:not`
  ([[op & rules :as cond-spec]]
   (if (#{:or :and :not} op)
     (let [cnds (map where rules)]
       (cond
         (= op :or) (f-or cnds)
         (= op :and) (f-and cnds)

         (and (= :not op) (not= 1 (count rules)))
         (ops/raise-error ":not expects exactly one predicate")

         (= op :not) (complement (first cnds))))
     (apply where cond-spec)))

  ;; it expect a collection of values
  ;; which can be direclty understood
  ;; by the comparator therefore it
  ;; doesn't need an extractor.
  ;; (where = value)
  ([comparator value]
   (where identity comparator value))

  ;; this is what you are going tipically to use with
  ;; maps. The first element extract a value of of the
  ;; map and it passes into the comparator.
  ;; (where :field = value)
  ([extractor comparator value]
   (operator extractor comparator value)))
