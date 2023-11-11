package ru.job4j.site.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.dto.InterviewProfileDTO;
import ru.job4j.site.service.ProfilesService;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class InterviewProfileDtoMapper {

    private final ProfilesService profilesService;

    public List<InterviewProfileDTO> toDto(List<InterviewDTO> interviewDTOList) {
        List<InterviewProfileDTO> list = new ArrayList<>();
        for (InterviewDTO dto : interviewDTOList) {
            InterviewProfileDTO interviewProfileDto = new InterviewProfileDTO();
            interviewProfileDto.setId(dto.getId());
            interviewProfileDto.setMode(dto.getMode());
            interviewProfileDto.setStatus(dto.getStatus());
            interviewProfileDto.setSubmitterId(dto.getSubmitterId());
            interviewProfileDto.setProfileUsername(profilesService.getProfileById(dto.getSubmitterId()).get().getUsername());
            interviewProfileDto.setTitle(dto.getTitle());
            interviewProfileDto.setAdditional(dto.getAdditional());
            interviewProfileDto.setContactBy(dto.getContactBy());
            interviewProfileDto.setApproximateDate(dto.getApproximateDate());
            interviewProfileDto.setCreateDate(dto.getCreateDate());
            interviewProfileDto.setTopicId(dto.getTopicId());
            list.add(interviewProfileDto);
        }
        return list;
    }
}
