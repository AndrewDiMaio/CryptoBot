import java.util.Scanner;

class UserInfo {
    private String aKey = "default1";
    private String aSecret = "default2";
    private String passphrase = "default3";
    private String recipientEmail = "default4";

    String getaKey() {
        return aKey;
    }

    String getaSecret() {
        return aSecret;
    }

    String getPassphrase() {
        return passphrase;
    }

    String getRecipientEmail() {
        return recipientEmail;
    }

    void setUserInfo() {
        Scanner input = new Scanner(System.in);
        System.out.print("Do you want to add your API Key? Enter '1' for Yes or '2' for No ");
        String first = input.next();
        if (first.equals("1")) {
            System.out.print("Enter your Public Key: ");
            this.aKey = input.next();
            System.out.print("Enter your Secret Key: ");
            this.aSecret = input.next();
            System.out.print("Enter your PassPhrase: ");
            this.passphrase = input.next();
        }else if (first.equals("2")) {
            System.out.print("What email would you like to receive notifications to? ");
            this.recipientEmail = input.next();
        } else {
            System.out.println("Invalid input. Please try again.");
            setUserInfo();
        }
    }
}