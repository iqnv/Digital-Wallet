package org.example.service;

import org.example.Repoistory.UserRepoistory;
import org.example.dto.UserCreatedPayload;
import org.example.dto.UserDto;
import org.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
public class UserService {
     private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepoistory userRepoistory;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    private static String USER_CREATED_TOPIC= "USER_CREATED";

    public Long createUser(UserDto userDto) throws ExecutionException, InterruptedException {
        User user = User.builder().name(userDto.getName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .kycId(userDto.getKycId())
                .addrss(userDto.getAddress())
                .build();
        userRepoistory.save(user);
        UserCreatedPayload userCreatedPayload = new UserCreatedPayload(user.getId(), user.getName(), user.getEmail());
        CompletableFuture<SendResult<String, Object>> g = kafkaTemplate.send(USER_CREATED_TOPIC, String.valueOf(user.getId()), userCreatedPayload);
        return user.getId();
    }
}
