package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private CartController cartController;

    @Test
    public void addToCartWithUserNotFound() {
        String username = "some_username";
        given(userRepository.findByUsername(username)).willReturn(null);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(123);
        cartRequest.setUsername(username);
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void addToCartWithItemNotFound() {
        String username = "another_username";
        long itemId = 123L;
        given(userRepository.findByUsername(username)).willReturn(new User());
        given(itemRepository.findById(itemId)).willReturn(Optional.empty());

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(itemId);
        cartRequest.setUsername(username);
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void addToCart() {
        String username = "just_another_username";
        long itemId = 456L;
        BigDecimal itemPrice = BigDecimal.valueOf(12.34);
        String itemName = "some item";
        String itemDescription = "some description";

        User user = new User();
        user.setUsername(username);

        Item item = new Item();
        item.setName(itemName);
        item.setId(itemId);
        item.setPrice(itemPrice);
        item.setDescription(itemDescription);

        Cart cart = new Cart();

        user.setCart(cart);

        given(userRepository.findByUsername(username)).willReturn(user);
        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(itemId);
        cartRequest.setUsername(username);
        cartRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        Cart responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, responseBody.getItems().size());

        assertEquals(itemName, responseBody.getItems().get(0).getName());
        assertEquals(itemName, responseBody.getItems().get(1).getName());

        assertEquals(itemDescription, responseBody.getItems().get(0).getDescription());
        assertEquals(itemDescription, responseBody.getItems().get(1).getDescription());

        assertEquals(itemPrice, responseBody.getItems().get(0).getPrice());
        assertEquals(itemPrice, responseBody.getItems().get(1).getPrice());

        assertEquals(BigDecimal.valueOf(24.68), responseBody.getTotal());
    }

    @Test
    public void removeFromCartWithUserNotFound() {
        String username = "some_username";
        given(userRepository.findByUsername(username)).willReturn(null);

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(123);
        cartRequest.setUsername(username);
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void removeFromCartWithItemNotFound() {
        String username = "another_username";
        long itemId = 123L;
        given(userRepository.findByUsername(username)).willReturn(new User());
        given(itemRepository.findById(itemId)).willReturn(Optional.empty());

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(itemId);
        cartRequest.setUsername(username);
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void removeFromCart() {
        String username = "just_another_username";
        long itemId = 456L;
        BigDecimal itemPrice = BigDecimal.valueOf(12.34);
        String itemName = "some item";
        String itemDescription = "some description";

        User user = new User();
        user.setUsername(username);

        Item item = new Item();
        item.setName(itemName);
        item.setId(itemId);
        item.setPrice(itemPrice);
        item.setDescription(itemDescription);

        Cart cart = new Cart();
        cart.addItem(item);
        cart.addItem(item);
        cart.setTotal(BigDecimal.valueOf(24.68));

        user.setCart(cart);

        given(userRepository.findByUsername(username)).willReturn(user);
        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(itemId);
        cartRequest.setUsername(username);
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);
        Cart responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, responseBody.getItems().size());

        assertEquals(itemName, responseBody.getItems().get(0).getName());

        assertEquals(itemDescription, responseBody.getItems().get(0).getDescription());

        assertEquals(itemPrice, responseBody.getItems().get(0).getPrice());

        assertEquals(BigDecimal.valueOf(12.34), responseBody.getTotal());
    }
}
