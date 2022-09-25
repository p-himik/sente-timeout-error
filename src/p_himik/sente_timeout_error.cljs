(ns p-himik.sente-timeout-error
  (:require [taoensso.sente :as sente]))

(defn main []
  (let [{:keys [send-fn]}
        (sente/make-channel-socket-client! "/"
                                           nil
                                           {:type          :ws
                                            ;; Never reconnect to ease debugging.
                                            :backoff-ms-fn (constantly js/Number.MAX_SAFE_INTEGER)
                                            ;; Default http-kit port.
                                            :port          8090})]
    (js/setTimeout (fn []
                     (send-fn [::message "hello"]
                              1000
                              #(js/console.log "Response" %)))
                   ;; Delaying a bit - otherwise it seems to sometimes be handled
                   ;; while the WS connection is still being configured.
                   1000)))
