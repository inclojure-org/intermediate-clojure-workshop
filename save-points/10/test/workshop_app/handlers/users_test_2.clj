(ns workshop-app.handlers.users-test-2
  (:require [clojure.test :refer :all]
            [workshop-app.handlers.users :as wahu]
            [workshop-app.db.sqlite :as wads]
            [workshop-app.db.in-mem :as wadim]))

;; pattern 2. don't create the connection but instead redefine
;; it to an inmemory implementation.
;; these are useful when you communicate to a database over
;; a network
(use-fixtures :each (fn [t]
                      (with-redefs [wads/conn wadim/conn
                                    wads/create! wadim/create!
                                    wads/update! wadim/update!
                                    wads/read (fn [conn k] {"dob" (wadim/read conn k)})
                                    wads/delete! wadim/delete!]
                        (t))))


(deftest all-handlers-test
  (testing "Testing all handlers in one go."
    (are [expected-response actual-response] (= expected-response actual-response)
                                             {:status 201 :body "Created user."} (wahu/add-person {:name "Joel Victor"
                                                                                                   :dob  "2001-01-01"})
                                             {:status 200 :body "Updated user."} (wahu/update-person {:name "Joel Victor"
                                                                                                      :dob  "2000-01-01"})
                                             {:status 200 :body "Deleted user."} (wahu/delete-person "Joel Victor"))))