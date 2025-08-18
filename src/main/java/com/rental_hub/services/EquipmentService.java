package com.rental_hub.services;

import org.springframework.stereotype.Service;

import com.rental_hub.repositories.EquipmentRepository;
import com.rental_hub.repositories.TagRepository;

import jakarta.persistence.EntityNotFoundException;

import com.rental_hub.entities.Equipment;
import com.rental_hub.entities.Tag;

import java.util.Collections;
import java.util.List;

@Service
public class EquipmentService {
    
    private final EquipmentRepository equipmentRepo;

    private final TagRepository tagRepo;

    public EquipmentService(EquipmentRepository equipmentRepo, TagRepository tagRepo) {
        this.equipmentRepo = equipmentRepo;
        this.tagRepo = tagRepo;
    }

    public void addEquipment(Equipment equipment) {
        equipmentRepo.save(equipment);
    }

    public void updateEquipment(int id, Equipment equipment) {
        Equipment equipmentToUpdate = equipmentRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        equipmentToUpdate.setName(equipment.getName());
        equipmentToUpdate.setBrand(equipment.getBrand());
        equipmentToUpdate.setType(equipment.getType());
        equipmentToUpdate.setPricePerDayCents(equipment.getPricePerDayCents());
        equipmentToUpdate.setAvailable(equipment.isAvailable());
        equipmentRepo.save(equipmentToUpdate);
    }

    public void addTagToEquipment(String tagId, int equipmentId) {
        Equipment equipment = equipmentRepo.findById(equipmentId)
            .orElseThrow(EntityNotFoundException::new);
        Tag tag = tagRepo.findById(tagId)
            .orElseThrow(EntityNotFoundException::new);

        equipment.getTags().add(tag);
        tag.getEquipments().add(equipment);

        equipmentRepo.save(equipment);
        tagRepo.save(tag);
    }

    public List<Equipment> getEquipmentsInPriceRangeInCents(int min, int max) {
        if (min > max) return Collections.emptyList();

        return equipmentRepo.findAll().stream()
                .filter(Equipment::isAvailable)
                .filter(equipment -> isWithinPriceRange(equipment, min, max))
                .toList();
    }

    private boolean isWithinPriceRange(Equipment equipment, int min, int max) {
        return equipment.getPricePerDayCents() >= min && equipment.getPricePerDayCents() <= max;
    }
}
