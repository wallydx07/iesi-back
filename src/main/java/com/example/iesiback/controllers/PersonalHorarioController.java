package com.example.iesiback.controllers;

import com.example.iesiback.dto.PersonalHorarioDTO;
import com.example.iesiback.services.PersonalHorariosService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal-horarios")
@CrossOrigin(origins = "*")
public class PersonalHorarioController {
    private final PersonalHorariosService personalHorariosService;

    public PersonalHorarioController(PersonalHorariosService personalHorariosService) {
        this.personalHorariosService = personalHorariosService;
    }


    @GetMapping("/{year}")
    public List<PersonalHorarioDTO> getPersonalHorariosPorAnio(@PathVariable Integer year) {
        return personalHorariosService.getPersonalHorarios(year);
    }

}
