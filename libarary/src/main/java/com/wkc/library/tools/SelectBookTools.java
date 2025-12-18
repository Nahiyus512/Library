package com.wkc.library.tools;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wkc.library.entity.Book;
import com.wkc.library.entity.User;
import com.wkc.library.service.BookScoreService;
import com.wkc.library.service.BookService;
import com.wkc.library.service.UserService;
import com.wkc.library.util.UserContext;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author rong
 * @Description
 * @date 2025/12/18 13:38
 */
@Slf4j
@Component
public class SelectBookTools {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookScoreService bookScoreService;

    @Autowired
    private UserService userService;

    @Tool(
            name = "根据图书名称模糊匹配图书",
            value = "当用户输入图书名称、书名关键词或描述想找某一本或某一类书籍时调用该工具。" +
                    "工具会根据关键词在图书名称中进行模糊匹配，返回符合条件的图书列表。"
    )
    public List<Book> searchBooksByName(String keyword) {
        log.info("调用根据图书名称模糊匹配图书工具");
        return bookService.searchBookByLike(keyword);
    }

    @Tool(
            name = "根据分类名称查询图书",
            value = "当用户明确说明想查看某一分类下的图书时调用该工具，例如计算机、文学、历史等。" +
                    "工具会根据分类名称查询并返回该分类下的图书列表。"
    )
    public List<Book> getBooksByCategory(String categoryName) {
        log.info("调用根据分类名称查询图书工具");
        return bookService.searchBookByCategoryName(categoryName);
    }

    @Tool(
            name = "获取全部图书列表",
            value = "当用户请求图书推荐、猜你喜欢或阅读建议时调用该工具。" +
                    "该工具会返回系统中所有可推荐的图书信息。" +
                    "你必须严格基于工具返回的图书数据进行筛选和推荐，推荐图书不得超过5本" +
                    "不得编造不存在的书籍或作者信息。" +
                    "推荐结果应从返回的图书列表中选择，并说明推荐理由。"
    )
    public List<Book> recommend() {
        log.info("调用获取全部图书列表工具");
        return bookService.list();
    }

}

