package org.braketime.machinetrackerapi.services;


import lombok.RequiredArgsConstructor;
import org.braketime.machinetrackerapi.Dtos.BusinessCreateUpdateRequest;
import org.braketime.machinetrackerapi.Dtos.BusinessResponse;
import org.braketime.machinetrackerapi.domain.Business;
import org.braketime.machinetrackerapi.exception.BadRequestException;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.mapper.BusinessMapper;
import org.braketime.machinetrackerapi.repository.BusinessRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository repository;
    private final BusinessMapper businessMapper;

    // Create
    public BusinessResponse createBusiness(BusinessCreateUpdateRequest request){
        if (repository.existsByNameAndActive(request.getName(), true)){
            throw new BadRequestException("Business already exists with same name");
        }
        Business createdBusiness = businessMapper.toEntity(request);
        return businessMapper.toDto(repository.save(createdBusiness));
    }

    //getbyID
    public BusinessResponse getByID(String id){
        Business business = repository.findByIdAndActive(id,true).orElseThrow(()->new NotFoundException("Business not found"));
        return businessMapper.toDto(business);
    }

    // Get All
    public Page<BusinessResponse> getAll(boolean active, Pageable pageable){
        return repository.findAllByActive(true,pageable).map(businessMapper::toDto);
    }

    // Update
    public BusinessResponse update(String id, BusinessCreateUpdateRequest request){
        Business business = repository.findByIdAndActive(id, true).orElseThrow(()->new NotFoundException("Business with ID not found"));
        businessMapper.update(request, business);
        return businessMapper.toDto(business);

    }

    // Delete
    public BusinessResponse deActive(String id){
        Business business = repository.findByIdAndActive(id,true).orElseThrow(()->new NotFoundException("Selected Business is not found"));
        business.setActive(false);
        repository.save(business);
        return businessMapper.toDto(business);
    }
    // Delete
    public BusinessResponse reActive(String id){
        Business business = repository.findById(id).orElseThrow(()->new NotFoundException("Selected Business is not found"));
        business.setActive(true);
        repository.save(business);
        return businessMapper.toDto(business);
    }

}
