package com.wkc.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wkc.library.entity.Book;

import java.util.List;


/**
 * @author xiaoniao
 * @date 2023/2/17 13:32
 */
public interface BookService extends IService<Book> {
    //添加书本
     int add(Book book);

     //删除书本
    boolean deleteById(Integer id);

    //根据id修改书本
    boolean updateByBookId(Book book);

    //根据书本名模糊查询书本
    List<Book> searchBookByLike(String name);

    List<Book> searchBookByCategoryName(String categoryName);

}
