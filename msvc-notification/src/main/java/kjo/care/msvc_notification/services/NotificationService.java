package kjo.care.msvc_notification.services;

import kjo.care.msvc_notification.dto.*;

import java.util.List;

public interface NotificationService {
    void createLikeNotification(ReactionEventDto event);
    void createCommentNotification(CommentEventDto event);
    void createCommentReplyNotification(CommentEventDto event);
    void createNewBlogNotification(NewBlogEventDto event);
    void createBlogRejectedNotification(BlogRejectedEventDto event);
    List<NotificationResponseDto> getNotificationsForUser(String userId);
}
