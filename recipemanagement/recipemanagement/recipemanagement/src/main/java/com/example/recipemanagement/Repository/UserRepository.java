package com.example.recipemanagement.Repository;

import com.example.recipemanagement.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username); // Custom method to find a user by username
}
