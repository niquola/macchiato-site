(ns site.md
  (:require
   [clojure.java.io :as io]
   [clj-yaml.core :as yaml]
   [endophile.hiccup :as eh]
   [endophile.core :as ec]))

(defn basename [name]
  (get (re-find #"(.+?).md$" name) 1))

(defn docs-url [name]
  (clojure.string/replace name #"\ " "_"))
(defn docs-title-by-url [name]
  (clojure.string/replace name #"_" " "))

(def docs-file
  (slurp (io/resource "data/docs.yaml")))

(def doc (yaml/parse-string docs-file))

(defn docs-pages []
  (reduce (fn [acc v]
            (conj acc (docs-url (:title v)) ))
          [] doc))

(defn docs-page [title]
  (first (filter #(= (:title %) (docs-title-by-url title)) doc)))

(defn md [mdst]
  (try (eh/to-hiccup (ec/mp mdst))
       (catch Exception e
         [:p "Cannot parse md" [:pre mdst] [:pre (pr-str e)]])))

(defn md-to-hiccup [file]
  (if-let [res (io/resource (str "docs/" file))]
    (md (slurp res))
    [:h1.error {:style "color: red;"} (str "File does not exists " file)]))

(defn doc-file-hiccup [file]
  (if (string? file)
    (md-to-hiccup file)
    (into [:div ] (map md-to-hiccup file))))
