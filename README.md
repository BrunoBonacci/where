# where
[![Clojars Project](https://img.shields.io/clojars/v/com.brunobonacci/where.svg)](https://clojars.org/com.brunobonacci/where) ![CircleCi](https://img.shields.io/circleci/project/BrunoBonacci/where.svg) ![last-commit](https://img.shields.io/github/last-commit/BrunoBonacci/where.svg) [![Dependencies Status](https://jarkeeper.com/BrunoBonacci/where/status.svg)](https://jarkeeper.com/BrunoBonacci/where)

Human readable conditions and `filter` best companion.
A Clojure and ClojureScript library to write expressive
predicate functions.

Advantages:
  * Predicate functions are very expressive and easy to read
  * All *built-in comparators* are `nil` safe.
  * All *built-in comparators* which operate on strings have a *case insensitive version*
  * Very fast execution (same as hand crafted version)
  * Very easy to embed in your DSL
  * Support for glob pattern matching (like: `*.txt`)

## Motivations

Maps are everywhere is Clojure. And when dealing with loads of maps it
is important to work comfortably and make your code as readable as
possible.  However when it comes to filter maps building a clean
predicate function becomes increasingly harder. Even harder with nested
maps or when it required to combine predicate function with logical
operators.

The purpose of this library is to simplify the construction of predicate
functions.  A predicate function is a function which takes a value and
return a truthy or a falsey value.  `f(x) -> truthy | falsey`.  These
are often used together with function like `filter` to retain only items
which match a specific condition.

`where` allows to build very expressive predicate functions and `nil`-safe.


## Usage

To use this library add the following dependency in your `project.clj`

    [com.brunobonacci/where "0.5.5"]

Latest version: [![where](https://img.shields.io/clojars/v/com.brunobonacci/where.svg)](https://clojars.org/com.brunobonacci/where)

then require the library

```Clojure
(ns your-ns
  (:require [where.core :refer [where]])
```

The signature of the function is:

```Clojure
(where extractor comparator value)
```

  * `extractor` is a function which is applied to every item passed
     and it has to extract the value from the map which ultimately
     needs to be compared.
  * `comparator` is any binary comparator such as: `=`, `not=`, `>`, `<`, etc
     It is just a function with takes two value and return true or false
  * `value` is the value to compare against.


## `where` cheatsheet

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
- `(where [:not [:country = "USA"]])` `:not` can be used to negate the logical value of a single predicate.


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
- `(where :country :matches-exactly? #"United.*")` - like `re-matches`
- `(where :filename :glob-matches? "*.txt")` - like glob pattern or wildcard matching `?` for any character, `*` any number of any characters

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
- `(where :country :MATCHES-EXACTLY? #"united.*")` - like `re-matches`, but case insensitive
- `(where :filename :GLOB-MATCHES? "*.txt")` - like `:glob-matches?`, but case insensitive

## Built-in Negation of String comparators

All String comparators they expect a String and are `nil` safe (don't
throw _NullPointerException_ like they String.class counterparts),
`:not-matches` and `:NOT-MATCHES` require a valid Pattern.

- `(where :country :not-starts-with? "Ita")`     - same as `(complement (where :country :starts-with? "Ita"))`
- `(where :country :not-ends-with? "ly")`        - same as `(complement (where :country :ends-with? "ly"))`
- `(where :country :not-contains? "tal")`       - same as `(complement (where :country :contains? "tal"))`
- `(where :country :not-matches? #"United.*")`  - same as `(complement (where :country :matches? #"United.*"))`
- `(where :country :not-matches-exactly? #"United.*")` - same as `(complement (where :country :matches-exactly? #"United.*"))`
- `(where :filename :not-glob-matches? "*.txt")` - same as `(complement (where :filename :not-glob-matches? "*.txt"))`
- `(where :country :IS-NOT? "italy")`           - same as `(complement (where :country :IS? "italy"))`
- `(where :country :NOT-STARTS-WITH? "ITA")`     - same as `(complement (where :country :STARTS-WITH? "ITA"))`
- `(where :country :NOT-ENDS-WITH? "ly")`        - same as `(complement (where :country :ENDS-WITH? "ly"))`
- `(where :country :NOT-CONTAINS? "tal")`       - same as `(complement (where :country :CONTAINS? "tal"))`
- `(where :country :NOT-IN? ["ITALY" "france"])`- same as `(complement (where :country :IN? ["ITALY" "france"]))`
- `(where :country :NOT-MATCHES? #"united.*")`  - same as `(complement (where :country :MATCHES? #"united.*"))`
- `(where :country :NOT-MATCHES-EXACTLY? #"united.*")`  - same as `(complement (where :country :MATCHES-EXACTLY? #"united.*"))`
- `(where :filename :NOT-GLOB-MATCHES? "*.txt")` - same as `(complement (where :filename :NOT-GLOB-MATCHES? "*.txt"))`

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


### Built-in comparators.

There are a number of common compartors which are provided as built-in functions.
These comparators allow for much simpler and expressive code than their respective
Clojure's counterparts. Additionally all built-in comparators have the following
properties:

  - **All built-in comparator are `nil` safe**
  - **All built-in comparator have a complement operator (which starts with `not`)**
  - **All built-in string comparator have a __case insensitive__ version (uppercase)**

#### Comparators to work with Strings

| Comparator        | Complement (not)      | Case-insensitive  | Insensitive Complement |
| -------------     | -----------------     | ----------------  | ---------------------- |
| :is?              | :is-not?              | :IS?              | :IS-NOT?               |
| :starts-with?     | :not-starts-with?     | :STARTS-WITH?     | :NOT-STARTS-WITH?      |
| :ends-with?       | :not-ends-with?       | :ENDS-WITH?       | :NOT-ENDS-WITH?        |
| :contains?        | :not-contains?        | :CONTAINS?        | :NOT-CONTAINS?         |
| :in?              | :not-in?              | :IN?              | :NOT-IN?               |
| :matches?         | :not-matches?         | :MATCHES?         | :NOT-MATCHES?          |
| :matches-exactly? | :not-matches-exactly? | :MATCHES-EXACTLY? | :NOT-MATCHES-EXACTLY?  |
| :glob-matches?    | :not-glob-matches?    | :GLOB-MATCHES?    | :NOT-GLOB-MATCHES?     |
|                   |                       |                   |                        |


| Comparator    | Example                                          |
|-------------- | ------------------------------------------------ |
| :is?          | `(where :country :is? "USA")`                    |
| :starts-with? | `(where :country :starts-with? "US")`            |
| :ends-with?   | `(where :country :ends-with? "SA")`              |
| :contains?    | `(where :country :contains? "SA")`               |
| :in?          | `(where :country :in? ["USA" "Italy" "France"])` |
| :matches?     | `(where :country :matches? #"United.*")`         |


#### Comparators to work with Numbers

| Comparator         | Complement (not)       |
|------------------- | ---------------------- |
| :between?          | :not-between?          |
| :strictly-between? | :not-strictly-between? |
| :range?            | :not-range?            |
| :in?               | :not-in?               |

| Comparator         | Example                                   | True for       |
|------------------- | ----------------------------------------- | -------------- |
| :between?          | `(where :age :between? [18 21])`          | 18, 19, 20, 21 |
| :strictly-between? | `(where :age :strictly-between? [18 21])` | 19, 20         |
| :range?            | `(where :age :range? [18 21])`            | 18, 19, 20     |
| :in?               | `(where :age :in? [18 20 22 24])`         | 18, 20, 22, 24 |


### Using `where` outside of maps.

`where` can be used also with numbers and strings. When the extractor doesn't apply
then simply use the two-arity version of the function.

For example:

```Clojure
(filter (where > 5) (range 10))
;; => (6 7 8 9)

(filter (where ends-with "er") ["warrior" "singer" "player"])
;; => ("singer" "player")
```

See more [examples](/doc/examples.md).

## Development

CI status: [![CircleCI](https://circleci.com/gh/BrunoBonacci/where.svg?style=svg)](https://circleci.com/gh/BrunoBonacci/where)

### How to build.

Build profiles are by Clojure version:

``` shell
lein build-all
```

## TODOs

Here are things I'm considering to add.

  * more logical operators such as `XOR`
  * support for composition with custom predicates `(where [:and f1 f2 f3])`
  * a set of useful/common extractors and comparators.
  * support for unary predicates

## License

Copyright © 2015 - 2018 Bruno Bonacci - Distributed under the [Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0)
