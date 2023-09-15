package com.santana.api.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.santana.api.user.validate.ValidRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    @Field("_id")
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

    private boolean enabled = true;

    public User(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
    }
}
