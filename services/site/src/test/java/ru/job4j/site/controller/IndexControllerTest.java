package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.site.SiteSrv;
import ru.job4j.site.domain.Breadcrumb;
import ru.job4j.site.dto.*;
import ru.job4j.site.service.*;
import ru.job4j.site.util.CategoryInterviewDtoMapper;
import ru.job4j.site.util.InterviewProfileDtoMapper;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 * CheckDev пробное собеседование
 * IndexControllerTest тесты на контроллер IndexController
 *
 * @author Dmitry Stepanov
 * @version 24.09.2023 21:50
 */
@SpringBootTest(classes = SiteSrv.class)
@AutoConfigureMockMvc
class IndexControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoriesService categoriesService;
    @MockBean
    private TopicsService topicsService;
    @MockBean
    private InterviewsService interviewsService;
    @MockBean
    private AuthService authService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private InterviewProfileDtoMapper dtoMapperInterview;
    @MockBean
    private CategoryInterviewDtoMapper dtoMapperCategory;

    private IndexController indexController;

    @BeforeEach
    void initTest() {
        this.indexController = new IndexController(
                categoriesService, interviewsService, topicsService, authService,
                notificationService, dtoMapperInterview, dtoMapperCategory
        );
    }

    @Test
    void whenGetIndexPageThenReturnIndex() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void whenGetIndexPageExpectModelAttributeThenOk() throws JsonProcessingException {
        var topicDTO1 = new TopicDTO();
        topicDTO1.setId(1);
        topicDTO1.setName("topic1");
        var topicDTO2 = new TopicDTO();
        topicDTO2.setId(2);
        topicDTO2.setName("topic2");
        var topicNameDTO1 = new TopicIdNameDTO();
        topicNameDTO1.setId(1);
        topicNameDTO1.setName("topic1");
        var topicNameDTO2 = new TopicIdNameDTO();
        topicNameDTO2.setId(2);
        topicNameDTO2.setName("topic2");
        var listTopicName1 = List.of(topicNameDTO1);
        var listTopicName2 = List.of(topicNameDTO2);
        var cat1 = new CategoryDTO(1, "name1", 1, 1, 1);
        var cat2 = new CategoryDTO(2, "name2", 1, 1, 1);
        var listCat = List.of(cat1, cat2);
        var catInterview1 = new CategoryNewInterviewDTO(1, "name1", 1, 1, 1, 1);
        var catInterview2 = new CategoryNewInterviewDTO(2, "name2", 1, 1, 1, 1);
        var listCatInterview = List.of(catInterview1, catInterview2);
        var firstInterview = new InterviewDTO(1, 1, 1, 1,
                "interview1", "description1", "contact1",
                "30.02.2024", "09.10.2023", 1);
        var secondInterview = new InterviewDTO(2, 1, 1, 2,
                "interview2", "description2", "contact2",
                "30.02.2024", "09.10.2023", 2);
        var listInterviews = List.of(firstInterview, secondInterview);
        var firstInterviewProfile = new InterviewProfileDTO(1, 1, 1, 1, "username",
                "interview1", "description1", "contact1",
                "30.02.2024", "09.10.2023", 1);
        var secondInterviewProfile = new InterviewProfileDTO(2, 1, 1, 2, "username2",
                "interview2", "description2", "contact2",
                "30.02.2024", "09.10.2023", 2);
        var listInterviewsProfile = List.of(firstInterviewProfile, secondInterviewProfile);
        when(topicsService.getByCategory(cat1.getId())).thenReturn(List.of(topicDTO1));
        when(topicsService.getByCategory(cat2.getId())).thenReturn(List.of(topicDTO2));
        when(categoriesService.getMostPopular()).thenReturn(listCat);
        when(dtoMapperCategory.toDto(listCat)).thenReturn(listCatInterview);
        when(interviewsService.getByType(1)).thenReturn(listInterviews);
        when(dtoMapperInterview.toDto(listInterviews)).thenReturn(listInterviewsProfile);
        when(topicsService.getByCategory(listCat.get(0).getId())).thenReturn(List.of(topicDTO1));
        when(topicsService.getByCategory(listCat.get(1).getId())).thenReturn(List.of(topicDTO2));
        when(topicsService.getTopicIdNameDtoByCategory(listCat.get(0).getId())).thenReturn(listTopicName1);
        when(topicsService.getTopicIdNameDtoByCategory(listCat.get(1).getId())).thenReturn(listTopicName2);
        when(interviewsService.getByTopicId(topicNameDTO1.getId())).thenReturn(List.of(firstInterview));
        when(interviewsService.getByTopicId(topicNameDTO2.getId())).thenReturn(List.of(secondInterview));
        var listBread = List.of(new Breadcrumb("Главная", "/"));
        var model = new ConcurrentModel();
        var view = indexController.getIndexPage(model, null);
        var actualCategories = model.getAttribute("categories");
        var actualBreadCrumbs = model.getAttribute("breadcrumbs");
        var actualUserInfo = model.getAttribute("userInfo");
        var actualInterviews = model.getAttribute("new_interviews");
        assertThat(view).isEqualTo("index");
        assertThat(actualCategories).usingRecursiveComparison().isEqualTo(listCatInterview);
        assertThat(actualBreadCrumbs).usingRecursiveComparison().isEqualTo(listBread);
        assertThat(actualUserInfo).isNull();
        assertThat(actualInterviews).usingRecursiveComparison().isEqualTo(listInterviewsProfile);
    }
}