import org.junit.Assert;
import org.junit.Test;

public class UserInfoTest {

    @Test
    public void getaKey() {
        UserInfo nsu = new UserInfo();
        String actual = nsu.getaKey();
        String expected = "default1";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getaSecret() {
        UserInfo nsu = new UserInfo();
        String actual = nsu.getaSecret();
        String expected = "default2";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getPassphrase() {
        UserInfo nsu = new UserInfo();
        String actual = nsu.getPassphrase();
        String expected = "default3";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getRecipientEmail() {
        UserInfo nsu = new UserInfo();
        String actual = nsu.getRecipientEmail();
        String expected = "default4";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setUserInfo() {

    }
}