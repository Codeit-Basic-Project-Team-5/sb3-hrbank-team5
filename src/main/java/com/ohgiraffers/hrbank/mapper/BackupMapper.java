package com.ohgiraffers.hrbank.mapper;


import com.ohgiraffers.hrbank.dto.data.BackupDto;
import com.ohgiraffers.hrbank.entity.Backup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BackupMapper {

    @Mapping(target = "id", source = "file.id")
    BackupDto toDto(Backup backup);

}
