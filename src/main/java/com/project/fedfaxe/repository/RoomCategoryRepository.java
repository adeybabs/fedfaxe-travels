package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.RoomCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomCategoryRepository extends MongoRepository<RoomCategory, String> {
}
