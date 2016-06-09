# where

Human readable conditions and `filter` best companion.
A Clojure and ClojureScript library to write expressive
predicate functions.

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

`where` allows to build very expressive predicate functions.

## Usage

To use this library add the following dependency in your `project.clj`

    [com.brunobonacci/where "0.2.0"]

Latest version: [![where](https://img.shields.io/clojars/v/com.brunobonacci/where.svg)](https://clojars.org/com.brunobonacci/where)

then require the library

```Clojure
(ns your-ns
  (:require [where.core :refer [where]])
```

Now assuming that you have a collection maps, called `users` which look like this:

```
{:name "....",
 :user "....",
 :country "France",
 :age 33,
 :active true,
 :scores {:high 6671, :last 4344, :min 2475}}
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


Now If you want to filter out all the users who are older the 50
you would write something like this:

```Clojure
;; using plain Clojure
(filter #(< (:age %) 50) users)

;; using where
(filter (where :age < 50) users)
```

Now to better show how you write nice and clean predicates
I've added some randomly generated test data into the project.

In order to show the following examples I suggest you clone this
git repo and fire up a REPL.

```
git clone https://github.com/BrunoBonacci/where.git
cd where
lein repl

;; importing the library
user> (use 'where.core)
;; nil

;; importing the test data
user> (use 'where.test-util)
;; nil

user> (bootstrap)
;; Loading test data...
;; #'where.test-util/users

user> (first users)
;; {:name "Kiayada Wyatt",
;;  :user "kiayada33",
;;  :country "France",
;;  :age 33,
;;  :active true,
;;  :scores {:high 6671, :last 4344, :min 2475}}

user> (count users)
;; 500
```

**PLEASE NOTE THAT THE DATA IS TOTALLY RANDOM**

The namespace `where.test-util` beside the function `bootstrap` which
load the test data, it defines a utility function called `ptable` which
prints the users list as a table in the `stdout`.

```Clojure

(ptable (take 5 users))

;; |            :name |      :user | :age |       :country | :active |                             :scores |
;; |------------------+------------+------+----------------+---------+-------------------------------------|
;; |    Kiayada Wyatt |  kiayada33 |   33 |         France |    true | {:high 6671, :last 4344, :min 2475} |
;; |    Dominic Ochoa |  dominic43 |   72 | United Kingdom |   false |  {:high 7394, :last 4907, :min 599} |
;; | Cherokee Hammond | cherokee10 |   22 |         Russia |   false | {:high 4896, :last 4247, :min 2803} |
;; |      Gemma Foley |    gemma36 |   28 |          Italy |    true | {:high 6157, :last 2294, :min 1003} |
;; |    Ginger Garcia |   ginger55 |   28 |          India |   false | {:high 3729, :last 3568, :min 1254} |
;; => nil
```

### Simple map filtering

Now let see how we can make use of the `where` function and how much
more readable the predicates are compared to custom Clojure functions.
Here results of `ptable` will be shortened for brevity the ellipsis
(`...`) will indicate the point in which it has been truncated.

```Clojure
;; plain Clojure
;; #(= (:country %) "Italy")

;; with where
;; (where :country = "Italy")

(ptable (filter (where :country = "Italy") users))

;; |              :name |        :user | :age | :country | :active |                             :scores |
;; |--------------------+--------------+------+----------+---------+-------------------------------------|
;; |        Gemma Foley |      gemma36 |   28 |    Italy |    true | {:high 6157, :last 2294, :min 1003} |
;; |      Sierra Bishop |     sierra15 |   38 |    Italy |   false | {:high 9184, :last 7354, :min 5344} |
;; |  Catherine Wallace |  catherine16 |   40 |    Italy |    true |  {:high 9053, :last 1527, :min 988} |
;; |     Odette Goodman |     odette51 |   85 |    Italy |    true |   {:high 5434, :last 4295, :min 11} |
;; ...
```


```Clojure
;; plain Clojure
;; #(>= (:age %) 18)

;; with where
;; (where :age >= 18)

(ptable (filter (where :age >= 18) users))

;; |            :name |      :user | :age |       :country | :active |                             :scores |
;; |------------------+------------+------+----------------+---------+-------------------------------------|
;; |    Kiayada Wyatt |  kiayada33 |   33 |         France |    true | {:high 6671, :last 4344, :min 2475} |
;; |    Dominic Ochoa |  dominic43 |   72 | United Kingdom |   false |  {:high 7394, :last 4907, :min 599} |
;; | Cherokee Hammond | cherokee10 |   22 |         Russia |   false | {:high 4896, :last 4247, :min 2803} |
;; |      Gemma Foley |    gemma36 |   28 |          Italy |    true | {:high 6157, :last 2294, :min 1003} |
;; |    Ginger Garcia |   ginger55 |   28 |          India |   false | {:high 3729, :last 3568, :min 1254} |
;; ...
```

### Logical operators

It is possible to combine predicate function with logical operators such
as `AND` and `OR` to create more sophisticate filters.

```Clojure
;; now plain Clojure starts to be very hard to read
;; #(and (= (:country %) "Italy") (>= (:age %) 18))

;; while with where is it still pretty intuitive
;; (where [:and [:country = "Italy"] [:age >= 18]])

(ptable (filter (where [:and [:country = "Italy"] [:age >= 18]]) users))

;; |             :name |       :user | :age | :country | :active |                             :scores |
;; |-------------------+-------------+------+----------+---------+-------------------------------------|
;; |       Gemma Foley |     gemma36 |   28 |    Italy |    true | {:high 6157, :last 2294, :min 1003} |
;; |     Sierra Bishop |    sierra15 |   38 |    Italy |   false | {:high 9184, :last 7354, :min 5344} |
;; | Catherine Wallace | catherine16 |   40 |    Italy |    true |  {:high 9053, :last 1527, :min 988} |
;; |    Odette Goodman |    odette51 |   85 |    Italy |    true |   {:high 5434, :last 4295, :min 11} |
;; |    Danielle Tyler |  danielle44 |   61 |    Italy |    true | {:high 9648, :last 9002, :min 3567} |
;; ...
```

Let's now get all active users from Italy or USA which are of age between 18 and 65.

```Clojure
;; Clojure now becomes impossible to read
;; #(and (or (= (:country %) "Italy") (= (:country %) "USA"))
;;       (>= (:age %) 18) (<= (:age %) 65)
;;       (:active %))

;; let's see how `where` simplifies this.
;; (where [:and [:or [:country = "Italy"] [:country = "USA"]]
;;              [:age >= 18] [:age <= 65] [:active = true]])

(ptable (filter (where [:and [:or [:country = "Italy"] [:country = "USA"]]
                             [:age >= 18] [:age <= 65] [:active = true]]) users))

;; |             :name |       :user | :age | :country | :active |                             :scores |
;; |-------------------+-------------+------+----------+---------+-------------------------------------|
;; |       Gemma Foley |     gemma36 |   28 |    Italy |    true | {:high 6157, :last 2294, :min 1003} |
;; |     Walter Hodges |    walter34 |   63 |      USA |    true | {:high 3060, :last 2625, :min 2053} |
;; |       Jonah Duran |     jonah45 |   47 |      USA |    true | {:high 8473, :last 6827, :min 1214} |
;; | Catherine Wallace | catherine16 |   40 |    Italy |    true |  {:high 9053, :last 1527, :min 988} |
;; |    Danielle Tyler |  danielle44 |   61 |    Italy |    true | {:high 9648, :last 9002, :min 3567} |
;; ...
```

### Nested Maps

Because the `extractor`, the first argument of `where`, it's just a function which is applied to the
item/map you can compose your key functions to reach nested maps.

```Clojure
;; nested maps makes predicates even more unreadable
;; #(> (:high (:scores %)) 9000)

;; with `where` you can use the clojure `comp`
;; (where (comp :high :scores) > 9000)

(ptable (filter (where (comp :high :scores) > 9000) users))

;; |           :name |     :user | :age |       :country | :active |                             :scores |
;; |-----------------+-----------+------+----------------+---------+-------------------------------------|
;; |     Hoyt Dennis |    hoyt50 |   84 |        Nigeria |    true | {:high 9982, :last 5168, :min 3766} |
;; |    Haviva Allen |  haviva49 |   89 |         France |   false | {:high 9665, :last 8768, :min 5960} |
;; |     Mona Massey |    mona46 |   66 | United Kingdom |    true | {:high 9935, :last 7238, :min 2986} |
;; |   Nicole Carter |  nicole35 |   32 |         Russia |    true | {:high 9990, :last 2218, :min 1854} |
;; | Deirdre Pittman | deirdre88 |   41 |        Nigeria |   false | {:high 9320, :last 9213, :min 5795} |
;; ...
```

### More on `extractor`

Another interesting property of having the `extractor` as a function is that it can be replaced
with anything you like which given a value return another value.

For example if we want to find out which users have a `:name` which is
more than 15 characters we can write:

```Clojure
;; (where (comp count :name) > 15)

(ptable (filter (where (comp count :name) > 15) users))

;; |              :name |       :user | :age | :country | :active |                             :scores |
;; |--------------------+-------------+------+----------+---------+-------------------------------------|
;; |   Cherokee Hammond |  cherokee10 |   22 |   Russia |   false | {:high 4896, :last 4247, :min 2803} |
;; |   Josephine Castro | josephine87 |   58 |   Canada |   false | {:high 6815, :last 3574, :min 3053} |
;; |  Catherine Wallace | catherine16 |   40 |    Italy |    true |  {:high 9053, :last 1527, :min 988} |
;; | Priscilla Mcfadden | priscilla14 |   19 |   Russia |    true | {:high 9612, :last 7930, :min 4343} |
;; | Anastasia Whitaker | anastasia23 |   76 |   Russia |   false | {:high 9583, :last 4547, :min 1186} |
;; ...
```

### More on `comparator`

The `comparator` function is a function which takes two values and
return a truthy or a falsey value.  So you can easily write your own
comparators with anything you need.


```Clojure
(defn ends-with [s end] (.endsWith s end))
;; => #'user/ends-with

;; find all users which username ends with a `6`
;; (where :user ends-with "6")

(ptable (filter (where :user ends-with "6") users))

;; |             :name |       :user | :age |       :country | :active |                             :scores |
;; |-------------------+-------------+------+----------------+---------+-------------------------------------|
;; |       Gemma Foley |     gemma36 |   28 |          Italy |    true | {:high 6157, :last 2294, :min 1003} |
;; |       Mona Massey |      mona46 |   66 | United Kingdom |    true | {:high 9935, :last 7238, :min 2986} |
;; |    Dahlia Whitney |    dahlia86 |   39 |         France |   false | {:high 7701, :last 5101, :min 4793} |
;; | Catherine Wallace | catherine16 |   40 |          Italy |    true |  {:high 9053, :last 1527, :min 988} |
;; |    Xerxes Holland |    xerxes26 |   51 |         Russia |    true | {:high 9437, :last 6868, :min 5280} |
;; ...
```

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

## Development

CI status: [![CircleCI](https://circleci.com/gh/BrunoBonacci/where.svg?style=svg)](https://circleci.com/gh/BrunoBonacci/where)

## TODOs

Here are things I'm considering to add.

  * more logical operators such as `NOT` and `XOR`
  * support for composition with custom predicates `(where [:and f1 f2 f3])`
  * a set of useful/common extractors and comparators.
  * support for unary predicates

## License

Copyright © 2015 Bruno Bonacci

Distributed under the Apache 2 License.
