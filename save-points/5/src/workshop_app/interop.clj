(ns workshop-app.interop
  (:refer-clojure :rename {get cc-get})
  (:import (java.util Calendar Comparator ArrayList HashMap AbstractMap$SimpleEntry)
           (java.time.temporal ChronoUnit)
           (java.time LocalDate)))

;; class access
;; HashMap.class
HashMap                                           ;; a class

;; new HashMap();
(HashMap.)                                        ;; an object created from the class

;; HashMap hm = new HashMap();
(def hm (HashMap.))                               ;; bind the object to a var. More on var's later.

;; hm.put("a", 10);
(.put hm "a" 10)

;; hm.put("b", 10);
(.put ^HashMap hm "b" 10)                         ;; the first gives us a warning of how it cannot be resolved.

;; Member access
;; method access
;; hm.get("a")
(.get hm "a")

;; hm.get("b")
(.get hm "b")

;; "joel".toUpperCase();
(.toUpperCase "joel")

;; field access
;; new Point(10,20).x;
(.-x (java.awt.Point. 10 20))

;; static variables or methods access
;; Calendar.ERA
Calendar/ERA

;; Math.PI
Math/PI

;; System.getProperties()
(System/getProperties)

;; dot special form
;; hm.get("a");
(. hm get "a")

;; "joel".toUpperCase();
(. "joel" toUpperCase)

;; System.getProperties().get("os.name")
(. (. System (getProperties)) (get "os.name"))

;; double dot
(.. System getProperties (get "os.name"))

;; doto
;; HashMap dotoHm = new HashMap();
;; dotoHm.put("a", 10);
;; dotoHm.put("b", 20);
(def doto-hm (doto (HashMap.)
               (.put "a" 10)
               (.put "b" 20)))

;; new
;; HashMap newSyntaxHm = new HashMap();
(def new-syntax-hm (new HashMap))

;; accessing inner classes
;; AbstractMap.SimpleEntry("a", "b")
(AbstractMap$SimpleEntry. "a" "b")


;; reify example. Sort an array list of array list using
;; a custom comparator.
;; List al = new ArrayList();
;; List alChild1 = new ArrayList();
;; alChild1.add(1);
;; alChild1.add(2);
;; List alChild2 = new ArrayList();
;; alChild2.add(1);
;; alChild2.add(2);
;; List alChild3 = new ArrayList();
;; alChild3.add(1);
;; alChild3.add(2);
;; List alChild1 = new ArrayList();
;; alChild1.add(1);
;; alChild1.add(2);
;; al.add(alChild1);
;; al.add(alChild2);
;; al.add(alChild3);
;; al.sort(new Compartor<ArrayList> {
;;                       @Override
;;                       public int compare(ArrayList al1, ArrayList al2) {
;;                         return al1.elementAt(0) - al2.elementAt(0);
;;                       }
;;                     })
(let [l (doto (ArrayList.)
          (.add (doto (ArrayList.)
                  (.add 1)
                  (.add 2)))
          (.add (doto (ArrayList.)
                  (.add 2)
                  (.add 3)))
          (.add (doto (ArrayList.)
                  (.add 3)
                  (.add 4))))]
  ;; this modifies the list.
  (.sort l
         (reify Comparator
           (compare [_ al1 al2]
             (- (.get ^ArrayList al2 (int 0))
                (.get ^ArrayList al1 (int 0))))))
  l)


;; a small interop task. of finding the days between today and first January
;; following is a sample java code that you should translate to clojure interop code.
;;   LocalDate dateOne = LocalDate.of(2020,1,1);
;;   LocalDate dateTwo = LocalDate.now();
;;   long daysBetween = ChronoUnits.DAYS.between(dateOne, dateTwo);
#_(let [date-one _
        date-two _]
    (_ _ date-one date-two))

