import org.knowm.xchange.Exchange;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static org.knowm.xchange.currency.CurrencyPair.BTC_USD;

public class Alerts implements Runnable{
    private Exchange exchange;
    private UserInfo user;

    public Alerts(Exchange exchange, UserInfo user) {
        this.exchange = exchange;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            runProgram(exchange, user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void runProgram(Exchange exchange, UserInfo user) throws IOException {
        String status = "OK";
        BreakCheck breakCheck = new BreakCheck();

        while (status.equals("OK")){
            status = breakCheck.breakCheck(exchange);
        }

        if (status.equals("BREAKDOWN")){
            generateAlert("BREAKDOWN", exchange, user.getRecipientEmail());
        } else generateAlert("BREAKOUT", exchange, user.getRecipientEmail());
        runProgram(exchange, user);
    }

    static void generateAlert(String status, Exchange exchange, String recipientEmail)throws IOException{
        String username = "INSERT_USERNAME"; //Enter a valid Gmail account for emails to be sent from
        String password = "INSERT_PASSWORD";
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
