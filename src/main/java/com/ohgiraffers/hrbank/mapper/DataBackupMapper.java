package com.ohgiraffers.hrbank.mapper;


import com.ohgiraffers.hrbank.dto.data.DataBackupDto;
import com.ohgiraffers.hrbank.entity.DataBackup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataBackupMapper {

    @Mapping(target = "id", source = "file.id")
    DataBackupDto toDto(DataBackup dataBackup);

}
