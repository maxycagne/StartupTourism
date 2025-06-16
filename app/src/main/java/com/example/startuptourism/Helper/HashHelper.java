package com.example.startuptourism.Helper;

import org.mindrot.jbcrypt.BCrypt;

public class HashHelper {
    private static final String pepper = "jU7^gP9$dM7!cG5)kUr%7Yh*Kj&5FgT4";

    public static String hashPassword(String password) {

        String salt = BCrypt.gensalt();
        password += pepper;
        return BCrypt.hashpw(password, salt);
    }

    public static boolean hashPasswordCheck(String password, String hashed) {
        password += pepper;
        return BCrypt.checkpw(password, hashed);
    }
}
