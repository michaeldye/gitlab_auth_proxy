(ns proxy.wrappers
  (:import java.net.URI)
  (:require [clj-http.client :as http]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            [clojure.string :as string]))
(timbre/refer-timbre)

(defn req-log [handler]
  (fn [req]
    (debug "HTTP Request:\n" (with-out-str (pprint req)))
      (handler req)))

(defn resp-log [handler]
  (fn [req]
    (let [resp (handler req)]
      (debug "HTTP Response:\n" (with-out-str (pprint resp)))
      resp)))

(def default-conf {:throw-exceptions false :insecure? true})
(defn -conf
  ([] default-conf)
  ([conf] (merge default-conf conf)))

(defn -basic-to-gitlab-auth [auth-string]
  (zipmap [:login :password]
    (if (nil? auth-string)
      []
      (string/split
        (->> (last (string/split auth-string #"\s"))
             (.decode (java.util.Base64/getDecoder))
             (new String))
        #":"))))

(defn auth [handler auth-url]
  (fn [req]
    (let [conf (-conf {:form-params (-basic-to-gitlab-auth (get (:headers req) "authorization"))})]
      (debug "auth request to" auth-url "with conf" conf)
      (let [{:keys [status headers] :as resp} (http/post auth-url conf)]
        (debug "++++++++proxy status:" status", headers:" headers)
        (if (not (= (quot status 100) 2))
          resp
          (handler (update-in req [:headers] dissoc "authorization")))))))

(defn rev-proxy [handler proxy-url]
  (fn [req]
    (debug "proxy request to" proxy-url)
    (let [url (str proxy-url (clojure.string/replace-first (:uri req) proxy-url ""))]
      (let [{:keys [status headers body] :as resp} (http/post url (-conf))]
        (debug "status:" status", headers:" headers", resp:" resp)
        resp))))
