package com.example.gamestore.service;

import com.example.gamestore.dto.InformationDTO;

import java.util.List;
import java.util.UUID;

public interface InformationService {
    InformationDTO addInformation(InformationDTO informationDTO);

    List<InformationDTO> getAll();

    InformationDTO getById(UUID id);

    List<InformationDTO> getByStatus(boolean isChecked, UUID id);

    void deleteInformation(UUID id);
}
