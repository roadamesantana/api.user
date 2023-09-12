package com.santana.api.user.initialize;

import com.santana.api.user.domain.User;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!production")
@Component
public class UserDataInitializer implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        User user = User.builder()
                .name("Para teste fora de produção")
                .build();

        System.out.println("--- --- --- SmartInitializingSingleton: ".concat(user.getName()));
    }

}
