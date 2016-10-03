# `where` cheatsheet

## Syntax

``` clojure
(where extractor comparator target)
;;=> predicate function
```

## With maps

- `(where :field = "value")` Use the keyword as a function to extract the value
- `(where :age > 18)` Comparator can be any clojure function which take two arities.
- `(where (comp :zip :address) not= "ABC123")` Nested maps can be reached by composing functions.
- `(where [:age <= 24])` The condition can be expressed as a vector of 3 elements for better composition.

## Logical operators

- `(where [:and [:age <= 24] [:country = "USA"]])` `:and` can be used to connect predicates logically for which all the conditions must be truthy.
- `(where [:or [:country = "USA"] [:country = "Italy]])` `:or` can be used to connect predicates logically for which one the conditions must be truthy.
- `(where [:not [:country = "USA"]])` `:not` can be used to negate the logical value o a single predicate.


## Built-in Generic comparators

- `(where :country :is? "Italy")`
- `(where :country :is-not? "Italy")`
- `(where :country :in? ["Italy" "France" "USA"])`
- `(where :country :not-in? ["Italy" "France" "USA"])`

## Built-in String comparators

- `(where :country :startsWith? "Ita")`
- `(where :country :endsWith? "ly")`
- `(where :country :startsWith? "Ita")`
- `(where :country :contains? "tal")`
- `(where :country :matches? #"United.*")`

## Built-in Case-insensitive String comparators

- `(where :country :IS? "italy")`
- `(where :country :STARTSWITH? "ITA")`
- `(where :country :ENDSWITH? "ly")`
- `(where :country :STARTSWITH? "ita")`
- `(where :country :CONTAINS? "tal")`
- `(where :country :IN? ["ITALY" "france"])`
- `(where :country :MATCHES? #"united.*")`

## Built-in Negation of String comparators

- `(where :country :not-startsWith? "Ita")`
- `(where :country :not-endsWith? "ly")`
- `(where :country :not-startsWith? "Ita")`
- `(where :country :not-contains? "tal")`
- `(where :country :not-matches? #"United.*")`
- `(where :country :IS-NOT? "italy")`
- `(where :country :NOT-STARTSWITH? "ITA")`
- `(where :country :NOT-ENDSWITH? "ly")`
- `(where :country :NOT-STARTSWITH? "ita")`
- `(where :country :NOT-CONTAINS? "tal")`
- `(where :country :NOT-IN? ["ITALY" "france"])`
- `(where :country :NOT-MATCHES? #"united.*")`

## Built-in numerical comparators

- `(where :age :between? [18 34])` - true for all number between 18 and 34 (included)
- `(where :age :strictly-between? [18 34])` - true for all number between 18 and 34 (excluded)
- `(where :age :range? [18 34])` - true for all number between 18 (inlcuded) and 34 (excluded)
- `(where :age :in? [18 22 34 16])` - true if the :age is in the given list of values
- `(where :age :not-between? [18 34])` - false for all number between 18 and 34 (included)
- `(where :age :not-strictly-between? [18 34])` - false for all number between 18 and 34 (excluded)
- `(where :age :not-range? [18 34])` - false for all number between 18 (inlcuded) and 34 (excluded)
- `(where :age :not-in? [18 22 34 16])` - false if the :age is in the given list of values
