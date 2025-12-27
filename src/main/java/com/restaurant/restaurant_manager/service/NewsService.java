package com.restaurant.restaurant_manager.service;

import com.restaurant.restaurant_manager.dto.category.CategoryResponse;
import com.restaurant.restaurant_manager.dto.news.NewsDTOs;
import com.restaurant.restaurant_manager.dto.news.NewsDTOs.*;
import com.restaurant.restaurant_manager.dto.response.PageResponse;
import com.restaurant.restaurant_manager.entity.Category;
import com.restaurant.restaurant_manager.entity.News;
import com.restaurant.restaurant_manager.entity.User;
import com.restaurant.restaurant_manager.exception.ResourceNotFoundException;
import com.restaurant.restaurant_manager.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    // --- CREATE (Chỉ Staff/Admin) ---
    @Transactional
    public NewsResponse createNews(User author, CreateNewsRequest request) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setImageUrl(request.getImageUrl());
        news.setAuthor(author);

        return NewsResponse.fromEntity(newsRepository.save(news));
    }

    public PageResponse<NewsResponse> getAllNews(int page, int size) {
        // News thường sắp xếp theo ngày tạo mới nhất (createdAt DESC)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<News> newsPage = newsRepository.findAll(pageable);

        List<NewsResponse> content = newsPage.getContent().stream()
                .map(NewsResponse::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.<NewsResponse>builder()
                .content(content)
                .pageNo(newsPage.getNumber())
                .pageSize(newsPage.getSize())
                .totalElements(newsPage.getTotalElements())
                .totalPages(newsPage.getTotalPages())
                .last(newsPage.isLast())
                .build();
    }

    // --- READ ONE (Public) ---
    public NewsResponse getNewsById(UUID id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));
        return NewsResponse.fromEntity(news);
    }

    // --- UPDATE (Chỉ Staff/Admin) ---
    @Transactional
    public NewsResponse updateNews(UUID id, UpdateNewsRequest request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));

        if (request.getTitle() != null) news.setTitle(request.getTitle());
        if (request.getContent() != null) news.setContent(request.getContent());
        if (request.getImageUrl() != null) news.setImageUrl(request.getImageUrl());

        return NewsResponse.fromEntity(newsRepository.save(news));
    }

    // --- DELETE (Chỉ Admin) ---
    @Transactional
    public void deleteNews(UUID id) {
        if (!newsRepository.existsById(id)) {
            throw new ResourceNotFoundException("News not found");
        }
        newsRepository.deleteById(id);
    }
}