package com.atenasconsultores.spring_boot_crash_course.database.respository

import com.atenasconsultores.spring_boot_crash_course.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository: MongoRepository<Note, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId):List<Note>
}
