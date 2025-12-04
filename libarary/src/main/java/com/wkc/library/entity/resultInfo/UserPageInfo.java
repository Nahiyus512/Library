package com.wkc.library.entity.resultInfo;

import com.wkc.library.entity.User;
import lombok.Data;

import java.util.List;

/**
 * @author xiaoniao
 * @date 2024/6/2 13:20
 */
@Data
public class UserPageInfo {

    List<User> userList;
    int count;
}
