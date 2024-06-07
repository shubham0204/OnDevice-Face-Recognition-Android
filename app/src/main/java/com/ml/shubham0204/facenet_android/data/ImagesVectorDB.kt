package com.ml.shubham0204.facenet_android.data

class ImagesVectorDB {

    private val imagesBox = ObjectBoxStore.store.boxFor(FaceImageRecord::class.java)

    fun addFaceImageRecord(record: FaceImageRecord) {
        imagesBox.put(record)
    }

    fun removeFaceRecordsWithPersonID(personID: Long) {
        imagesBox.removeByIds(
            imagesBox.query(FaceImageRecord_.personId.equal(personID)).build().findIds().toList()
        )
    }
}
