package com.turtlesoup.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

@TableName("puzzle")
public class Puzzle {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String answer;

    private Integer usedCount;

    private String difficulty;

    private String author;

    private Long uploaderId;

    private String status;

    private String reviewReason;

    private String category;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public Puzzle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Long getUploaderId() { return uploaderId; }
    public void setUploaderId(Long uploaderId) { this.uploaderId = uploaderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReviewReason() { return reviewReason; }
    public void setReviewReason(String reviewReason) { this.reviewReason = reviewReason; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
