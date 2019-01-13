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

The generic comparator accept any Clojure value.

- `(where :country :is? "Italy")` - like `=`
- `(where :country :is-not? "Italy")` - like `not=`
- `(where :country :in? ["Italy" "France" "USA"])` - truthy if it matches any of the values listed
- `(where :country :not-in? ["Italy" "France" "USA"])` - falsey if it matches any of the values listed

## Built-in String comparators

All String comparators they expect a String and are `nil` safe (don't
throw _NullPointerException_ like they String.class counterparts),
`:matches` require a valid Pattern.

- `(where :country :starts-with? "Ita")` - like `String/startsWith`
- `(where :country :ends-with? "ly")` - like `String/endsWith`
- `(where :country :contains? "tal")` - like `String/indexOf != -1`
- `(where :country :matches? #"United.*")` - like `re-find`

## Built-in Case-insensitive String comparators

All String comparators they expect a String and are `nil` safe (don't
throw _NullPointerException_ like they String.class counterparts),
`:MATCHES` require a valid Pattern.

- `(where :country :IS? "italy")` - like `String/.equalsIgnoreCase`
- `(where :country :STARTS-WITH? "ITA")` - like `String/startsWith`, but case insensitive
- `(where :country :ENDS-WITH? "ly")` - like `String/endsWith`, but case insensitive
- `(where :country :CONTAINS? "tal")` - like `String/indexOf != -1`, but case insensitive
- `(where :country :IN? ["ITALY" "france"])` - truthy if it matches any of the values listed, but case insensitive
- `(where :country :MATCHES? #"united.*")` - like `re-find`, but case insensitive

## Built-in Negation of String comparators

All String comparators they expect a String and are `nil` safe (don't
throw _NullPointerException_ like they String.class counterparts),
`:not-matches` and `:NOT-MATCHES` require a valid Pattern.

- `(where :country :not-starts-with? "Ita")`     - same as `(complement (where :country :starts-with? "Ita"))`
- `(where :country :not-ends-with? "ly")`        - same as `(complement (where :country :ends-with? "ly"))`
- `(where :country :not-contains? "tal")`       - same as `(complement (where :country :contains? "tal"))`
- `(where :country :not-matches? #"United.*")`  - same as `(complement (where :country :matches? #"United.*"))`
- `(where :country :IS-NOT? "italy")`           - same as `(complement (where :country :IS? "italy"))`
- `(where :country :NOT-STARTS-WITH? "ITA")`     - same as `(complement (where :country :STARTS-WITH? "ITA"))`
- `(where :country :NOT-ENDS-WITH? "ly")`        - same as `(complement (where :country :ENDS-WITH? "ly"))`
- `(where :country :NOT-CONTAINS? "tal")`       - same as `(complement (where :country :CONTAINS? "tal"))`
- `(where :country :NOT-IN? ["ITALY" "france"])`- same as `(complement (where :country :IN? ["ITALY" "france"]))`
- `(where :country :NOT-MATCHES? #"united.*")`  - same as `(complement (where :country :MATCHES? #"united.*"))`

## Built-in numerical comparators

All numerical comparators are `nil` safe (don't throw
_NullPointerException_) when one of the argument is nil.

- `(where :age :between? [18 34])` - truthy for all number between 18 and 34 (included)
- `(where :age :strictly-between? [18 34])` - truthy for all number between 18 and 34 (excluded)
- `(where :age :range? [18 34])` - truthy for all number between 18 (inlcuded) and 34 (excluded)
- `(where :age :in? [18 22 34 16])` - truthy if the :age is in the given list of values
- `(where :age :not-between? [18 34])` - falsey for all number between 18 and 34 (included)
- `(where :age :not-strictly-between? [18 34])` - falsey for all number between 18 and 34 (excluded)
- `(where :age :not-range? [18 34])` - falsey for all number between 18 (inlcuded) and 34 (excluded)
- `(where :age :not-in? [18 22 34 16])` - falsey if the :age is in the given list of values
