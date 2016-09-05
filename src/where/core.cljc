(ns where.core
  "Provides a function called `where` which facilitate
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



(def ^{:private true} operators-map
  (let [_gen  {:is      (fn [extractor _ value]
                          (fn [item]
                            (= (extractor item) value)))
               :is-not  (fn [extractor _ value]
                          (fn [item]
                            (not= (extractor item) value)))
               :in?     (fn [extractor _ values]
                          (let [vs (set values)]
                            (fn [item]
                              (vs (extractor item)))))
               }
        ;;
        ;; String operators
        ;;
        _str {
              :starts-with? (fn [extractor _ ^String value]
                              (fn [item]
                                (let [^String s (extractor item)]
                                  (when (and s value)
                                    (.startsWith s value)))))

              :ends-with?   (fn [extractor _ ^String value]
                              (fn [item]
                                (let [^String s (extractor item)]
                                  (when (and s value)
                                    (.endsWith s value)))))

              :contains?    (fn [extractor _ ^String value]
                              (fn [item]
                                (let [^String s (extractor item)]
                                  (when (and s value)
                                    (not= -1 (.indexOf s value))))))

              :matches?    (fn [extractor _ ^java.util.regex.Pattern value]
                             (fn [item]
                               (let [^String s (extractor item)]
                                 (when (and s value)
                                   (re-find value s)))))

              ;; TODO: :matches-date?
              ;; TODO: :like?
              }
        _str-not (->> _str
                      (mapcat (fn [[op f]]
                                [[op f]
                                 [(keyword (str "not-" (name op)))
                                  (fn [extractor comparator value]
                                    (complement
                                     (f extractor comparator value)))]]))
                      (into {}))
        ;;
        ;; Numerical operators
        ;;
        _num {:between? (fn [extractor _ [v1 v2]]
                          (let [low (min v1 v2)
                                high (max v1 v2)]
                            (fn [item]
                              (<= low item high))))

              :strictly-between? (fn [extractor _ [v1 v2]]
                                   (let [low (min v1 v2)
                                         high (max v1 v2)]
                                     (fn [item]
                                       (< low item high))))

              :range?   (fn [extractor _ [v1 v2]]
                          (let [low (min v1 v2)
                                high (max v1 v2)]
                            (fn [item]
                              (or (= low item) (< low item high)))))}]
    (merge _gen _str _str-not _num)))



(defn- operator
  [extractor comparator value]
  (if-not (keyword? comparator)
    (fn [item]
      (comparator (extractor item) value))
    (if-let [op (get operators-map comparator)]
      (op extractor comparator value)
      (throw (IllegalArgumentException.
              (str "Illegal comparator: " comparator))))))



(defn where
  "It builds a predicate function in a more human readable way.

  It takes an `extractor` which applied to the item it will return the
  property you want to compare. Then second argument is the comparator
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
   (operator extractor comparator value)))
