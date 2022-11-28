package com.example.rewardcalculator.controller;

import com.example.rewardcalculator.data.Transaction;
import com.example.rewardcalculator.model.CustomerReward;
import com.example.rewardcalculator.service.RewardCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("api/v1/calculate-reward")
public class RewardWebService {

    private static final Logger log = LoggerFactory.getLogger(RewardWebService.class);

    private final RewardCalculationService rewardCalculationService;

    public RewardWebService(@Autowired final RewardCalculationService rewardCalculationService) {
        this.rewardCalculationService = rewardCalculationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CustomerReward>> getRewardsForCustomers(@RequestBody final List<Transaction> transactions) {
        log.info("Calculating Rewards.");

        Collection<CustomerReward> customerRewards = rewardCalculationService.processRewardCalculation(transactions);
        return new ResponseEntity<>(customerRewards, HttpStatus.OK);

    }

}
