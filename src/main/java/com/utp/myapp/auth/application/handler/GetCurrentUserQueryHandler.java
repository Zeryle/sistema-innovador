package com.utp.myapp.auth.application.handler;

import com.utp.myapp.auth.application.assembler.UserAssembler;
import com.utp.myapp.auth.application.dto.UserDto;
import com.utp.myapp.auth.application.query.GetCurrentUserQuery;
import com.utp.myapp.auth.domain.model.aggregates.User;
import com.utp.myapp.auth.domain.model.repository.IUserRepository;
import com.utp.myapp.shared.domain.model.exceptions.EntityNotFoundException;
import com.utp.myapp.shared.domain.model.valueobjects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCurrentUserQueryHandler {

    private final IUserRepository userRepository;
    private final UserAssembler userAssembler;

    @Transactional(readOnly = true)
    public UserDto handle(GetCurrentUserQuery query) {
        Email email = Email.of(query.getEmail());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", query.getEmail()));
        return userAssembler.toDto(user);
    }
}
