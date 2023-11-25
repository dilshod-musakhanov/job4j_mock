package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.site.dto.SubscribeCategory;
import ru.job4j.site.dto.SubscribeTopicDTO;
import ru.job4j.site.dto.UserDTO;
import ru.job4j.site.dto.UserTopicDTO;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final RestTemplate restTemplate;

    public void addSubscribeCategory(String token, int userId, int categoryId) throws JsonProcessingException {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall("http://localhost:9920/subscribeCategory/add", restTemplate).post(
                token, mapper.writeValueAsString(subscribeCategory));
    }

    public void deleteSubscribeCategory(String token, int userId, int categoryId) throws JsonProcessingException {
        SubscribeCategory subscribeCategory = new SubscribeCategory(userId, categoryId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall("http://localhost:9920/subscribeCategory/delete", restTemplate).post(
                token, mapper.writeValueAsString(subscribeCategory));
    }

    public UserDTO findCategoriesByUserId(int id) throws JsonProcessingException {
        var text = new RestAuthCall("http://localhost:9920/subscribeCategory/" + id, restTemplate).get();
        var mapper = new ObjectMapper();
        List<Integer> list = mapper.readValue(text, new TypeReference<>() {
        });
        return new UserDTO(id, list);
    }

    public void addSubscribeTopic(String token, int userId, int topicId) throws JsonProcessingException {
        SubscribeTopicDTO subscribeTopicDTO = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall("http://localhost:9920/subscribeTopic/add", restTemplate).post(
                token, mapper.writeValueAsString(subscribeTopicDTO));
    }

    public void deleteSubscribeTopic(String token, int userId, int topicId) throws JsonProcessingException {
        SubscribeTopicDTO subscribeTopic = new SubscribeTopicDTO(userId, topicId);
        var mapper = new ObjectMapper();
        var out = new RestAuthCall("http://localhost:9920/subscribeTopic/delete", restTemplate).post(
                token, mapper.writeValueAsString(subscribeTopic));
    }

    public UserTopicDTO findTopicByUserId(int id) throws JsonProcessingException {
        var text = new RestAuthCall("http://localhost:9920/subscribeTopic/" + id, restTemplate).get();
        var mapper = new ObjectMapper();
        List<Integer> list = mapper.readValue(text, new TypeReference<>() {
        });
        return new UserTopicDTO(id, list);
    }
}