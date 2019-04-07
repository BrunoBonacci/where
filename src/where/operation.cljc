(ns where.operation)


(defn raise-error
  [^String message]
  #?(:clj  (throw (IllegalArgumentException. message))
     :cljs (throw {:type "IllegalArgumentException"
                   :message message})))


(defmulti operation
  (fn [_ op _]
    (if (some-> op name
                ((fn [^String s]
                   (.startsWith (.toLowerCase s) "not-"))))
      :not
      op)))



(defmethod operation :default
  [extractor op value]
  (raise-error (str "Illegal comparator: " op)))



(defmethod operation :not
  [extractor op value]
  (let [op (keyword (.substring (name op) 4))]
    (complement
     (operation extractor op value))))


;; TODO: :matches-date?
;; TODO: :like?

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;              ---==| G E N E R I C   O P E R A T O R S |==----              ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



(defmethod operation :is?
  [extractor _ value]
  (fn [item]
    (= (extractor item) value)))



(defmethod operation :is-not?
  [extractor _ value]
  (fn [item]
    (not= (extractor item) value)))



(defmethod operation :in?
  [extractor _ values]
  (let [vs (set values)]
    (fn [item]
      (vs (extractor item)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;               ---==| S T R I N G   O P E R A T O R S |==----               ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defmethod operation :IS?
  [extractor _ ^String value]
  (let [^String value (when value (.toLowerCase value))]
    (fn [item]
      (let [^String s (extractor item)
            s (when s (.toLowerCase s))]
        (= s value)))))



(defmethod operation :IS-NOT?
  [extractor _ value]
  (complement
   (operation extractor :IS? value)))



(defmethod operation :IN?
  [extractor _ values]
  (let [vs (set
            (filter identity
                    (map (fn [^String s]
                           (when s (.toLowerCase s))) values)))]
    (fn [item]
      (let [^String s (extractor item)]
        (vs (when s (.toLowerCase s)))))))



(defmethod operation :starts-with?
  [extractor _ ^String value]
  (fn [item]
    (let [^String s (extractor item)]
      (when (and s value)
        (.startsWith s value)))))



(defmethod operation :STARTS-WITH?
  [extractor _ ^String value]
  (let [^String value (when value (.toLowerCase value))]
    (fn [item]
      (let [^String s (extractor item)]
        (when (and s value)
          (.startsWith (.toLowerCase s) value))))))



(defmethod operation :ends-with?
  [extractor _ ^String value]
  (fn [item]
    (let [^String s (extractor item)]
      (when (and s value)
        (.endsWith s value)))))



(defmethod operation :ENDS-WITH?
  [extractor _ ^String value]
  (let [^String value (when value (.toLowerCase value))]
    (fn [item]
      (let [^String s (extractor item)]
        (when (and s value)
          (.endsWith (.toLowerCase s) value))))))



(defmethod operation :contains?
  [extractor _ ^String value]
  (fn [item]
    (let [^String s (extractor item)]
      (when (and s value)
        (not= -1 (.indexOf s value))))))



(defmethod operation :CONTAINS?
  [extractor _ ^String value]
  (let [^String value (when value (.toLowerCase value))]
    (fn [item]
      (let [^String s (extractor item)]
        (when (and s value)
          (not= -1 (.indexOf (.toLowerCase s) value)))))))



(defmethod operation :matches?
  [extractor _ value]
  (let [value (when value (re-pattern value))]
    (fn [item]
      (let [^String s (extractor item)]
        (when (and s value)
          (re-find value s))))))



(defn- insensitive-pattern [value]
  #?(:clj
     (when value
       (java.util.regex.Pattern/compile
        (.pattern (re-pattern value))
        java.util.regex.Pattern/CASE_INSENSITIVE))

     :cljs
     (when value
       (re-pattern
        (str "(?i)"
             (some-> (re-find #"^/(.*)/$" (str (re-pattern value))) second))))))



(defmethod operation :MATCHES?
  [extractor _ value]
  (let [value (insensitive-pattern value)]
    (fn [item]
      (let [^String s (extractor item)]
        (when (and s value)
          (re-find value (.toLowerCase s)))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                            ;;
;;            ---==| N U M E R I C A L   O P E R A T O R S |==----            ;;
;;                                                                            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defmethod operation :between?
  [extractor _ [v1 v2]]
  (if (and v1 v2)
    (let [low (min v1 v2)
          high (max v1 v2)]
      (fn [item]
        (when item
          (<= low (extractor item) high))))
    (constantly nil)))



(defmethod operation :strictly-between?
  [extractor _ [v1 v2]]
  (if (and v1 v2)
    (let [low (min v1 v2)
          high (max v1 v2)]
      (fn [item]
        (when item
          (< low (extractor item) high))))
    (constantly nil)))



(defmethod operation :range?
  [extractor _ [v1 v2]]
  (if (and v1 v2)
    (let [low (min v1 v2)
          high (max v1 v2)]
      (fn [item]
        (when item
          (or (= low (extractor item)) (< low (extractor item) high)))))
    (constantly nil)))
