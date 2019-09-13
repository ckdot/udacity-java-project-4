package com.example.demo.controllers;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemController itemController;

    @Test
    public void getItems() {
        Item firstItem = new Item();
        Item secondItem = new Item();

        ArrayList<Item> items = new ArrayList<Item>();
        items.add(firstItem);
        items.add(secondItem);

        given(itemRepository.findAll()).willReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, responseBody.size());
    }

    @Test
    public void getItemById() {
        Item item = new Item();
        long itemId = 123L;

        item.setId(itemId);

        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(itemId);
        Item responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(itemId, responseBody.getId().longValue());
    }

    @Test
    public void getItemsByNameWithNoItemFound() {
        String name = "item name";

        given(itemRepository.findByName(name)).willReturn(new ArrayList<>());

        ResponseEntity<List<Item>> response = itemController.getItemsByName(name);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void getItemsByName() {
        String name = "item name";

        Item firstItem = new Item();
        firstItem.setId(123L);
        firstItem.setName(name);

        Item secondItem = new Item();
        secondItem.setId(456L);
        secondItem.setName(name);

        ArrayList<Item> items = new ArrayList<Item>();
        items.add(firstItem);
        items.add(secondItem);

        given(itemRepository.findByName(name)).willReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(name);
        List<Item> body = Objects.requireNonNull(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, body.size());
        assertEquals(name, body.get(0).getName());
        assertEquals(name, body.get(1).getName());
        assertEquals(123L, body.get(0).getId().longValue());
        assertEquals(456L, body.get(1).getId().longValue());
    }
}
