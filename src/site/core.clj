(ns site.core
  (:require
   [esthatic.core :as es]
   [esthatic.data :as esd]
   [esthatic.hiccup :as esh]
   [clojure.string :as str]
   [clojure.java.io :as io]))

(defn footer [{data :data :as opts}]
  [:div#footer
   [:css
    [:#footer {:background-color "#666"
               :color "white"
               :$margin [10 0 0]
               :$height 20 :text-align "center"
               :$padding [3 0] }
     [:img {:height "60px"}]

     [:.footer-container {:$width 40 :text-align "center" :margin "0 auto"}]
     [:p {:$text 1}]]]

   [:div.footer-container
    [:h2 [:img.logo {:src (es/href opts "/imgs/logo.png")}] [:> :brand]]
    [:h4 [:> :moto]]
    [:span.by "by @niquola"]]])

(def style
  [:body {:font-family "'Exo 2'"
          :$text [1 1.5]
          :font-weight "300"}
   [:p {:$push-bottom 1}]
   [:.block {:margin-top "40px"}]
   [:nav
    [:&.navbar-default {:background-color "white"
                        :padding-top "10px"
                        :border-bottom "1px solid #ddd"}]
    [:&.navbar {:margin 0} [:.navbar-brand {:font-weight "bold"}]]
    [:a {:color "#666"}]
    ]])

(defn layout [h]
  (fn [{data :data params :params :as opts}]
    [:html
     [:head
      [:bs/css]
      [:fa/css]
      [:goog/font :Exo-2]
      [:goog/font :Open-Sans]
      [:link {:rel "icon" :href (es/href opts "/imgs/icon.png") :type "image/x-icon"}]
      [:css style]
      [:css
       [:body
        [:nav.navbar
         {:border-radius 0}
         [:li [:a {:color "#666"
                   :border-bottom "3px solid transparent"}
               [:&:hover {:border-color "#4cc61e"
                          :color "black"
                          :background "transparent"}]]]]
        (when (:invert-menu params)
          [:&
           [:nav.navbar
            {:background (str  "url(" (es/href opts "/imgs/bg.png") ") #583426")
             :margin-bottom "40px"}
            [:.navbar-brand {:color "white"
                             :background-image (str  "url(" (es/href opts "/imgs/logo.png") ")")
                             :background-size "40px"
                             :background-repeat "no-repeat"
                             :padding-left "51px"
                             :background-position-y "9px"}]
            [:li.active [:a {:border-bottom "3px solid #4cc61e"}]]
            [:li [:a {:color "white"}
                  [:&:hover {:background-color "#291e1a"
                             :color "white"}]]
             ]]])]]]
     [:body
      [:bs/menu {:source [:menu]}]
      (h opts)
      (footer opts)
      ]]))

(defn doc-layout [h]
  (fn [opts]
    [:div.container (h opts)]))


(defn moto [{data :data :as opts}]
  [:div#moto
   [:css
    [:#moto {:margin 0
             :background-color "#7B3F00"
             :color "white"
             :border-bottom "1px solid #eee"}
     [:#coffee
      {:padding "80px 0 100px"
       :margin "0 auto"
       :text-align "center"
       :background (str  "url(" (es/href opts "/imgs/bg.png") ") #583426")
       :color "white"}
      [:.logo {:height "105px"
               :vertical-align "top"
               :margin-right "20px"
               :display "inline-block"}]
      [:.text {:display "inline-block"
               :vertical-align "top"}
       [:.brand {:font-size "60px"
                 :display "block"}]
       [:.moto  {:font-size "24px"
                 :font-weight "300"
                 :display "inline-block"}]]]]]

   [:div#coffee
    [:h1
     [:img.logo {:src (es/href opts "/imgs/logo.png")}]
     [:span.text
      [:span.brand "MOCCHIATO"]
      [:span.moto "ClojureScript arrives on server"]]]]])

(defn icon [nm]
  [:i {:class (str "fa fa-" (name nm))}])


(defn $index [{data :data :as opts}]
  [:div#index
   (moto opts)
   [:div.container
    [:div#features
     [:css
      [:#features {:margin "40px 0 60px"}
       [:img {:width "40px" :opacity 0.7 :margin-right "10px"}]]]
     [:div.row
      (for [{:keys [title text img]} (get-in data [:features])]
        [:div.col-md-6
         [:h3 [:img {:src (es/href opts (str "/imgs/" img))}] title]
         [:p text]])]]
    [:div.block [:md/doc "index.md"]]]])

(defn $doc [{{id :doc-id} :params data :data :as opts}]
  [:div.container
   [:css
    [:.docs-nav
     {:margin-top "20px"
      :border-left "1px solid #ddd"}
     [:li
      [:a {:color "#888"
           :padding "5px 20px"
           }]
      ]
     ]
    ]
   
   [:.row 
    [:.col-md-9
     [:md/doc (str "docs/" id ".md")]]
    [:.col-md-3.docs-nav
     [:h3 "Documentation"]
     [:ul.nav
      (for [f (:files data)]
        [:li [:a {:href (:name f)} (:name f)]])]]]])

(defn with-ls [h]
  (fn [req]
    (let [files (mapv (fn [x]
                        {:name (.getName x)
                         :path (.getPath x)})
                      (file-seq (io/file (io/resource "docs"))))]
      (println files)
      (h (assoc-in req [:data :files] files)))))



(def routes
  {:es/mw [(esd/with-yaml "data.yaml")
           #'es/hiccup-mw
           #'layout]

   :. #'$index
   "index" {:. #'$index}
   "docs" {:es/mw [#'with-ls]
           :es/params {:invert-menu true}
           :doc-id (constantly ["getting-started" "documentation"])
           [:doc-id]
           {:. #'$doc}}
   })

(def config
  {:port 8888
   :es/hiccup [#'esh/bootstrap-hiccup
               #'esh/yaml-hiccup
               #'esh/google-hiccup
               #'esh/data-hiccup
               #'esh/fa-icon-hiccup
               #'esh/markdown-hiccup]
   :es/routes #'routes })

;; (defn -main [] (es/generate config))


(comment
  (es/restart config)
  (es/generate (assoc config :es/prefix "/macchiato-site"))
  (require '[clojure.java.shell :as sh])

  (defn publish [config]
    (println (sh/sh "bash" "-c" "cd dist && git init && git add .  && git commit -m 'build' && git remote add origin https://github.com/niquola/macchiato-site.git && git checkout -b gh-pages && git push -f origin gh-pages"))
    )

  (publish {})
  )
