package com.wkc.library;

import com.wkc.library.entity.Book;
import com.wkc.library.entity.BookScore;
import com.wkc.library.mapper.BookMapper;
import com.wkc.library.mapper.BookScoreMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiaoniao
 * @date 2023/2/28 15:21
 */
@SpringBootTest
public class testBookCase {

    @Autowired
    BookMapper bookMapper;


    @Autowired
    BookScoreMapper bookScoreMapper;

    @Test
    public void testGetByName(){
        String name = "消失的影子";
        //Book book = bookMapper.getByName(name);
        //System.out.println(book);
    }

    @Test
    public void testInsertBook(){
        Book book = new Book();
       // book.setBookId(null);
        book.setBookName("明天你好");
        //book.setBookPrice(23.2);
        book.setBookPublic("小鸟出版社");
        book.setBookAuthor("小鸟");
        //book.setBookClassify("自然");
        Integer result = bookMapper.insertBook(book);
        System.out.println(result);
    }

    @Test
    public void testScore(){
        BookScore bookScore = new BookScore();
        bookScore.setUserId("2");
        bookScore.setBookId("2");
        bookScore.setScore(2);
        Integer integer = bookScoreMapper.insetScore(bookScore);
        System.out.println(integer);
    }

    @Test
    public void testDel(){

    }
}
