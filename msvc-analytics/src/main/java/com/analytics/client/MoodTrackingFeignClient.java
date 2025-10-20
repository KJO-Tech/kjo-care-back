package com.analytics.client;

import com.analytics.DTOs.ApiResponseDto;
import com.analytics.DTOs.MoodRegisterDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "msvc-mood-tracking",
        url = "http://kjo-care-back-msvc-mood-tracking:9002/mood-registers"
)
public interface MoodTrackingFeignClient {

    @GetMapping("/user/{userId}")
    ApiResponseDto<List<MoodRegisterDto>> getMoodRegistersByUser(@PathVariable("userId") String userId);
}
