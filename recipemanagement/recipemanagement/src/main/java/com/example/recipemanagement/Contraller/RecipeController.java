package com.example.recipemanagement.Contraller;

import com.example.recipemanagement.Model.Recipe;
import com.example.recipemanagement.Repository.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class RecipeController {

    @Autowired
    private RecipeRepo recipeRepo;

    @PostMapping("/addRecipe")
    public String addRecipe(@RequestBody Recipe recipe) {
        if (recipe.getId() == null || recipe.getId().isEmpty()) {
            return "Recipe ID is required!";
        }

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

    @GetMapping("/searchRecipes")
    public List<Recipe> searchRecipes(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String instructions) {

        List<Recipe> allRecipes = recipeRepo.findAll();

        if (id != null) {
            return allRecipes.stream()
                    .filter(r -> r.getId().equalsIgnoreCase(id))
                    .collect(Collectors.toList());
        }

        if (title != null) {
            return allRecipes.stream()
                    .filter(r -> r.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (ingredients != null) {
            return allRecipes.stream()
                    .filter(r -> r.getIngredients() != null && r.getIngredients().stream()
                            .anyMatch(ingredient -> ingredient.toLowerCase().contains(ingredients.toLowerCase())))
                    .collect(Collectors.toList());
        }

        if (instructions != null) {
            return allRecipes.stream()
                    .filter(r -> r.getInstructions() != null && r.getInstructions().stream()
                            .anyMatch(instruction -> instruction.toLowerCase().contains(instructions.toLowerCase())))
                    .collect(Collectors.toList());
        }

        return allRecipes; // If no filter provided, return all recipes
    }

    @GetMapping("/getallRecipes")
    public List<Recipe> getAllRecipes() {
        try {
            // Get all recipes and sort them numerically if IDs are numbers
            return recipeRepo.findAll().stream()
                    .sorted(Comparator.comparing(r -> Integer.parseInt(r.getId())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @PutMapping("/updateRecipe")
    public String updateRecipe(@RequestBody Recipe recipe) {
        if (recipe.getId() == null || recipe.getId().isEmpty()) {
            return "Recipe ID is required!";
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
    public String deleteRecipe(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return "Recipe ID is required!";
        }

        if (recipeRepo.existsById(id)) {
            recipeRepo.deleteById(id);
            return "Recipe deleted!";
        } else {
            return "Recipe not found!";
        }
    }

    @GetMapping("/getRecipe/{id}")
    public Object getRecipe(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            return "Recipe ID is required!";
        }

        return recipeRepo.findById(id)
                .<Object>map(recipe -> recipe)
                .orElse("Recipe with ID '" + id + "' not found!");
    }
}