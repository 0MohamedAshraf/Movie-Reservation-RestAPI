package com.example.moviereservation.service;

import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;

import java.util.List;

public interface PricingService {
    double calculatePrice(
            List<ScheduleSeat> seats,
            Schedule schedule
    );
}
