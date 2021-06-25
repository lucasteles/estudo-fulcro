(ns app.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defonce app (app/fulcro-app))

(defsc Car [this {:car/keys [id model]}]
  {:query [:car/id :car/model]
   :ident :car/id}
  (dom/li "Modelo: " model))
(def ui-car (comp/factory Car {:key-fn :car/id}))

(defsc Person [this {:person/keys [id name cars]}]
  {:query [:person/id :person/name {:person/cars (comp/get-query Car)}]
   :ident :person/id}
  (dom/div
    (dom/div (str id " - " name))
    (dom/h4 "Carros:")
    (dom/ul (map ui-car cars))))

(def ui-person (comp/factory Person {:key-fn :person/id}))

(defsc Root [this {person :root/person}]
  {:query [{:root/person (comp/get-query Person)}]}
  (dom/div (ui-person person)))



(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/mount! app Root "app")
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload"))


(comment
  (shadow/repl :main)
  (js/console.log "ola")
  (keys shadow)
  (js/alert "hi")

  (app/current-state app)
  (reset! (::app/state-atom app) {})

  (merge/merge-component!
    app Person
    #:person {:id   1
              :name "Lucas"
              :cars [#:car{:id 22 :model "Opala"}
                     #:car{:id 23 :model "Caravan"}]}
    :replace [:root/person])
  (app/schedule-render! app)
  )
