(ns where.core
  "Provides a function called `where` which facilicate
  the building of complex predicates for `filter`,
  especially useful with maps.")


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


(defn where
  "It builds a predicate function in a more human readable way.

  It takes an `extractor` which applied to the item it will return the
  property you want to compare. Then second argument is the compartor
  which it can be any binary `comparator` function, and it take a
  `value` to compare to.

  For example if you have a collection `users` which contains map with
  the following format:

     [{ :name \"Kiayada Wyatt\", :user \"kiayada33\", :age 33,
       :country \"USA\", :active true } ,,,]

  and let' say you want to find all users coming from \"USA\"

     (filter (where :country = \"USA\") users)

  to get the users which are over 18yo you can write.

     (filter (where :age > 18) users)

  `where` can be used not only with maps but also with lists, vectors,
  sets.

     (filter (where > 6) (range 20))

  Finally you can easily compose condition with logical operators such
  as `:and` and `:or`

     (filter (where [:and [:country = \"USA\"] [:age > 18]]) users)

  For more info please visit: https://github.com/BrunoBonacci/where
  "
  ;; it expects a to compose various
  ;; conditions via logical operators
  ;; such as `:and`, `:or`
  ([[op & rules :as cond-spec]]
   (if (#{:or :and} op)
     (let [cnds (map where rules)]
       (case op
         :or (f-or cnds)
         :and (f-and cnds)))
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
   (fn [item]
     (comparator (extractor item) value))))
