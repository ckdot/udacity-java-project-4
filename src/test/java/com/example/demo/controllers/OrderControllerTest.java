package com.example.demo.controllers;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderController orderController;

    @Test
    public void submitWithUserNotFound() {
        String username = "some_username";
        given(userRepository.findByUsername(username)).willReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(username);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void submit() {
        String username = "another_username";

        User user = new User();
        user.setUsername(username);

        Item item = new Item();
        item.setId(321L);
        item.setPrice(BigDecimal.valueOf(321));

        Cart cart = new Cart();
        cart.addItem(item);
        cart.addItem(item);
        cart.setTotal(BigDecimal.valueOf(642));
        user.setCart(cart);

        given(userRepository.findByUsername(username)).willReturn(user);


        ResponseEntity<UserOrder> response = orderController.submit(username);
        UserOrder body = Objects.requireNonNull(response.getBody());

        verify(orderRepository, times(1)).save(any());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(BigDecimal.valueOf(642), body.getTotal());
        assertEquals(2, body.getItems().size());
        assertEquals(321L, body.getItems().get(0).getId().longValue());
    }

    @Test
    public void historyWithUserNotFound() {
        String username = "some_username";
        given(userRepository.findByUsername(username)).willReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void history() {
        String username = "another_username";
        UserOrder order = new UserOrder();
        order.setId(123L);

        User user = new User();
        user.setUsername(username);

        ArrayList<UserOrder> orders = new ArrayList<>();
        orders.add(order);

        given(userRepository.findByUsername(username)).willReturn(user);
        given(orderRepository.findByUser(user)).willReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);
        List<UserOrder> responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, responseBody.size());
        assertEquals(123L, responseBody.get(0).getId().longValue());
    }
}
