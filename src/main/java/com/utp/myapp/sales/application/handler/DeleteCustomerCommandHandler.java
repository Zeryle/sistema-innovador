package com.utp.myapp.sales.application.handler;

import com.utp.myapp.sales.application.command.DeleteCustomerCommand;
import com.utp.myapp.sales.domain.model.repository.ICustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCustomerCommandHandler {

    private final ICustomerRepository repository;

    public void handle(DeleteCustomerCommand command) {
        repository.delete(command.getId());
    }
}
