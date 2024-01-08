package org.articlesblog.services.article;

import com.google.firebase.database.annotations.NotNull;
import lombok.RequiredArgsConstructor;
import org.articlesblog.dto.ArticleDTO;
import org.articlesblog.jpa.entity.Article;
import org.articlesblog.jpa.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService{
    private final ArticleRepository articleRepository;

    @Override
    public ArticleDTO getArticle(Integer id) {
        return articleRepository.findById(id)
                .map(article -> {
                    String dateChange = article.getDateChange() != null ? article.getDateChange().toString() : "-";
                    return new ArticleDTO(article.getId(), article.getTitle(), article.getDescription(),
                            article.getAuthor(),  article.getLabel(), article.getDateCreate().toString(), dateChange);
                })
                .orElse(null);
    }

    @Override
    public List<ArticleDTO> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return getArticleDTOS(articles);
    }

    @Transactional
    @Override
    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        Article article = new Article();
        article.setTitle(articleDTO.getTitle());
        article.setDescription(articleDTO.getDescription());
        article.setAuthor(articleDTO.getAuthor());
        article.setLabel(articleDTO.getLabel());
        article.setDateCreate(new Date());
        article.setDateChange(null);

        Article savedArticle = articleRepository.save(article);
        return new ArticleDTO(savedArticle.getId(), savedArticle.getTitle(), savedArticle.getDescription(),
                savedArticle.getAuthor(), article.getLabel(), savedArticle.getDateCreate().toString(), null);
    }

    @Transactional
    @Override
    public ArticleDTO updateArticle(Integer id, ArticleDTO articleDTO) {
        Article article = articleRepository.findById(id)
                .map(existingArticle -> {
                    existingArticle.setTitle(articleDTO.getTitle());
                    existingArticle.setDescription(articleDTO.getDescription());
                    existingArticle.setAuthor(articleDTO.getAuthor());
                    existingArticle.setLabel(articleDTO.getLabel());
                    existingArticle.setDateChange(new Date());
                    return articleRepository.save(existingArticle);
                })
                .orElseThrow(() -> new RuntimeException("Статья с id " + id + " не найдена."));

        return new ArticleDTO(article.getId(), article.getTitle(), article.getDescription(),
                article.getAuthor(), article.getLabel(), article.getDateCreate().toString(), article.getDateChange().toString());
    }

    @Transactional
    @Override
    public String deleteArticle(Integer id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        return articleOptional.map(article -> {
            articleRepository.deleteById(id);
            return "Статья " + id + " удалена";
        }).orElse("Неудачное удаление статьи");
    }

    @Override
    public List<ArticleDTO> searchArticles(String searchText) {
        String searchTextIgnoreCase = searchText.toLowerCase();

        List<Article> articles = new ArrayList<>();
        articles.addAll(articleRepository.findAllByTitleContainingIgnoreCase(searchTextIgnoreCase));
        articles.addAll(articleRepository.findAllByDescriptionContainingIgnoreCase(searchTextIgnoreCase));
        articles.addAll(articleRepository.findAllByAuthorContainingIgnoreCase(searchTextIgnoreCase));
        articles.addAll(articleRepository.findAllByLabelContainingIgnoreCase(searchTextIgnoreCase));

        articles = articles.stream().distinct().collect(Collectors.toList());

        return getArticleDTOS(articles);
    }

    @NotNull
    private List<ArticleDTO> getArticleDTOS(List<Article> articles) {
        List<ArticleDTO> articleDTOs = new ArrayList<>();
        for (Article article : articles) {
            Optional<Date> dateChangeOptional = Optional.ofNullable(article.getDateChange());
            String dateChange = dateChangeOptional.map(Date::toString).orElse("-");
            ArticleDTO articleDTO = new ArticleDTO(
                    article.getId(),
                    article.getTitle(),
                    article.getDescription(),
                    article.getAuthor(),
                    article.getLabel(),
                    article.getDateCreate().toString(),
                    dateChange
            );
            articleDTOs.add(0, articleDTO);
        }
        return articleDTOs;
    }

}