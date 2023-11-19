package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.util.RestPageImpl;

import java.util.List;

@Service
public class InterviewsService {

    public Page<InterviewDTO> getAll(String token, int page, int size)
            throws JsonProcessingException {
        var text = new RestAuthCall(String
                .format("http://localhost:9912/interviews/?page=%d&?size=%d", page, size))
                .get(token);
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var pageType = mapper.getTypeFactory()
                .constructParametricType(RestPageImpl.class, InterviewDTO.class);
        return mapper.readValue(text, pageType);
    }

    public List<InterviewDTO> getByType(int type) throws JsonProcessingException {
        var text = new RestAuthCall(String.format("http://localhost:9912/interviews/%d", type))
                .get();
        var mapper = new ObjectMapper();
        return mapper.readValue(text, new TypeReference<>() {
        });
    }

    public List<InterviewDTO> getByTopicId(int topicId) throws JsonProcessingException {
        var text = new RestAuthCall(String.format("http://localhost:9912/interviews/findByTopicId/%d", topicId)).get();
        System.out.println("*****This is text from service getByTopicId start***** " + text + " ***** this is text end *****");
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(text, new TypeReference<>() {});
    }

    public Page<InterviewDTO> getByTopicIdWithPagination(int topicId, int page, int size)
            throws JsonProcessingException {
        var text =
                new RestAuthCall(String
                        .format("http://localhost:9912/interviews/findByTopicIdWithPagination/%d?page=%d&?size=%d",
                                topicId, page, size)).get();
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var pageType = mapper.getTypeFactory()
                .constructParametricType(RestPageImpl.class, InterviewDTO.class);
        return mapper.readValue(text, pageType);
    }

    public Page<InterviewDTO> getByTopicsIds(List<Integer> topicIds, int page, int size)
            throws JsonProcessingException {
        var tids = parseIdsListToString(topicIds);
        var mapper = new ObjectMapper();
        var text =
                new RestAuthCall(String
                        .format("http://localhost:9912/interviews/findByTopicsIds/%s?page=%d&?size=%d",
                                tids, page, size)).get();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var pageType = mapper.getTypeFactory()
                .constructParametricType(RestPageImpl.class, InterviewDTO.class);
        return mapper.readValue(text, pageType);
    }

    private String parseIdsListToString(List<Integer> list) {
        var builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i));
            if (i < list.size() - 1) {
                builder.append(',');
            }
        }
        return builder.toString();
    }
}
