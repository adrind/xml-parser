(ns xml-parser.parse
  (:require
    [clojure.xml :as xml]
    [clojure.java.io :as io]
    [clojure.string :as stri]))

(defn load-content [file] (:content (xml/parse (io/input-stream file))))

;;Filter by any tag name
(defn find-tag [content tag] (filter #(= (:tag %) tag) content))

(defn find-first-tag [content tag] (first (find-tag content tag)))

;;Lets you query nested xml tags
;;Sample use: (find-nested-tag (load-content "file.xml") [:first-tag :second-tag :third]
(defn find-nested-tag [content tags]
  (reduce #(:content (find-first-tag %1 %2)) content tags))

;;Find custom block definitions
(defn find-custom-blocks [content]
  (map #(select-keys (:attrs %) [:s, :type]) (find-tag (:content content) :block-definition)))
