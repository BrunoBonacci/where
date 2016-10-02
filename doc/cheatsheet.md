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
