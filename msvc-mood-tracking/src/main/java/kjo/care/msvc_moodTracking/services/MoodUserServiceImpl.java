package kjo.care.msvc_moodTracking.services;

import kjo.care.msvc_moodTracking.Repositories.MoodUserRepository;
import kjo.care.msvc_moodTracking.services.Impl.MoodUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@Log4j2
public class MoodUserServiceImpl implements MoodUserService {
    private final MoodUserRepository moodUserRepository;
}
