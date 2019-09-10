package com.cgy.seckill.service;

import com.cgy.seckill.dao.UserDao;
import com.cgy.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public User getById(int id) {
        return userDao.getById(id);
    }

    @Transactional
    public boolean trans() {
        User user1 = new User();
        user1.setId(2);
        user1.setName("u2");
        userDao.insert(user1);
        User user2 = new User();
        user2.setId(1);
        user2.setName("u1");
        userDao.insert(user2);
        return true;
    }
}
