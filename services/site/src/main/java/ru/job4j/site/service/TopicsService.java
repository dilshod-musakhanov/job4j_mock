package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.TopicDTO;
import ru.job4j.site.dto.TopicLiteDTO;
import ru.job4j.site.dto.TopicIdNameDTO;

import java.util.Calendar;
import java.util.List;

@Service
@AllArgsConstructor
public class TopicsService {

    private final RestTemplate restTemplate;

    public List<TopicDTO> getByCategory(int id) throws JsonProcessingException {
        var text = new RestAuthCall("http://localhost:9902/topics/" + id, restTemplate).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public TopicDTO getById(int id) throws JsonProcessingException {
        var text = new RestAuthCall("http://localhost:9902/topic/" + id, restTemplate).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public TopicDTO create(String token, TopicLiteDTO topicLite) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var topic = new TopicDTO();
        topic.setName(topicLite.getName());
        topic.setPosition(topicLite.getPosition());
        topic.setText(topicLite.getText());
        var category = new CategoryDTO();
        category.setId(topicLite.getCategoryId());
        topic.setCategory(category);
        var out = new RestAuthCall("http://localhost:9902/topic/", restTemplate).post(
                token,
                mapper.writeValueAsString(topic)
        );
        return mapper.readValue(out, TopicDTO.class);
    }

    public void update(String token, TopicDTO topic) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        topic.setUpdated(Calendar.getInstance());
        var json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(topic);
        new RestAuthCall("http://localhost:9902/topic/", restTemplate).update(
                token,
                json
        );
    }

    public void delete(String token, int id) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var topic = new TopicDTO();
        topic.setId(id);
        new RestAuthCall("http://localhost:9902/topic/", restTemplate).delete(
                token,
                mapper.writeValueAsString(topic)
        );
    }

    public String getNameById(int id) {
        return new RestAuthCall(String.format("http://localhost:9902/topic/name/%d", id), restTemplate).get();
    }

    public List<TopicIdNameDTO> getTopicIdNameDtoByCategory(int categoryId)
            throws JsonProcessingException {
        var text = new
                RestAuthCall(String.format("http://localhost:9902/topics/getByCategoryId/%d",
                categoryId), restTemplate).get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }
}
