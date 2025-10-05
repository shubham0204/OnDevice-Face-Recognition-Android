package com.ml.shubham0204.facenet_android.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single

@Single
class ImagesVectorDB {
    private val imagesBox = ObjectBoxStore.store.boxFor(FaceImageRecord::class.java)

    fun addFaceImageRecord(record: FaceImageRecord) {
        imagesBox.put(record)
    }

    fun getNearestEmbeddingPersonName(
        embedding: FloatArray,
        flatSearch: Boolean,
    ): FaceImageRecord? {
        // Enabling `flatSearch` disables ObjectBox's vector search (ANN based on HNSW)
        // and performs a linear-search to precisely compute the nearest neighbors
        if (flatSearch) {
            val allRecords = imagesBox.all
            val numThreads = 4
            val batchSize = allRecords.size / numThreads
            val batches = allRecords.chunked(batchSize)
            val results =
                runBlocking {
                    batches
                        .map { batch ->
                            async(Dispatchers.Default) {
                                var bestMatch: FaceImageRecord? = null
                                var bestDistance = Float.NEGATIVE_INFINITY
                                for (record in batch) {
                                    val distance = cosineDistance(embedding, record.faceEmbedding)
                                    if (distance > bestDistance) {
                                        bestDistance = distance
                                        bestMatch = record
                                    }
                                }
                                Pair(bestMatch, bestDistance)
                            }
                        }.awaitAll()
                }
            return results.maxByOrNull { it.second }?.first
        }
        /*
        Use maxResultCount to set the maximum number of objects to return by the ANN condition.
        Hint: it can also be used as the "ef" HNSW parameter to increase the search quality in combination
        with a query limit. For example, use maxResultCount of 100 with a Query limit of 10 to have 10 results
        that are of potentially better quality than just passing in 10 for maxResultCount
        (quality/performance tradeoff).
         */
        return imagesBox
            .query(FaceImageRecord_.faceEmbedding.nearestNeighbors(embedding, 10))
            .build()
            .findWithScores()
            .map { it.get() }
            .firstOrNull()
    }

    private fun cosineDistance(
        x1: FloatArray,
        x2: FloatArray,
    ): Float {
        var mag1 = 0.0f
        var mag2 = 0.0f
        var product = 0.0f
        for (i in x1.indices) {
            mag1 += x1[i] * x1[i]
            mag2 += x2[i] * x2[i]
            product += x1[i] * x2[i]
        }
        mag1 = kotlin.math.sqrt(mag1)
        mag2 = kotlin.math.sqrt(mag2)
        return product / (mag1 * mag2)
    }

    fun removeFaceRecordsWithPersonID(personID: Long) {
        imagesBox.removeByIds(
            imagesBox
                .query(FaceImageRecord_.personID.equal(personID))
                .build()
                .findIds()
                .toList(),
        )
    }
}
