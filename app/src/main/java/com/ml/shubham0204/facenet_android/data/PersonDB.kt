package com.ml.shubham0204.facenet_android.data

class PersonDB {

    private val personBox = ObjectBoxStore.store.boxFor(PersonRecord::class.java)

    fun addPerson(person: PersonRecord) {
        personBox.put(person)
    }

    fun removePerson(personID: Long) {
        personBox.removeByIds(listOf(personID))
    }
}
