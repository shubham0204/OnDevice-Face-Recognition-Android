package com.ml.shubham0204.facenet_android.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Entity
data class PersonRecord(
    @PrimaryKey(autoGenerate = true)
    var personID: Long = 0,
    var personName: String = "",
    var numImages: Long = 0,
    var addTime: Long = 0,
)

@Dao
interface PersonRecordDao {
    @Insert
    fun insertPersonRecord(personRecord: PersonRecord): Long

    @Query("SELECT * FROM PersonRecord")
    fun getAllPersonRecords(): Flow<List<PersonRecord>>

    @Query("DELETE FROM PersonRecord WHERE personName = :personName")
    fun deletePersonRecord(personName: String)

    @Query("SELECT count(*) FROM PersonRecord")
    fun getCount(): Long
}

data class RecognitionMetrics(
    val timeFaceDetection: Long,
    val timeVectorSearch: Long,
    val timeFaceEmbedding: Long,
    val timeFaceSpoofDetection: Long,
)
