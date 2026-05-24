package com.eagledev.bookreaders.mappers;

import com.eagledev.bookreaders.dtos.admin.AdminUserResponse;
import com.eagledev.bookreaders.dtos.user.UserContactDto;
import com.eagledev.bookreaders.dtos.user.UserProfileResponse;
import com.eagledev.bookreaders.entities.User;
import com.eagledev.bookreaders.entities.UserContact;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
        UserProfileResponse toProfile(User user);
        AdminUserResponse toAdminUserResponse(User user);
        UserContactDto toContactDto(UserContact userContact);
}
