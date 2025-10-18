package kjo.care.msvc_notification.mappers;

import kjo.care.msvc_notification.dto.NotificationResponseDto;
import kjo.care.msvc_notification.entities.Notification;
import kjo.care.msvc_notification.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final ModelMapper modelMapper;

    public NotificationResponseDto toDto(Notification notification) {
        return modelMapper.map(notification, NotificationResponseDto.class);
    }

    public List<NotificationResponseDto> toDtoList(List<Notification> notifications) {
        return notifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
