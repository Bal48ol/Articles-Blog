package org.articlesblog.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.articlesblog.dto.articledto.GetAllArticlesDTO;
import org.articlesblog.services.article.ArticleService;
import org.articlesblog.services.hibernatesearch.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Main page")
@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {
    private final ArticleService articleService;
    private final SearchService searchService;
    @GetMapping("/")
    @Operation(summary = "Переадресация на главную страницу")
    public String slashArticles(){
        return "redirect:/articles/page/1";
    }

    @GetMapping("/articles")
    @Operation(summary = "Переадресация на главную страницу")
    public String anotherSlashArticles(){
        return "redirect:/articles/page/1";
    }

    @GetMapping("/articles/page/{id}")
    @Operation(summary = "Получение статей по страницам")
    public String getArticlesByPage(@PathVariable Integer id, Model model) {
        int pageSize = 9;
        List<GetAllArticlesDTO> articles = articleService.getAllArticles();

        int totalPages = (int) Math.ceil((double) articles.size() / pageSize);
        int startIndex = (id - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, articles.size());

        if (id < 1 || id > totalPages) {
            return "articles";
        }

        List<GetAllArticlesDTO> articlesOnPage = new ArrayList<>();

        for (int i = endIndex - 1; i >= startIndex; i--) {
            articlesOnPage.add(articles.get(i));
        }

        Collections.reverse(articlesOnPage);

        model.addAttribute("articles", articlesOnPage);
        model.addAttribute("title", "Страница " + id + " из " + totalPages);
        model.addAttribute("currentPage", id);
        model.addAttribute("totalPages", totalPages);

        return "articles";
    }

    @GetMapping("/auth")
    @Operation(summary = "Страница входа")
    public String auth(Model model){
        model.addAttribute("title", "Авторизация");
        return "auth";
    }

    @GetMapping("/about")
    @Operation(summary = "О нас")
    public String about(Model model){
        model.addAttribute("title", "О нас");
        return "about";
    }

    @GetMapping("/articles/search")
    @Operation(summary = "Поиск")
    public String searchArticles(@RequestParam("searchText") String searchText, Model model) {
        if (searchText.isEmpty()) {
            return "redirect:/articles/page/1";
        }

        List<GetAllArticlesDTO> articles = searchService.searchBy(searchText);
        model.addAttribute("articles", articles);
        return "articles";
    }
}