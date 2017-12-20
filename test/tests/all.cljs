(ns tests.all
  (:require
    [cljs.spec.alpha :as s]
    [cljs.test :refer [deftest is testing run-tests use-fixtures]]
    [district0x.re-frame.spec-interceptors :refer [validate-db validate-args validate-first-arg conform-args]]
    [re-frame.core :refer [reg-event-fx dispatch-sync trim-v]]))

(s/def ::a int?)
(s/def ::db (s/keys :req-un [::a]))

(reg-event-fx
  ::invalid-db
  [(validate-db ::db)]
  (fn []
    {:db {:a "wrong"}}))

(reg-event-fx
  ::valid-db
  [(validate-db ::db)]
  (fn []
    {:db {:a 1}}))

(reg-event-fx
  ::validate-args
  [trim-v (validate-args (s/cat :a int?))]
  (fn []))

(reg-event-fx
  ::validate-first-arg
  [trim-v (validate-first-arg ::a)]
  (fn []))

(reg-event-fx
  ::conform-args
  [trim-v (conform-args (s/cat :a int? :b string?))]
  (fn [_ [{:keys [:a :b]}]]
    {:db {:conformed {:a a :b b}}}))

(deftest tests
  (is (thrown? :default (dispatch-sync [::invalid-db])))

  (dispatch-sync [::valid-db])

  (is (thrown? :default (dispatch-sync [::validate-args "wrong"])))

  (dispatch-sync [::validate-args 1])

  (is (thrown? :default (dispatch-sync [::validate-first-arg "wrong"])))

  (dispatch-sync [::validate-first-arg 1])

  (is (thrown? :default (dispatch-sync [::conform-args "wrong"])))

  (dispatch-sync [::conform-args 1 "good"])

  (is (= (:conformed @re-frame.db/app-db)
         {:a 1 :b "good"})))
