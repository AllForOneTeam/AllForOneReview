package com.market.allForOneReview.domain.article.config;


import com.market.allForOneReview.domain.article.Repository.CategoryRepository;
import com.market.allForOneReview.domain.article.entity.Category;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private CategoryRepository categoryRepository;

    @jakarta.annotation.PostConstruct
    public void initData() {
        saveIfNotExists("drama", "romance");
        saveIfNotExists("drama", "action");
        saveIfNotExists("drama", "sf");
        saveIfNotExists("drama", "horror");
        saveIfNotExists("drama", "comedy");
        saveIfNotExists("drama", "others");
        saveIfNotExists("movie", "movie1");
        saveIfNotExists("novel", "novel1");
        saveIfNotExists("clothes", "clothes1");
        saveIfNotExists("electronics", "electronics1");
        saveIfNotExists("restaurant", "restaurant1");
        saveIfNotExists("plays", "plays1");
    }
    private void saveIfNotExists(String category, String subCategory) {
        boolean exists = categoryRepository.findByCategoryAndSubCategory(category, subCategory).isPresent();
        if (!exists) {
            categoryRepository.save(new Category(category, subCategory));
        }
    }
}
