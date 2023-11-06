package com.jromans.hwk.transactions.validation;

import com.jromans.hwk.configuration.exceptions.MyConstraintException;
import com.jromans.hwk.configuration.exceptions.MyViolation;
import com.jromans.hwk.configuration.mapping.ConstraintViolationMapper;
import com.jromans.hwk.transactions.SendRequestDTO;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.cfg.defs.DecimalMaxDef;
import org.hibernate.validator.cfg.defs.DecimalMinDef;
import org.hibernate.validator.cfg.defs.NotNullDef;

import java.util.List;

public class TransactionRequestValidator {
    private static Validator getValidator() {
        var configuration = Validation
                .byProvider(HibernateValidator.class)
                .configure();

        var constraintMapping = configuration.createConstraintMapping();

        constraintMapping
                .type(SendRequestDTO.class)
                .field("currency")
                .constraint(new NotNullDef())
                .field("amount")
                .ignoreAnnotations(true)
                .constraint(new NotNullDef())
                .constraint(new DecimalMinDef().value("0.01").inclusive(true))
                .constraint(new DecimalMaxDef().value("1000000").inclusive(true));

        return configuration.addMapping(constraintMapping)
                .buildValidatorFactory()
                .getValidator();
    }

    public static void validate(SendRequestDTO request) {
        var constraintViolations = getValidator().validate(request);
        var myViolations = ConstraintViolationMapper.getViolations(new ConstraintViolationException(constraintViolations));
        validateMultipleFields(request, myViolations);

        if (!myViolations.isEmpty()) throw new MyConstraintException(myViolations);
    }


    /**
     * Easier to implement this way than own Hibernate constraint, but means hardcoded path
     *
     * @param request      Money transfer request
     * @param myViolations List of violations to append additional validation results
     */
    private static void validateMultipleFields(SendRequestDTO request, List<MyViolation> myViolations) {
        if (request.getAccountDst().equals(request.getAccountSrc())) {
            myViolations.add(new MyViolation("accountDestination", "Destination and origin accounts cannot be the same"));
        }
    }
}
