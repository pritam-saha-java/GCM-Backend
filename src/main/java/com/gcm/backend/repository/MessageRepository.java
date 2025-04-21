package com.gcm.backend.repository;

import com.gcm.backend.entity.MessageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<MessageEntity, Long> {
    List<MessageEntity> findByUserName(String userName);
}
