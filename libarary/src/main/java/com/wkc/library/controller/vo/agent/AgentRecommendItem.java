package com.wkc.library.controller.vo.agent;

import com.wkc.library.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentRecommendItem {
    private Book book;
    private Double score;
    private String reason;
    private List<String> hitStrategies;
}
