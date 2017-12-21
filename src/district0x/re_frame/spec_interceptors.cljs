(ns district0x.re-frame.spec-interceptors
  (:require
    [cljs.spec.alpha :as s]
    [re-frame.core :as re-frame :refer [console]]))

(defn throw-event-error [event untrimmed-event spec]
  (throw (js/Error. (str "Invalid input into event" (or untrimmed-event event) "\n" (s/explain-str spec event)))))


(defn validate-db [spec]
  (re-frame/->interceptor
    :id :validate-db
    :after (fn [{{:keys [:event :re-frame.std-interceptors/untrimmed-event]} :coeffects
                 {:keys [:db]} :effects :as context}]
             (when (and (s/check-asserts?) db (not (s/valid? spec db)))
               (console :log db)
               (throw (js/Error. (str "DB is invalid after event"
                                      (or untrimmed-event event) "\n"
                                      (subs (s/explain-str spec db) 0 1000)))))
             context)))



(defn validate-args [spec]
  (re-frame/->interceptor
    :id :validate-args
    :before (fn [{{:keys [:event :re-frame.std-interceptors/untrimmed-event]} :coeffects :as context}]
              (if (and (s/check-asserts?) (not (s/valid? spec event)))
                (throw-event-error event untrimmed-event spec)
                context))))


(defn validate-first-arg [spec]
  (re-frame/->interceptor
    :id :validate-args
    :before (fn [{{:keys [:event :re-frame.std-interceptors/untrimmed-event]} :coeffects :as context}]
              (if (and (s/check-asserts?) (not (s/valid? spec (first event))))
                (throw-event-error (first event) untrimmed-event spec)
                context))))


(defn conform-args [spec]
  (re-frame/->interceptor
    :id :conform-args
    :before (fn [{{:keys [:event :re-frame.std-interceptors/untrimmed-event]} :coeffects :as context}]
              (let [conformed (s/conform spec event)]
                (if (= conformed :cljs.spec.alpha/invalid)
                  (throw-event-error event untrimmed-event spec)
                  (update context :coeffects merge (merge {:event [conformed]
                                                           ::unconformed-event event})))))))

