package com.kett.TicketSystem.user.domain.exceptions;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }

    public UserException(EmailAddress emailAddress) {
        //TODO Auto-generated constructor stub
    }
}
