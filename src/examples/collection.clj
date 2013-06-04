(ns examples.collection
  (:require [liberator.core :refer (defresource by-method)]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [excel-exporter.kiez2013 :as kiez2013]
            [liberator.dev :refer (wrap-trace)])
  (:import java.net.URL))

(defn build-entry-url [request id]
  (println "R" request)
  (URL. (format "%s://%s:%s%s/%s"
          (name (:scheme request))
          (:server-name request)
          (:server-port request)
          (:uri request)
          (str id))))

(defn body-as-string [ctx]
  (if-let [body (get-in ctx [:request :body])]
    (condp instance? body
      java.lang.String body
      (slurp (io/reader body)))))

(defn parse-json [context key]
  (when (#{:put :post} (get-in context [:request :request-method]))
    (try
      (if-let [body (body-as-string context)]
        (let [data (json/read-str body)]
          [false {key data}])
        {:message "No body"})
      (catch Exception e
        (.printStackTrace e)
        {:message (format "IOException: " (.getMessage e))}))))

(defn check-content-type [ctx content-types]
  (if (#{:put :post} (get-in ctx [:request :request-method]))
    (or
      (some #{(get-in ctx [:request :headers "content-type"])}
        content-types)
      [false {:message "Unsupported Content-Type"}])
    true))

(defresource list-resource
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :known-content-type? #(check-content-type % ["application/json"])
  :malformed? #(parse-json % ::data)
  :handle-ok (kiez2013/vereine-list))

(defresource entry-resource [id]
  :allowed-methods [:get]
  :known-content-type? #(check-content-type % ["application/json"])
  :exists? (fn [_]
             (prn "ID" id)
             (let [e (kiez2013/verein-by-startnummer id)]
               (if-not (nil? e)
                 {::entry (json/write-str e ) })))
  :existed? (fn [_] (nil? (kiez2013/verein-by-startnummer id)))
  :available-media-types ["application/json"]
  :handle-ok ::entry
  :malformed? #(parse-json % ::data)
  )