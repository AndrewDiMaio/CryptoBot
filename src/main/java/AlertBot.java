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
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Locale;

import static org.knowm.xchange.currency.CurrencyPair.BTC_USD;

public class AlertBot {
    public static void main(String[] args) throws IOException {
        UserInfo api = new UserInfo();
        api.setUserInfo();
        Exchange exchange = generateExchange(api);
        runProgram(exchange, api);
    }

    private static void runProgram(Exchange exchange, UserInfo api) throws IOException {
        String status = "OK";

        while (status.equals("OK")){
            status = breakCheck(exchange);
        }

        if (status.equals("BREAKDOWN")){
            generateAlert("BREAKDOWN", exchange, api.getRecipientEmail());
        } else generateAlert("BREAKOUT", exchange, api.getRecipientEmail());
        runProgram(exchange, api);
    }

    private static void generateAlert(String status, Exchange exchange, String recipientEmail)throws IOException{
        String username = "ENTER_USERNAME"; //Enter a valid Gmail account for emails to be sent from
        String password = "ENTER_PASSWORD";
        BigDecimal price;
        InputStream in = new FileInputStream("alert.wav");
        AudioStream as = new AudioStream(in);

        int notified = 0;
        while (notified < 3) {
            try {
                price = exchange.getMarketDataService().getTicker(BTC_USD).getLast();
                AudioPlayer.player.start(as);
                GoogleMail.Send(username, password, recipientEmail, "BITCOIN TECHNICAL ALERT!!", "BTC ALERT TECHNICAL " + status);
                System.out.println(price + status);
                Thread.sleep(30000);
                notified++;
            } catch (InterruptedException | MessagingException e) {
                e.printStackTrace();
            }
            AudioPlayer.player.stop(as);
        }
    }

    private static Exchange generateExchange(UserInfo userInfo){
        ExchangeSpecification specification = new ExchangeSpecification(CoinbaseProExchange.class.getName());
        specification.setApiKey(userInfo.getaKey());
        specification.setSecretKey(userInfo.getaSecret());
        specification.setExchangeSpecificParametersItem("passphrase", userInfo.getPassphrase());
        return ExchangeFactory.INSTANCE.createExchange(specification);
    }

    private static String breakCheck(Exchange exchange) throws IOException {
        BigDecimal price = exchange.getMarketDataService().getTicker(BTC_USD).getLast();
        BigDecimal min = exchange.getMarketDataService().getTicker(BTC_USD).getLow();
        BigDecimal max = exchange.getMarketDataService().getTicker(BTC_USD).getHigh();

        while (price.compareTo(min) > 0 && price.compareTo(max) < 0) {
            price = exchange.getMarketDataService().getTicker(BTC_USD).getLast();
            try {
                Thread.sleep(2500);
                BigDecimal volume = exchange.getMarketDataService().getTicker(BTC_USD).getVolume();
                Long volumeInDollar = volume.longValue()*price.longValue();
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                String volumeInDollarString = numberFormat.format(volumeInDollar);
                Timestamp time = new Timestamp(System.currentTimeMillis());
                System.out.println(time.toLocalDateTime().toLocalDate() + " " + time.toLocalDateTime().toLocalTime());
                System.out.printf("24 Hour Low: |    Current:    |    24 Hour High: \n%s       <    %s     <    %s%n", min.floatValue(), price.floatValue(), max.floatValue());
                System.out.printf("Current Price: $%s%n", price);
                System.out.printf("24 Hour Volume: %s Bitcoin Traded or $%s USD%n", volume.floatValue(), volumeInDollarString);
                System.out.println("************************************************");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return status(price, min);
    }

    private static String status(BigDecimal price, BigDecimal min) {
        if (price.compareTo(min) < 0){
            return "BREAKDOWN";
        } else return "BREAKOUT";
    }
}
