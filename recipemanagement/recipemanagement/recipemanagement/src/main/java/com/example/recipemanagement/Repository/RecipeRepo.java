package com.example.recipemanagement.Repository;

import com.example.recipemanagement.Model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecipeRepo extends MongoRepository<Recipe, String> {
}
