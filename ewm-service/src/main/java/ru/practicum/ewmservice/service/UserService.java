package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.mapper.*;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.repository.*;
import ru.practicum.ewmservice.tools.exception.*;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto saveUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toUserFromNewUserRequest(newUserRequest);

        User userForSave;
        userForSave = userRepository.save(user);
        return UserMapper.toUserDto(userForSave);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Long from, Long size) {
        // Перевод порядкового номера элемента в номер страницы для Pageable
        int pageNum = (int) (from / size);
        Pageable pageable = PageRequest.of(pageNum, Math.toIntExact(size));
        if (ids == null) {
            return userRepository.findAllUsers(pageable);
        } else {
            return userRepository.findUsersByIds(ids, pageable);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        checkExistAndGetUser(id);
        userRepository.deleteById(id);
    }

    /**
     * Проверка, что объект существует,
     * если нет - исключение, если да - возврат его самого
     */
    public User checkExistAndGetUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User " + id));
    }

}
