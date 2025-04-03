package com.project.fedfaxe.service;

import com.project.fedfaxe.model.PackageProduct;
import com.project.fedfaxe.repository.PackageProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PackageProductService {

    private final PackageProductRepository packageProductRepository;

    public PackageProduct addPackage(PackageProduct packageProduct) {
        return packageProductRepository.save(packageProduct);
    }

    public Optional<PackageProduct> getPackageById(String id) {
        return packageProductRepository.findById(id);
    }

    public boolean deletePackage(String id) {
        if (packageProductRepository.existsById(id)) {
            packageProductRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<PackageProduct> getAllPackages() {
        return packageProductRepository.findAll();
    }

}
