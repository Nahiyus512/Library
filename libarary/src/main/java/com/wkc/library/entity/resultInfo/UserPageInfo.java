package com.wkc.library.entity.resultInfo;

import com.wkc.library.entity.User;
import lombok.Data;

import java.util.List;

/**
 * @author Nah
 * @date 2025/12/2 13:20
 */
@Data
public class UserPageInfo {

    List<User> userList;
    int count;
}
