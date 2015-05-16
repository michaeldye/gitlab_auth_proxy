(ns proxy.handler
  (:require [ring.adapter.jetty :as jetty]
            [proxy.wrappers :as wrappers]
            [taoensso.timbre :as timbre])
  (:gen-class))
(timbre/refer-timbre)

(defn init []
  (info "app starting..."))

(defn destroy [] (info "app shutting down..."))

; need to add WWW-Authenticate header
(defn -server-error [request]
  {:status 500
   :body "server error"})

(def app
  (let [auth-url (System/getenv "GITLAB_URL")
        proxy-url (System/getenv "PROXY_URL")]
  (info "starting authentication proxy service with GitLab URL:" auth-url", proxy URL:" proxy-url)
  (-> -server-error
      (wrappers/rev-proxy proxy-url)
      (wrappers/auth auth-url)
      (wrappers/req-log)
      (wrappers/resp-log))))
