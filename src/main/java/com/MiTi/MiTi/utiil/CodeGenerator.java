package com.MiTi.MiTi.utiil;

import java.security.SecureRandom;

public class CodeGenerator {

    private static final String CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6; // 생성할 코드 길이

    private static SecureRandom random = new SecureRandom();

    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }
}