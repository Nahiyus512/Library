package com.wkc.library.common;

import lombok.Data;

/**
 * @author xiaoniao
 * @date 2024/5/26 16:01
 * @desc 这个类用于收集分页的数据
 */
@Data
public class QueryPageParam {

    private static int PAGE_SIZE=5;
    private static int PAGE_NUM=1;

    private int pageSize = PAGE_SIZE;
    private int pageNum = PAGE_NUM;
    private String nameInfo;

}
