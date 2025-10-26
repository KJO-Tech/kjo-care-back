package com.analytics.client;

import com.analytics.DTOs.ApiResponseDto;
import com.analytics.DTOs.ResourceStatsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "emergency-client", url = "http://kjo-care-back-msvc-emergency:9003")
public interface EmergencyFeignClient {

    @GetMapping("/resources/stats")
    ApiResponseDto<ResourceStatsDto> getEmergencyStats();
}
