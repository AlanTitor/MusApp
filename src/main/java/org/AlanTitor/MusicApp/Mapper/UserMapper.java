package org.AlanTitor.MusicApp.Mapper;

import org.AlanTitor.MusicApp.Dto.Users.RegisterUserRequest;
import org.AlanTitor.MusicApp.Dto.Users.RegisterUserResponse;
import org.AlanTitor.MusicApp.Dto.Users.ResponseUserDataDto;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    RegisterUserResponse toResponse(User user);
    User toUser(RegisterUserRequest request);
    ResponseUserDataDto toDtoWithMusic(User user);
}
