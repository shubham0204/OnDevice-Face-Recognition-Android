package com.ml.shubham0204.facenet_android.data

import io.objectbox.kotlin.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Single

@Single
class PersonDB {

    private val personBox = ObjectBoxStore.store.boxFor(PersonRecord::class.java)

    fun addPerson(person: PersonRecord): Long {
        return personBox.put(person)
    }

    fun removePerson(personID: Long) {
        personBox.removeByIds(listOf(personID))
    }

    // Returns the number of records present in the collection
    fun getCount(): Long = personBox.count()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAll(): Flow<MutableList<PersonRecord>> =
        personBox.query(PersonRecord_.personID.notNull()).build().flow().flowOn(Dispatchers.IO)
}
