package com.example.moviereservation.service;

import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingServiceImpl implements PricingService {

    @Override
    public double calculatePrice(List<ScheduleSeat> seats, Schedule schedule) {

        double totalPrice = 0f;


        for (ScheduleSeat seat : seats){
            switch (seat.getSeat().getSeatClass()){
                case STANDARD:
                    totalPrice += schedule.getPrice();
                    break;
                case PREMIUM:
                    totalPrice += (schedule.getPrice() * 1.3);
                    break;
                case VIP:
                    totalPrice += (schedule.getPrice() * 1.6);
                    break;
            }

        }

        return totalPrice;
    }
}
