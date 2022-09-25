(ns p-himik.sente-timeout-error
  (:require [clojure.core.async :as a]
            [org.httpkit.server :as hks]
            [ring.middleware.keyword-params]
            [ring.middleware.params]
            [ring.middleware.session]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]))

(defn -main [& _]
  (let [{:keys [ch-recv ajax-get-or-ws-handshake-fn]}
        (sente/make-channel-socket! (get-sch-adapter)
                                    {:csrf-token-fn nil})

        http-kit-channel (atom nil)
        handler (fn [req]
                  (assert (= (:request-method req) :get))
                  (let [response (ajax-get-or-ws-handshake-fn req)]
                    (reset! http-kit-channel (:body response))
                    response))]
    (hks/run-server (-> handler
                        ring.middleware.keyword-params/wrap-keyword-params
                        ring.middleware.params/wrap-params))
    (a/go-loop []
      (let [[event-id] (:event (a/<! ch-recv))]
        (when (not= (namespace event-id) "chsk")
          (when-some [c @http-kit-channel]
            (hks/close c)
            (compare-and-set! http-kit-channel c nil))))
      (recur))
    (println "The server is running")))
