import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Scanner;

public class AlertBot {

    public static void main(String[] args) throws IOException {
        String aKey = "default";
        String aSecret = "default";
        String passphrase = "default";

        Scanner input = new Scanner(System.in);
        System.out.print("Do you want to add your API Key? Enter '1' for Yes or '2' for No ");
        String first = input.next();
        if (first.equals("1")) {
            System.out.print("Enter your Public Key: ");
            aKey = input.next();
            System.out.print("Enter your Secret Key: ");
            aSecret = input.next();
            System.out.print("Enter your PassPhrase: ");
            passphrase = input.next();
        }
        System.out.print("What email would you like to receive notifications to? ");
        String recipientEmail = input.next();

        ExchangeSpecification specification = new ExchangeSpecification(CoinbaseProExchange.class.getName());
        specification.setApiKey(aKey);
        specification.setSecretKey(aSecret);
        specification.setExchangeSpecificParametersItem("passphrase", passphrase);
        Exchange exchange = ExchangeFactory.INSTANCE.createExchange(specification);

//        Scanner input2 = new Scanner(System.in);
//        System.out.print("Enter most recent Daily high: ");
//        BigDecimal recentDailyHigh = BigDecimal.valueOf(input2.nextInt());
//        System.out.print("Enter most recent Daily low: ");
//        BigDecimal recentDailyLow = BigDecimal.valueOf(input2.nextInt());
//        System.out.println("Low: " + recentDailyLow + " High: " + recentDailyHigh);

        BigDecimal price = exchange.getMarketDataService().getTicker(CurrencyPair.BTC_USD).getLast();
        BigDecimal min = exchange.getMarketDataService().getTicker(CurrencyPair.BTC_USD).getLow();
        BigDecimal max = exchange.getMarketDataService().getTicker(CurrencyPair.BTC_USD).getHigh();

        while (price.compareTo(min) > 0 && price.compareTo(max) < 0) {
            price = exchange.getMarketDataService().getTicker(CurrencyPair.BTC_USD).getLast();
            try {
                Thread.sleep(1000);
                System.out.println("24 Hour Low   | Current | 24 Hour High \n" + min + " < " + price + " < " + max);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (price.compareTo(min) < 0){
            notifyAlert("BREAKDOWN", exchange, recipientEmail);
        } else notifyAlert("BREAKOUT", exchange, recipientEmail);

        Scanner input3 = new Scanner(System.in);
        System.out.println("Would you like to make an order? ");
        String decision = input3.next();
        if (decision.equals("Yes")){
            LimitOrder.Builder lo = new LimitOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_USD);
            exchange.getTradeService().placeLimitOrder(lo.build());
        }
        System.exit(1);
    }

    public static void notifyAlert(String status, Exchange exchange, String recipientEmail)throws IOException{
        String username = "INSERT_FROM_ADDRESS"; //Must Be a Gmail account.
        String password = "INSERT_FROM_PASSWORD";
        BigDecimal price;
        InputStream in = new FileInputStream("alert.wav");
        AudioStream as = new AudioStream(in);

        int notified = 0;
        while (notified < 1) {
            try {
                price = exchange.getMarketDataService().getTicker(CurrencyPair.BTC_USD).getLast();
                AudioPlayer.player.start(as);
                GoogleMail.Send(username, password, recipientEmail, "BITCOIN TECHNICAL ALERT!!", "BTC ALERT TECHNICAL " + status);
                System.out.println(price + " Breakout/Breakdown");
                Thread.sleep(5000);
                notified++;
            } catch (InterruptedException | MessagingException e) {
                e.printStackTrace();
            }
            AudioPlayer.player.stop(as);
        }
    }
}
