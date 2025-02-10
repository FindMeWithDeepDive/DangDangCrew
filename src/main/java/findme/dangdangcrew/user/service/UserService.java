package findme.dangdangcrew.user.service;

import findme.dangdangcrew.user.dto.UserRequestDto;
import findme.dangdangcrew.user.dto.UserResponseDto;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(userRequestDto.getNickname())) {
            throw new IllegalArgumentException("Nickname already in use");
        }

        // 전화번호 중복 검사
        if (userRepository.existsByPhoneNumber(userRequestDto.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already in use");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());

        // 사용자 저장
        User user = User.builder()
                .email(userRequestDto.getEmail())
                .password(encodedPassword)
                .name(userRequestDto.getName())
                .nickname(userRequestDto.getNickname())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .build();
        userRepository.save(user);

        return new UserResponseDto(user.getId(), user.getEmail(), user.getName(), user.getNickname(), user.getPhoneNumber(), user.getCreatedAt());
    }

}
