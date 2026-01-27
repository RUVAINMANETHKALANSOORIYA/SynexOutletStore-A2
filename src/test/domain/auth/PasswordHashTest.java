package domain.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHashTest {

    @Test
    @DisplayName("Should hash string to SHA-256 hex")
    void testSha256() {
        String input = "123456";
        String hashed = PasswordHash.sha256(input);
        
        assertEquals(64, hashed.length());
        assertTrue(hashed.matches("[0-9a-fA-F]{64}"));
        
        // Known hash for "123456"
        assertEquals("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", hashed);
    }

    @Test
    @DisplayName("Should detect strings that look like SHA-256 hex")
    void testLooksLikeSha256Hex() {
        String valid = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
        String invalidShort = "8d969eef";
        String invalidChars = "zz969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
        
        assertTrue(PasswordHash.looksLikeSha256Hex(valid));
        assertFalse(PasswordHash.looksLikeSha256Hex(invalidShort));
        assertFalse(PasswordHash.looksLikeSha256Hex(invalidChars));
        assertFalse(PasswordHash.looksLikeSha256Hex(null));
    }
}
