package kjo.care.msvc_moodTracking.services;

import kjo.care.msvc_moodTracking.Repositories.MoodRepository;
import kjo.care.msvc_moodTracking.services.Impl.MoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Log4j2
@Validated
public class MoodServiceImpl implements MoodService {
    private final MoodRepository moodRepository;
}
