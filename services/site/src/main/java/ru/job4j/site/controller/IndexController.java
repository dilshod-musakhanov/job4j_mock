package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.domain.StatusInterview;
import ru.job4j.site.dto.*;
import ru.job4j.site.service.*;
import ru.job4j.site.util.CategoryInterviewDtoMapper;
import ru.job4j.site.util.InterviewProfileDtoMapper;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;
    private final TopicsService topicsService;
    private final AuthService authService;
    private final NotificationService notifications;
    private final InterviewProfileDtoMapper dtoMapperInterview;
    private final CategoryInterviewDtoMapper dtoMapperCategory;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model, HttpServletRequest req) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        try {
            List<CategoryDTO> categoryDTOList  = categoriesService.getMostPopular();
            List<CategoryNewInterviewDTO> categoryNewInterviewDTOS = dtoMapperCategory.toDto(categoryDTOList);
            List<CategoryNewInterviewDTO> categories = new ArrayList<>();
            for (CategoryNewInterviewDTO category : categoryNewInterviewDTOS) {
                category.setTopicsSize(topicsService.getByCategory(category.getId()).size());
                List<TopicIdNameDTO> topicIdNameDTOS = topicsService.getTopicIdNameDtoByCategory(category.getId());
                int totalCount = 0;
                for (TopicIdNameDTO topic : topicIdNameDTOS) {
                    List<InterviewDTO> interview = interviewsService.getByTopicId(topic.getId());
                    int count = (int) interview.stream().filter(i -> i.getStatus() == StatusInterview.IS_NEW.getId()).count();
                    totalCount += count;
                }
                category.setNewInterviewCount(totalCount);
                categories.add(category);
            }
            model.addAttribute("categories", categories);
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO", notifications.findCategoriesByUserId(userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        List<InterviewDTO> interviewDTOS = interviewsService.getByType(1);
        List<InterviewProfileDTO> interviewProfileDTOS = dtoMapperInterview.toDto(interviewDTOS);
        model.addAttribute("new_interviews", interviewProfileDTOS);
        return "index";
    }
}