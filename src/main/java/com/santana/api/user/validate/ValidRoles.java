package com.santana.api.user.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidRolesValidator.class})
@Documented
public @interface ValidRoles {

    String message() default "Invalid role detected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
