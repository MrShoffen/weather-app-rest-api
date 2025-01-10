package org.mrshoffen.weather.util;

import lombok.experimental.UtilityClass;
import org.mapstruct.Named;
import org.mindrot.jbcrypt.BCrypt;

@UtilityClass
public class PasswordEncoder {

    @Named("encodePassword")
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean arePasswordsEqual(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }


}
