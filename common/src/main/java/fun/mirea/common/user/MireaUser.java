package fun.mirea.common.user;

public interface MireaUser {

    String getName();

    void setInstitute(Institute institute);

    Institute getInstitute();

    void setAuthenticationData(AuthenticationData authenticationData);

    AuthenticationData getAuthenticationData();

}
