(ns where.core)

(defn- f-and [fs]
  (fn [x]
    (loop [[f1 & fr] fs]
      (if-not f1
        true ;; reached the end
        (if (f1 x) (recur fr) false)))))


(defn- f-or [fs]
  (fn [x]
    (loop [[f1 & fr] fs]
      (if-not f1
        false ;; reached the end
        (if (f1 x) true (recur fr))))))


(defn where
  ([[op & rules :as cond-spec]]
   (if (#{:or :and} op)
     (let [cnds (map where rules)]
       (case op
         :or (f-or cnds)
         :and (f-and cnds)))
     (apply where cond-spec)))
  ([comparator value]
   (where identity comp value))
  ([extractor comparator value]
   (fn [item]
     (comparator (extractor item) value))))

(defn load-data []
  (map (partial zipmap [:name :user :age :country :active])
   (read-string (slurp "/workspace/oss/cascalog-examples/data/users.edn"))))


#_(def users (load-data))


(clojure.pprint/print-table
 [:name :user :age :country :active]
 (filter (where  [:and
                  [:country = "Russia"]
                  [:age     > 35]
                  [:age     < 60]]) users))



(clojure.pprint/print-table
 [:name :user :age :country :active]
 (filter (where  [:or
                  [:country = "Russia"]
                  [:country = "USA"]]) users))



(clojure.pprint/print-table
 [:name :user :age :country :active]
 (filter (where  [:and
                  [:or
                   [:country = "Russia"]
                   [:country = "USA"]]
                  [:age     > 35]
                  [:age     < 60]]) users))



(def f1 (where :country = "Russia"))
(def f2 (where :country = "USA"))
(def f12 (where [:or f1 f2]))
(def f12 (f-or [f1 f2]))

(def f3 (where :age     > 35))
(def f4 (where :age     < 60))

(def f1234 (where [:and f12 f3 f4]))
(def f1234 (f-and [f12 f3 f4]))

(filter f1234 users)

(where f1)


(def fx (where  [:and
                  [:or
                   [:country = "Russia"]
                   [:country = "USA"]]
                  [:age     > 35]
                  [:age     < 60]]))

(filter fx users)


(def ff (fand [f1 f2]))

(def r (filter (where  :country = "Russia") users))

(ff (second (next r)))


((fand [f1]) (first users))
