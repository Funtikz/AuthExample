package org.example.authexample.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.authexample.dto.user.UserAuthDto;
import org.example.authexample.dto.user.UserDto;
import org.example.authexample.dto.user.UserRegistrationDto;
import org.example.authexample.entity.User;
import org.example.authexample.exceptions.DifferentPassword;
import org.example.authexample.exceptions.IncorrectData;
import org.example.authexample.repository.UserRepository;
import org.example.authexample.security.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Поиск по почте
    public UserRegistrationDto findByRegistrationNumber(String number){
        User user = userRepository.findByPhoneNumber(number).orElseThrow(
                () -> new UsernameNotFoundException("User this phone number " + number + " not found")
        );
        return toUserRegistrationDto(user);
    }

    public UserDto findByPhoneNumber(String number){
        User user = userRepository.findByPhoneNumber(number).orElseThrow(
                () -> new UsernameNotFoundException("User this phone number " + number + " not found")
        );
        return toDto(user);
    }
    //Вход для User и установка в куки
    public UserDto login(UserAuthDto userCredentialDto, HttpServletResponse response){
        // 1. Ищем пользователя по email
        String number = userCredentialDto.getPhoneNumber();
        String password = userCredentialDto.getPassword();
        Optional<User> userOpt = userRepository.findByPhoneNumber(number);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 3. Генерируем JWT с ролями
                String jwtToken = jwtService.generateJwtToken(user.getPhoneNumber(), user.getRoles());

                // 4. Создаем httpOnly cookie
                Cookie jwtCookie = new Cookie("jwt-auth-token", jwtToken);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(180 * 24 * 60 * 60); // Время жизни куки - 180 дней

                // 5. Добавляем куки в response
                response.addCookie(jwtCookie);
                UserDto dto = toDto(user);

                return dto;
            }
        }
        // 6. Если аутентификация неуспешна, возвращаем ошибку
        throw new IncorrectData();
    }

    //Регистрация нового пользователя
    @Transactional
    public UserRegistrationDto addUser(UserRegistrationDto userDto) {
        if (userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("User with phone number " + userDto.getPhoneNumber() + " already exists");
        }
        User entity = RegistrationDtotoUser(userDto);
        if (entity == null) {
            throw new RuntimeException("Mapped entity is null");
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())){
            throw new DifferentPassword();
        }
        entity.setRoles(Set.of("USER"));
        entity.setPassword(passwordEncoder.encode(entity.getPassword())); // Хеширование пароля
        User currentUser = userRepository.save(entity);
        return toUserRegistrationDto(currentUser);
    }

    @Transactional
    public UserDto updateUser(UserDto userDto){
        return toDto(userRepository.save(toUser(userDto)));
    }


    public User RegistrationDtotoUser(UserRegistrationDto userDto){
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRoles(userDto.getRoles());
        user.setId(userDto.getId());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPassword(userDto.getPassword());
        return  user;
    }

    public UserRegistrationDto toUserRegistrationDto(User user){
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoles(user.getRoles());
        userDto.setId(user.getId());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setId(user.getId());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoles(user.getRoles());
        userDto.setId(user.getId());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setId(user.getId());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    public User toUser(UserDto userDto){
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRoles(userDto.getRoles());
        user.setId(userDto.getId());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPassword(userDto.getPassword());
        return  user;
    }

}
