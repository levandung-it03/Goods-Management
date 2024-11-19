package com.distributionsys.backend.mappers;

import com.distributionsys.backend.dtos.request.PaginatedRelationshipRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.entities.utils.PageObject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PageMappers {

    PageObject relationshipPageRequestToPageable(PaginatedRelationshipRequest paginatedRelationshipRequest);

    PageObject tablePageRequestToPageable(PaginatedTableRequest paginationTableRequest);
}
