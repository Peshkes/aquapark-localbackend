package ru.kikopark.localbackend.modules.authentication.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kikopark.localbackend.configs.SecurityConfig;
import ru.kikopark.localbackend.modules.authentication.dto.AddEmployeeRequest;
import ru.kikopark.localbackend.modules.authentication.dto.AuthenticationRequest;
import ru.kikopark.localbackend.modules.authentication.dto.AuthenticationResponse;
import ru.kikopark.localbackend.modules.authentication.entities.EmployeeEntity;
import ru.kikopark.localbackend.modules.authentication.entities.RoleEntity;
import ru.kikopark.localbackend.modules.authentication.repositories.EmployeeRepository;
import ru.kikopark.localbackend.modules.authentication.repositories.RoleRepository;
import ru.kikopark.localbackend.modules.base.repositories.InstitutionRepository;
import ru.kikopark.localbackend.utils.AppError;
import ru.kikopark.localbackend.utils.Converter;
import ru.kikopark.localbackend.utils.JwtService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AuthenticationService implements UserDetailsService {
    private EmployeeRepository employeeRepository;
    private RoleRepository roleRepository;
    private InstitutionRepository institutionRepository;
    private JwtService jwtService;

    @Transactional
    public Object addNewEmployee(HttpEntity<String> employee) {
        Optional<AddEmployeeRequest> employeeRequest = Converter.jsonToObject(employee.getBody(), AddEmployeeRequest.class);
        if (employeeRequest.isPresent()) {
            try {
                EmployeeEntity newEmployee = employeeEntityMapper(employeeRequest.get());
                return employeeRepository.save(newEmployee);
            } catch (Exception e) {
                System.err.println("Ошибка при добавлении нового пользователя в базу данных: " + e.getMessage());
                return new AppError(HttpStatus.BAD_REQUEST.value(), "Почта уже существует");
            }
        } else
            return new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неверные данные");
    }

    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public Object authenticateUser(HttpEntity<String> httpEntity) {
        Optional<AuthenticationRequest> authenticationRequest = Converter.jsonToObject(httpEntity.getBody(), AuthenticationRequest.class);
        if (authenticationRequest.isPresent()) {
            AuthenticationRequest ar = authenticationRequest.get();
            String username = ar.getUsername();
            UserDetails userDetails = loadUserByUsername(username);
            if (userDetails != null) {
                if (SecurityConfig.passwordEncoder().matches(ar.getPassword(), userDetails.getPassword())) {
                    EmployeeEntity employee = getEmployeeByUsername(username);
                    String accessToken = jwtService.generateAccessToken(userDetails);
                    String refreshToken = jwtService.generateRefreshToken(userDetails);

                    String[] roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).distinct().toArray(String[]::new);

                    return new AuthenticationResponse(accessToken, refreshToken, roles, employee.getEmployeeId(), employee.getName());
                } else
                    return new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправленый пароль");
            } else
                return new AppError(HttpStatus.NOT_FOUND.value(), "Пользователь не найден");
        } else
            return new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неверные данные");
    }


    private EmployeeEntity getEmployeeByUsername(String username) {
        return employeeRepository.findEmployeeEntityByEmail(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Retrieve the user details from the database based on the email
        Optional<EmployeeEntity> optionalEmployee = Optional.ofNullable(employeeRepository.findEmployeeEntityByEmail(email));
        return optionalEmployee.map(this::buildUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

//    utils

    private EmployeeEntity employeeEntityMapper(AddEmployeeRequest addEmployeeRequest) {
        RoleEntity roleEntity = roleRepository.findRoleEntityByRoleId(addEmployeeRequest.getRoleId());
        if (roleEntity != null) {
            return new EmployeeEntity(
                    roleEntity,
                    institutionRepository.getInstitutionId(),
                    addEmployeeRequest.getName(),
                    SecurityConfig.passwordEncoder().encode(addEmployeeRequest.getPassword()),
                    addEmployeeRequest.getEmail());
        } else {
            throw new RuntimeException("Role is not found");
        }
    }

    private UserDetails buildUserDetails(EmployeeEntity employeeEntity) {
        RoleEntity[] roles = {employeeEntity.getRole()};
        // Create a list of GrantedAuthority based on the user's roles
        List<GrantedAuthority> authorities = Arrays.stream(roles)
                .map(roleEntity -> new SimpleGrantedAuthority(roleEntity.getRoleName()))
                .collect(Collectors.toList());
        // Build and return the UserDetails object
        return new User(employeeEntity.getEmail(), employeeEntity.getPassword(), authorities) {
        };
    }
}
