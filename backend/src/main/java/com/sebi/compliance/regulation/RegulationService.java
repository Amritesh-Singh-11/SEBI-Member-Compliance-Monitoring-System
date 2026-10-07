package com.sebi.compliance.regulation;

import com.sebi.compliance.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegulationService {

    private final RegulationRepository regulationRepository;

    public RegulationService(RegulationRepository regulationRepository) {
        this.regulationRepository = regulationRepository;
    }

    public List<Regulation> getAllRegulations() {
        return regulationRepository.findAll();
    }

    public Regulation getRegulationById(Long id) {
        return regulationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regulation", "id", id));
    }

    public Regulation createRegulation(Regulation reg) {
        return regulationRepository.save(reg);
    }
}
