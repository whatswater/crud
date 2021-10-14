package com.whatswater.curd.project.user;


import io.vertx.core.Future;

public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Future<User> getById(long userId) {
        return userRepository.getById(userId);
    }

    public Future<Long> insert(User user) {
        return userRepository.insert(user);
    }

}
