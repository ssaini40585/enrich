package com.enrichplus.allocation.client;

import com.enrichplus.common.entity.TalentDemand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class DemandClient {
    private final RestTemplate restTemplate;
    private final String demandServiceUrl;

    public DemandClient(RestTemplate restTemplate,
                        @Value("${clients.demand-service-url}") String demandServiceUrl) {
        this.restTemplate = restTemplate;
        this.demandServiceUrl = demandServiceUrl;
    }

    public List<TalentDemand> getOpenDemands() {
        ResponseEntity<List<TalentDemand>> response = restTemplate.exchange(
                demandServiceUrl + "/api/demands",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    public void incrementFulfilledCount(Long demandId) {
        restTemplate.patchForObject(demandServiceUrl + "/api/demands/" + demandId + "/fulfill", null, TalentDemand.class);
    }
}
