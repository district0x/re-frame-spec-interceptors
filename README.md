# re-frame-spec-interceptors

[![Build Status](https://travis-ci.org/district0x/re-frame-spec-interceptors.svg?branch=master)](https://travis-ci.org/district0x/re-frame-spec-interceptors)

[re-frame](https://github.com/Day8/re-frame) event interceptors for validating db and args with spec

## Installation
Add `[district0x/re-frame-spec-interceptors "1.0.0"]` into your project.clj  
Include `[district0x.re-frame.spec-interceptors]` in your CLJS file

## Usage
This library contains 4 interceptors:
* `validate-db` - Validates re-frame db with spec after an event
* `validate-args` - Validates args passed into an event as a collection
* `validate-first-arg` - Validates first arg passed into an event
* `conform-args` - Conforms collection of args passed to an event

```clojure
(ns my.project
  (:require
    [cljs.spec.alpha :as s]
    [district0x.re-frame.spec-interceptors :refer [validate-db validate-args validate-first-arg conform-args]]
    [re-frame.core :refer [reg-event-fx dispatch trim-v]]))
    
 (s/def ::a int?)
 (s/def ::db (s/keys :req-un [::a]))
 
 ;; validate-db
 (reg-event-fx
   ::validate-db
   [(validate-db ::db)]
   (fn []
     {:db {:a "wrong"}}))
     
 (dispatch[::invalid-db])
 ;; Throws error with spec explanation
 
 ;; validate-args
(reg-event-fx
  ::validate-args
  [trim-v (validate-args (s/cat :some-numbers int?))]
  (fn []))
  
(dispatch [::validate-args "wrong"])
;; Throws error with spec explanation


;; validate-first-arg
(reg-event-fx
  ::validate-first-arg
  [trim-v (validate-first-arg ::a)]
  (fn []))

(dispatch[::validate-first-arg "wrong"])
;; Throws error with spec explanation

;; 
(reg-event-fx
  ::conform-args
  [trim-v (conform-args (s/cat :a int? :b string?))]
  (fn [_ [{:keys [:a :b]}]]
    (println "a:" a "b:" b)))
    
(dispatch [::conform-args 1 "good"])
;; prints "a: 1 :b \"good\""

```

## Development
```bash
lein deps

# To run tests and rerun on changes
lein doo chrome tests
```