import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static org.knowm.xchange.currency.CurrencyPair.BTC_USD;

public class AlertBot {
    public static void main(String[] args) throws IOException {
        UserInfo user = new UserInfo();
        user.setUserInfo();
        Exchange exchange = generateExchange(user);
        runProgram(exchange, user);
    }

    private static void runProgram(Exchange exchange, UserInfo api) throws IOException {
        String status = "OK";
        BreakCheck breakCheck = new BreakCheck();

        while (status.equals("OK")){
            status = breakCheck.breakCheck(exchange);
        }

        if (status.equals("BREAKDOWN")){
            generateAlert("BREAKDOWN", exchange, api.getRecipientEmail());
        } else generateAlert("BREAKOUT", exchange, api.getRecipientEmail());
        runProgram(exchange, api);
    }

    static void generateAlert(String status, Exchange exchange, String recipientEmail)throws IOException{
        String username = "ENTER_USERNAME"; //Enter a valid Gmail account for emails to be sent from
        String password = "ENTER_PASSWORD";
        BigDecimal price;
        InputStream in = new FileInputStream("alert.wav");
        AudioStream as = new AudioStream(in);

        int notified = 0;
        while (notified < 1) {
            try {
                price = exchange.getMarketDataService().getTicker(BTC_USD).getLast();
                AudioPlayer.player.start(as);
                GoogleMail.Send(username, password, recipientEmail, "BITCOIN TECHNICAL ALERT!!", "BTC ALERT TECHNICAL " + status);
                System.out.println(price + " " + status);
                Thread.sleep(30000);
                notified++;
            } catch (InterruptedException | MessagingException e) {
                e.printStackTrace();
            }
            AudioPlayer.player.stop(as);
        }
    }

    static Exchange generateExchange(UserInfo userInfo){
        ExchangeSpecification specification = new ExchangeSpecification(CoinbaseProExchange.class.getName());
        specification.setApiKey(userInfo.getaKey());
        specification.setSecretKey(userInfo.getaSecret());
        specification.setExchangeSpecificParametersItem("passphrase", userInfo.getPassphrase());
        return ExchangeFactory.INSTANCE.createExchange(specification);
    }

    static String status(BigDecimal price, BigDecimal min, BigDecimal max) {
        if (price.doubleValue() < min.floatValue()){
            System.out.println(price + " " + max);
            return "BREAKDOWN";
        } else {
            System.out.println(price + " " + max);
            return "BREAKOUT";
        }
    }
}