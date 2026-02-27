package com.wkc.library.entity.resultInfo;

import com.wkc.library.entity.Book;
import lombok.Data;

import java.util.List;

/**
 * @author Nah
 * @date 2025/12/27 20:24
 */
@Data
public class BookPageInfo {
    List<Book> bookList;
    int count;
}
