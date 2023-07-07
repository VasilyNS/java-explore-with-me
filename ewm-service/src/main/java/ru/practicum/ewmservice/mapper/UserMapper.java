package ru.practicum.ewmservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.model.*;

@UtilityClass
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static User toUserFromNewUserRequest(NewUserRequest newUserRequest) {
        return new User(
                0L,
                newUserRequest.getName(),
                newUserRequest.getEmail()
        );
    }

}
