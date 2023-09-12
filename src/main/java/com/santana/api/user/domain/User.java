package com.santana.api.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.santana.api.user.validate.ValidRoles;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    private String id = "";

    @Builder.Default
    private String name = "";

    @NotEmpty
    @Email
    @Builder.Default
    private String email = "";

    @Size(min = 3, max = 254)
    @Builder.Default
    private String password = "";

    @ValidRoles
    private String role = "";
}
