(ns site.core
  (:require [esthatic.core :as es]
            [gardner.core :as g]
            [garden.stylesheet :as gs]
            [clj-yaml.core :as yaml]
            [garden.units :as gu]
            [endophile.core :as ec ]
            [clojure.java.io :as io]
            [endophile.hiccup :as  eh]
            [garden.units :as gu]
            [dali.io :as dali]
            [site.md :refer [docs-url docs-pages docs-page basename doc-file-hiccup] :as md]
            ))

(defn navigation [{data :data :as opts}]
  [:nav.navbar.navbar-default.navbar-static-top
   [:$style
    [:nav
     [:&.navbar-default {:background-color "white"
                         :padding-top "10px"
                         :border-bottom "1px solid #ddd"}]
     [:&.navbar {:margin 0}
      [:.navbar-brand {:font-weight "bold"}]]]]
   [:div.container
    [:a.navbar-brand {:href "/index.html"}
     (get-in data [:text :brand])]
    [:ul.nav.navbar-nav
     (for [item (get-in data [:menu])]
       [:li [:a {:href (:href item)}
             (when-let [ic (:icon item)] [:$icon ic])
             " "
             (:text item)]])]]])

(defn footer [{data :data :as opts}]
  [:div#footer
   [:$style
    [:#footer {:background-color "#666"
               :color "white"
               :$margin [10 0 0]
               :$height 20 :text-align "center"
               :$padding [3 0] }
     [:img {:height "60px"}]

     [:.footer-container {:$width 40 :text-align "center" :margin "0 auto"}]
     [:p {:$text 1}]]]

   [:div.footer-container
    [:h2
     [:img.logo {:src "/imgs/mochiato.png"}]
     (get-in data [:text :footer :header])]
    [:p (get-in data [:text :footer :text])]
    
    [:span.by "by @niquola"]
    ]])

(def style
  [:body {:font-family "'Exo 2'"
          :$text [1 1.5]
          :font-weight "300"}
   [:p {:$push-bottom 1}]
   [:.block {:margin-top "40px"}]])

(defn hlayout [h]
  (fn [{data :data :as opts}]
    [:html
     [:head
      [:meta {:name "viewport" :content "initial-scale=1, maximum-scale=1"}]
      [:$cdn-css :bootsrtrap]
      [:$cdn-css :fontawesome]
      [:$google-font :Exo-2]
      [:$google-font :Open-Sans]
      [:$style style]]
     [:body
      (navigation opts)
      (h opts)
      [:script {:type "text/javascript" :src "//maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"}]
      (footer opts)]]))

(defn doc-layout [h]
  (fn [opts]
    [:div.container (h opts)]))


(defn moto [{data :data :as opts}]
  [:div#moto
   [:$style
    [:#moto {:margin 0
             :background-color "blanchedalmond"
             :border-bottom "1px solid #eee"}
     [:#coffee
      {:padding "60px 0 40px"
       :background-color "#814a35"
       :color "white"}
      [:.logo {:height "100px"}]
      [:h1 {;;:text-align "center"
            }
       [:.brand {:font-size "60px"}]
       [:.moto  {:font-size "20px"
                 :display "inline-block"
                 }]]
      ]
     [:p {:font-size "22px"
          :line-height "40px"
          :color "#814a35"
          :margin "30px auto 40px"}]]]

   [:div#coffee
    [:div.container
     [:h1
      [:span.brand "MOCCHIATO"]
      [:img.logo {:src "/imgs/mochiato.png"}]
      [:span.moto "ClojureScript" [:br] "arrives on server"]]]
    ]
   [:div.container
    [:p  (get-in data [:text :moto :text])]]])

(defn icon [nm]
  [:i {:class (str "fa fa-" (name nm))}])

(defn features [{data :data :as opts}]
  [:div#features
   [:$style
    [:#features {:margin "40px 0 60px"}
     [:img {:width "40px" :opacity 0.7 :margin-right "10px"}]]]
   [:div.row
    (for [{:keys [title text img]} (get-in data [:features])]
      [:div.col-md-6
       [:h3 [:img {:src (str "imgs/" img)}] title]
       [:p text]]
      )]])

(defn $index [{data :data :as opts}]
  [:div#index
   (moto opts)
   [:div.container
    (features opts)
    [:$gist "niquola/6c73b5bc62e301b1c465c264d91fcc5f"]
    [:div.block [:$md "docs/index.md"]]]])

(defn $docs [{{id :id} :params data :data :as opts}]
  [:div.container.docs
   [:div.row
    [:nav#submenu.col-sm-2
     [:ul.nav
      (for [i (:docs data)] ^{:key (basename (:title i))}
        [:li [:a {:href (es/url "docs" (docs-url (:title i)))} (:title i)]])]]
    [:div.col-sm-8
     [:div.markdown
      (let [file (:file (docs-page id))]
        (doc-file-hiccup file))]] ]])

(def routes
  {:layout #'hlayout
   :GET #'$index
   "getting-started" {:layout #'doc-layout
                      :GET (es/md-page "docs/getting-started.md")}
   "docs" {:GET #'$docs
           [:id] {:GET #'$docs}
           :id docs-pages}
   "index" {:GET #'$index}})

(def styles
  {:vars {:v 18 :h 10 :g 300}
   :macros {}
   :colors {:gray "#474747" :text "#192F3D"
            :bereza-text "#49645F"
            :bereza "#B4E1DA"
            :blue "#2B4961"
            :light-blue "#406E93"
            :white "#fff"
            :text-yellow "#FDCD00"
            :text-muted "#47525d"
            :btn-gray "#424952"
            :black "black"
            :transparent "transparent"}})

(def config {:routes #'routes
             :styles #'styles})

(defn -main [] (es/generate config))

(def handler
  (es/mk-handler config))

(defn start-server []
  (def stop (es/start (merge config {:port 3000 :auto-refresh? true}))))

(comment
  (start-server)
  (es/generate (assoc config :prefix "site/"))
  (stop)
  )
