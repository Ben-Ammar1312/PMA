package com.example.backend.controller;

import com.example.backend.model.Couple;
import com.example.backend.model.FertilityRecord;
import com.example.backend.model.Partner;
import com.example.backend.model.PersonalInfo;
import com.example.backend.service.FertilityRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin")
public class DoctorController {
    final FertilityRecordService fertilityRecordService;

    public DoctorController(FertilityRecordService fertilityRecordService) {
        this.fertilityRecordService = fertilityRecordService;
    }

    @GetMapping("/hello")
    public String hello(Principal p) {
        FertilityRecord fertilityRecord = new FertilityRecord();
        fertilityRecord.setCouple(new Couple(new Partner(new PersonalInfo("x","x",LocalDate.of(2010,9,9),"x","x","x@x.com","12121")),new Partner(new PersonalInfo("y","y", LocalDate.of(2010,9,9),"x","x","x@x.com","12121"))));
        fertilityRecordService.addFertilityRecord(fertilityRecord);
        return "sa7it";
    };
}
