<<<<<<< HEAD
package com.shopsmart.shopsmart_server.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shopsmart.shopsmart_server.model.CartItem;

@Repository
public interface CartRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findByUserId(String userId);
    void deleteByUserIdAndProductId(String userId, String productId);
=======
package com.shopsmart.shopsmart_server.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shopsmart.shopsmart_server.model.CartItem;

@Repository
public interface CartRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findByUserId(String userId);
    void deleteByUserIdAndProductId(String userId, Long productId);
>>>>>>> f05afc47162a15a3034e3b01c500459bdef193e5
}