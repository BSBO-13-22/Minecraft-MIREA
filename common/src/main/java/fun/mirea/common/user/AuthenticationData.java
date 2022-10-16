package fun.mirea.common.user;

public record AuthenticationData(EncryptionType encryptionType, String password, String secret) { }

