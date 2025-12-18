package com.wkc.library.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.entity.BookLike;
import com.wkc.library.entity.User;
import com.wkc.library.mapper.BookLikeMapper;
import com.wkc.library.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 关联规则挖掘 (Association Rule Mining)
 * 核心思想：挖掘"啤酒与尿布"式的关联。如果用户喜欢了书A，大概率也会喜欢书B。
 * 基于用户的喜爱记录 (BookLike) 进行分析。
 */
@Component
public class AssociationRuleRecommend {

    @Autowired
    private BookLikeMapper bookLikeMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 推荐
     * @param userId 用户ID
     * @param topN 推荐数量
     * @return 推荐的图书ID列表
     */
    public List<Integer> recommend(Integer userId, int topN) {
        // 1. 获取目标用户的喜爱记录
        // User user = userMapper.selectById(userId);
        // if (user == null) return Collections.emptyList();

        LambdaQueryWrapper<BookLike> myLikeWrapper = new LambdaQueryWrapper<>();
        myLikeWrapper.eq(BookLike::getUserId, userId)
                     .ge(BookLike::getLikeLevel, 1); // 只统计还行和想看
        List<BookLike> myLikes = bookLikeMapper.selectList(myLikeWrapper);
        
        Set<Integer> myBookIds = myLikes.stream()
                .map(BookLike::getBookId)
                .collect(Collectors.toSet());

        if (myBookIds.isEmpty()) return Collections.emptyList();

        // 2. 获取所有喜爱记录并按用户分组 (构建事务)
        LambdaQueryWrapper<BookLike> allWrapper = new LambdaQueryWrapper<>();
        allWrapper.ge(BookLike::getLikeLevel, 1);
        List<BookLike> allLikes = bookLikeMapper.selectList(allWrapper);
        Map<Integer, Set<Integer>> userTransactions = new HashMap<>();
        
        for (BookLike like : allLikes) {
            userTransactions.computeIfAbsent(like.getUserId(), k -> new HashSet<>())
                    .add(like.getBookId());
        }

        // 3. 统计关联规则：(Book A -> Book B) 的共现次数
        Map<Integer, Map<Integer, Integer>> coOccurrence = new HashMap<>();

        for (Set<Integer> transaction : userTransactions.values()) {
            List<Integer> books = new ArrayList<>(transaction);
            for (int i = 0; i < books.size(); i++) {
                for (int j = 0; j < books.size(); j++) {
                    if (i == j) continue;
                    int bookA = books.get(i);
                    int bookB = books.get(j);
                    
                    coOccurrence.computeIfAbsent(bookA, k -> new HashMap<>())
                            .merge(bookB, 1, Integer::sum);
                }
            }
        }

        // 4. 基于用户当前已借阅的书，推荐关联度最高的书
        Map<Integer, Integer> recommendScores = new HashMap<>();

        for (Integer myBookId : myBookIds) {
            Map<Integer, Integer> rules = coOccurrence.getOrDefault(myBookId, Collections.emptyMap());
            
            for (Map.Entry<Integer, Integer> entry : rules.entrySet()) {
                Integer targetBookId = entry.getKey();
                Integer count = entry.getValue();

                if (myBookIds.contains(targetBookId)) continue; // 过滤已借

                recommendScores.merge(targetBookId, count, Integer::sum);
            }
        }

        // 5. 排序返回
        return recommendScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
