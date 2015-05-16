(defproject gitlab_auth_proxy "0.1.0-SNAPSHOT"
  :description "Authenticating proxy server for Gitlab"
  :url "https://github.com/michaeldye/gitlab_auth_proxy"
  :license {:name "GPL v3"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.4.0-beta2"]
                 [ring/ring-servlet "1.4.0-beta2"]
                 [ring/ring-codec "1.0.0"]
                 [com.taoensso/timbre "3.4.0"]
                 [clj-http "1.1.2"]]
  :plugins [[lein-deps-tree "0.1.2"]
            [lein-ring "0.9.3"]]
  :ring {:handler proxy.handler/app
         :init proxy.handler/init
         :destroy proxy.handler/destroy}
  :profiles {
    :dev {
         :dependencies [[ring/ring-devel "1.4.0-beta2"]]
    }
    :production {
         :ring {:stacktraces? false
                :auto-reload? false}
    }})
