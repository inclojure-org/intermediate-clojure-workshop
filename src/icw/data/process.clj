(ns icw.data.process
  (:require [clojure.string :as cs]
            [icw.java-interop.jdbc :as jdbc]
            [clojure.data.csv :as csv]
            [icw.data.gen :as data-gen]))


;; Reading and processing data from resources/data/albumlist.csv

(def album-lines (drop 1
                       (line-seq (clojure.java.io/reader
                                  "resources/data/albumlist.csv"))))

(comment
  (first album-lines))

;; Parsing

(defn parse-line
  "Input
  line -> \"1,1967,Sgt. Pepper's Lonely Hearts Club Band,The Beatles,Rock,\"Rock & Roll, Psychedelic Rock\"

  Output
  [\"1\"
   \"1967\"
   \"Sgt. Pepper's Lonely Hearts Club BandThe Beatles\"
   \"The Beatles\"
   \"Rock\"
   [\"Rock & Roll\" \"Psychedelic Rock\"]"
  [line]
  )


(comment
  (= (parse-line "1,1967,Sgt. Pepper's Lonely Hearts Club Band,The Beatles,Rock,\"Rock & Roll, Psychedelic Rock\"")
     ["1"
      "1967"
      "Sgt. Pepper's Lonely Hearts Club BandThe Beatles"
      "The Beatles"
      "Rock"
      ["Rock & Roll"
       "Psychedelic Rock"]]))

(comment
  (map parse-line album-lines))


(defn line-vec->line-map
  "xs -> [\"1\"
          \"1967\"
          \"Sgt. Pepper's Lonely Hearts Club BandThe Beatles\"
          \"The beatles\"
          \"Rock\"
          [\"Rock & Roll\"
           \"Psychedelic Rock\"]]
  Output
  {:number \"1\"
   :year \"1967\"
  :artist \"The beatles\"p
   :album \"Sgt. Pepper's Lonely Hearts Club BandThe Beatles\"
   :genre \"Rock\"
   :subgenre-xs [\"Rock & Roll\"
                 \"Psychedelic Rock\"]}"
  [xs]
  )

(comment
  (= (line-vec->line-map ["1"
                         "1967"
                          "Sgt. Pepper's Lonely Hearts Club BandThe Beatles"
                          "The Beatles"
                         "Rock"
                         ["Rock & Roll"
                          "Psychedelic Rock"]])
     {:number "1"
      :year "1967"
      :artist "The Beatles"
      :album "Sgt. Pepper's Lonely Hearts Club BandThe Beatles"
      :genre "Rock"
      :subgenre ["Rock & Roll"
                 "Psychedelic Rock"]}))

(comment
  (take 10
        (map #_FIXME
             (map #_FIXME
                  album-lines))))


(defn line-xs->album-xs
  [line-xs]
  ;; Use parse-line to convert list of strings to list of vectors
  ;; Use line-vec->line-map to convert list of vectors to list of map
  )

(defn populate-db
  []
  (jdbc/init-db)
  (let [albums (line-xs->album-xs album-lines)]
    (doseq [album albums]
      (jdbc/insert! album))))

;; Check http://localhost:6789/albums

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Some more exploration with sequences
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn line-xs->rock-albums-xs
  [line-xs]
  (comment (map #_FIXME
                (filter #_FIXME
                        #_FIXME))))

(comment (= (take 5 (line-xs->rock-albums-xs album-lines))
            '("Sgt. Pepper's Lonely Hearts Club Band"
              "Pet Sounds"
              "Revolver"
              "Highway 61 Revisited"
              "Exile on Main St.")))


(defn line-xs->albums-xs-before
  "Lists all albums before 'year'"
  [line-xs year]
  (comment (filter #_FIXME
                   (map #_FIXME
                        #_FIXME))))

(comment (= (take 5 (line-xs->albums-xs-before album-lines 1987))
            '([1967 "Sgt. Pepper's Lonely Hearts Club Band"]
              [1966 "Pet Sounds"]
              [1966 "Revolver"]
              [1965 "Highway 61 Revisited"]
              [1965 "Rubber Soul"])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Some more exploration with reduce
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn artists-with-range
  "Artists who have most genres"
  [line-xs]
  (map #_FIXME
       (sort-by #_FIXME
                (reduce #_FIXME
                        {}
                        (map #_FIXME
                             line-xs)))))

(comment (= (take 5 (artists-with-range album-lines))
            '("Various Artists"
              "Bob Dylan"
              "Ray Charles"
              "Muddy Waters"
              "Talking Heads")))



(defn find-popular-year
  ;; Find top years for album releases
  [line-xs]
  (sort-by #_FIXME
           (reduce #_FIXME
                   {}
                   (map #_ME
                        line-xs))))

(comment (= (take 5 (find-popular-year album-lines))
            '(["1970" 26]
              ["1972" 24]
              ["1973" 23]
              ["1969" 22]
              ["1971" 21])))

(defn find-popular-artists
  [line-xs]
  )


(comment (= (find-popular-artists album-lines)
            '(["The Beatles" 10]
              ["Bob Dylan" 10]
              ["The Rolling Stones" 9]
              ["Bruce Springsteen" 7]
              ["The Who" 7])))


;; We can transform data a great deal with just map, reduce and filter

;; Generally there are three patterns
;; Seq of N -> Seq of N (map)
;; Seq of N -> Seq of M (N > M) (filter)
;; Seq of N -> Any data structure (reduce)

;; This is great but what about lazy sequences all we processed till now was in-memory data


;; A stream from future timeline from 2040
data-gen/get-albums-xs

;; DONT evaluate the function in REPL it's a lazy sequence.
;; It's a list of albums generated from 2040 till infinity

(take 10 (data-gen/get-albums-xs))


;; Let's some of the functions we created till now
(take 10 (line-xs->rock-albums-xs (data-gen/get-albums-xs)))

;; We can use line-xs->albums-xs-before to just get limited set

;; Right?

(comment
  ;; This will evaluate for infinity since filter does not stop evaluation
  ;; We will just get infinite list of nils after year 2045
  (line-xs->albums-xs-before (data-gen/get-albums-xs)
                             2045))


;; https://clojure.org/api/cheatsheet
;; Espeically look for seq in and seq out

(#_FIXME identity
         (line-xs->albums-xs-before (data-gen/get-albums-xs)
                                    2045))

;; Try applying functions we have created till now


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; END of chapter 1
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Jump back to icw.core
