package com.ml.shubham0204.facenet_android.domain

import com.ml.shubham0204.facenet_android.data.PersonDB
import com.ml.shubham0204.facenet_android.data.PersonRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonUseCase @Inject constructor(val personDB: PersonDB) {

    fun addPerson(name: String, numImages: Long): Long {
        return personDB.addPerson(PersonRecord(personName = name, numImages = numImages, addTime=System.currentTimeMillis()))
    }

    fun removePerson(id: Long) {
        personDB.removePerson(id)
    }

    fun getAll(): Flow<List<PersonRecord>> =
        personDB.getAll()

    fun getCount(): Long = personDB.getCount()

}
