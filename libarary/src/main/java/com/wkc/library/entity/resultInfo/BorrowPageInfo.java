package com.wkc.library.entity.resultInfo;

import com.wkc.library.entity.BookBorrow;
import lombok.Data;

import java.util.List;

/**
 * @author xiaoniao
 * @date 2024/6/20 16:47
 */
@Data
public class BorrowPageInfo {

    private List<BookBorrow> borrows;
    private Integer count;
}
