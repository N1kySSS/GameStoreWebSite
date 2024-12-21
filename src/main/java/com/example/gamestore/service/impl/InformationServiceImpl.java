package com.example.gamestore.service.impl;

import com.example.gamestore.dto.InformationDTO;
import com.example.gamestore.entity.Information;
import com.example.gamestore.repository.InformationRepository;
import com.example.gamestore.service.InformationService;
import com.example.gamestore.utils.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InformationServiceImpl implements InformationService {
    private final InformationRepository informationRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public InformationServiceImpl(InformationRepository informationRepository, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.informationRepository = informationRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public InformationDTO addInformation(InformationDTO informationDTO) {
        if (!this.validationUtil.isValid(informationDTO)) {
            this.validationUtil
                    .violations(informationDTO)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);

            throw new RuntimeException("Информация не валидна");
        }

        if (informationDTO == null) {
            throw new RuntimeException("Информация отсутствует");
        }
        Information information = modelMapper.map(informationDTO, Information.class);
        Information savedInformation = informationRepository.save(information);

        return modelMapper.map(savedInformation, InformationDTO.class);
    }

    @Override
    public List<InformationDTO> getAll() {
        List<Information> informationList = (List<Information>) informationRepository.findAll();
        List<InformationDTO> dtoInformationList = new ArrayList<>();
        informationList.forEach(it -> dtoInformationList.add(modelMapper.map(it, InformationDTO.class)));

        if (dtoInformationList.isEmpty()) {
            throw new RuntimeException("Информация не найдена");
        } else {
            return dtoInformationList;
        }
    }

    @Override
    public InformationDTO getById(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        Optional<Information> information = informationRepository.findById(id);

        if (information.isPresent()) {
            return modelMapper.map(information, InformationDTO.class);
        } else {
            throw new RuntimeException("Информация с таким id: " + id + " не найдена");
        }
    }

    @Override
    public List<InformationDTO> getByStatus(boolean isChecked, UUID userId) {
        if (userId == null) {
            throw new RuntimeException("Неверный id");
        }

        List<Information> informationList = informationRepository.findByStatus(isChecked, userId);
        List<InformationDTO> dtoInformationList = new ArrayList<>();
        informationList.forEach(it -> dtoInformationList.add(modelMapper.map(it, InformationDTO.class)));

        if (dtoInformationList.isEmpty()) {
            throw new RuntimeException("Информация не найдена");
        } else {
            return dtoInformationList;
        }
    }

    @Override
    public void deleteInformation(UUID id) {
        if (id == null) {
            throw new RuntimeException("Неверный id");
        }

        if (informationRepository.findById(id).isPresent()) {
            informationRepository.deleteById(id);
        } else {
            throw new RuntimeException("Информации с таким id: " + id + " не существует");
        }
    }
}
