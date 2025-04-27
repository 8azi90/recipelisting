package com.example.recipemanagement.Contraller;

import com.example.recipemanagement.Model.Recipe;
import com.example.recipemanagement.Repository.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class RecipeController {

    @Autowired
    private RecipeRepo recipeRepo;

    // Protect Recipe operations by checking if the user is logged in
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @PostMapping("/addRecipe")
    public String addRecipe(@RequestBody Recipe recipe, HttpSession session) {

        if (recipeRepo.existsById(recipe.getId())) {
            return "Recipe with this ID already exists!";
        }

        try {
            recipeRepo.save(recipe);
            return "Recipe added successfully!";
        } catch (Exception e) {
            return "Failed to add recipe: " + e.getMessage();
        }
    }

    @GetMapping("/getallRecipes")
    public List<Recipe> getAllRecipes(HttpSession session) {
        if (!isAuthenticated(session)) {
            return List.of();  // Empty list if not authenticated
        }
        return recipeRepo.findAll();
    }

    @PutMapping("/updateRecipe")
    public String updateRecipe(@RequestBody Recipe recipe, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "You must be logged in to update a recipe!";
        }

        return recipeRepo.findById(recipe.getId()).map(existing -> {
            existing.setTitle(recipe.getTitle());
            existing.setIngredients(recipe.getIngredients());
            existing.setInstructions(recipe.getInstructions());
            recipeRepo.save(existing);
            return "Recipe updated!";
        }).orElse("Recipe not found!");
    }

    @DeleteMapping("/deleteRecipe/{id}")
    public String deleteRecipe(@PathVariable String id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "You must be logged in to delete a recipe!";
        }

        if (recipeRepo.existsById(id)) {
            recipeRepo.deleteById(id);
            return "Recipe deleted!";
        } else {
            return "Recipe not found!";
        }
    }

    @GetMapping("/getRecipe/{id}")
    public Object getRecipe(@PathVariable String id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return "You must be logged in to view recipes!";
        }

        return recipeRepo.findById(id)
                .<Object>map(recipe -> recipe)
                .orElse("Recipe with ID '" + id + "' not found!");
    }
}
