package com.ml.shubham0204.facenet_android.domain

import com.ml.shubham0204.facenet_android.data.AppDB
import com.ml.shubham0204.facenet_android.data.PersonRecord
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class PersonUseCase(
    private val appDB: AppDB
) {
    fun addPerson(
        name: String,
        numImages: Long,
    ): Long =
        appDB.db.personRecordsDao().insertPersonRecord(
            PersonRecord(
                personName = name,
                numImages = numImages,
                addTime = System.currentTimeMillis(),
            ),
        )

    fun removePerson(name: String) {
        appDB.db.personRecordsDao().deletePersonRecord(name)
    }

    fun getAll(): Flow<List<PersonRecord>> = appDB.db.personRecordsDao().getAllPersonRecords()

    fun getCount(): Long = appDB.db.personRecordsDao().getCount()
}
