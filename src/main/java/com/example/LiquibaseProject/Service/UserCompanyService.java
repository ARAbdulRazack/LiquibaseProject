package com.example.LiquibaseProject.Service;

import com.example.LiquibaseProject.DTO.UserCompanyRequestDTO;
import com.example.LiquibaseProject.DTO.UserCompanyResponseDTO;
import com.example.LiquibaseProject.Mapper.UserCompanyMapper;
import com.example.LiquibaseProject.Model.UserCompany;
import com.example.LiquibaseProject.Repository.UserCompanyRepository;
import com.example.LiquibaseProject.Specification.UserCompanySpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class UserCompanyService {

    private final UserCompanyRepository userCompanyRepository;
    private final UserCompanyMapper userCompanyMapper;

    @Autowired
    public UserCompanyService(UserCompanyRepository userCompanyRepository, UserCompanyMapper userCompanyMapper) {
        this.userCompanyRepository = userCompanyRepository;
        this.userCompanyMapper = userCompanyMapper;
    }

    public List<UserCompanyResponseDTO> getUserCompanies(Long userId, Long companyId, Long userCompanyId) {
        if (userCompanyId != null) {
            UserCompany userCompany = userCompanyRepository.findById(userCompanyId)
                    .orElseThrow(() -> new IllegalArgumentException("UserCompany not found with id: " + userCompanyId));
            return Collections.singletonList(userCompanyMapper.toDto(userCompany));
        } else if (userId != null) {
            List<UserCompany> userCompanies = userCompanyRepository.findByUserId(userId);
            return userCompanyMapper.toDtoList(userCompanies);
        } else if (companyId != null) {
            List<UserCompany> userCompanies = userCompanyRepository.findByCompanyId(companyId);
            return userCompanyMapper.toDtoList(userCompanies);
        } else {
            List<UserCompany> userCompanies = userCompanyRepository.findAll();
            return userCompanyMapper.toDtoList(userCompanies);
        }
    }

    public Page<UserCompanyResponseDTO> getUserCompaniesByUserNameAndCompanyName(String userName, String companyName, Pageable pageable) {
        Specification<UserCompany> spec = UserCompanySpecification.getUserCompaniesSpec(userName, companyName);
        return userCompanyRepository.findAll(spec, pageable).map(userCompanyMapper::toDto);
    }

    public List<UserCompanyResponseDTO> getProductWithSorting(String field) {
        List<UserCompany> userCompanies = userCompanyRepository.findAll(Sort.by(Sort.Direction.ASC, field));
        return userCompanyMapper.toDtoList(userCompanies);
    }

    public Page<UserCompanyResponseDTO> getProductWithPagination(int offSet, int pageSize) {
        Page<UserCompany> userCompanies = userCompanyRepository.findAll(PageRequest.of(offSet, pageSize));
        return userCompanies.map(userCompanyMapper::toDto);
    }

    public Page<UserCompanyResponseDTO> getProductWithPaginationAndSorting(int offSet, int pageSize, String field) {
        Page<UserCompany> userCompanies = userCompanyRepository.findAll(PageRequest.of(offSet, pageSize, Sort.by(field)));
        return userCompanies.map(userCompanyMapper::toDto);
    }

    public UserCompanyResponseDTO getUserCompanyById(Long id) {
        UserCompany userCompany = userCompanyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserCompany not found with id: " + id));
        return userCompanyMapper.toDto(userCompany);
    }

    public List<UserCompanyResponseDTO> getUserCompaniesByUserId(Long userId) {
        List<UserCompany> userCompanies = userCompanyRepository.findByUserId(userId);
        return userCompanyMapper.toDtoList(userCompanies);
    }

    public List<UserCompanyResponseDTO> getUserCompaniesByCompanyId(Long companyId) {
        List<UserCompany> userCompanies = userCompanyRepository.findByCompanyId(companyId);
        return userCompanyMapper.toDtoList(userCompanies);
    }

    public UserCompanyResponseDTO createUserCompany(UserCompanyRequestDTO userCompanyRequestDTO) {
        UserCompany userCompany = userCompanyMapper.toEntity(userCompanyRequestDTO);
        userCompany = userCompanyRepository.save(userCompany);
        return userCompanyMapper.toDto(userCompany);
    }

    public UserCompanyResponseDTO updateUserCompany(Long id, UserCompanyRequestDTO userCompanyRequestDTO) {
        UserCompany existingUserCompany = userCompanyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserCompany not found with id: " + id));

        userCompanyMapper.updateEntityFromDTO(userCompanyRequestDTO, existingUserCompany);
        existingUserCompany = userCompanyRepository.save(existingUserCompany);

        return userCompanyMapper.toDto(existingUserCompany);
    }

    public void deleteUserCompany(Long id) {
        userCompanyRepository.deleteById(id);
    }
}
