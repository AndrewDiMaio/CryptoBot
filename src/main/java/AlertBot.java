import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;

import java.io.IOException;

public class AlertBot {
    public static void main(String[] args) throws IOException {
        //Takes user API key and/or notification email address
        UserInfo user = new UserInfo();
        user.setUserInfo();

        //Generates the exchange
        Exchange exchange = generateExchange(user);

        //Plots current orderbook orders
        Thread orderBook = new Thread(new OrderBook(exchange));
        orderBook.start();

        //Runs main alert application
        Thread alert = new Thread(new Alerts(exchange, user));
        alert.start();
    }

    static Exchange generateExchange(UserInfo userInfo){
        ExchangeSpecification specification = new ExchangeSpecification(CoinbaseProExchange.class.getName());
        specification.setApiKey(userInfo.getaKey());
        specification.setSecretKey(userInfo.getaSecret());
        specification.setExchangeSpecificParametersItem("passphrase", userInfo.getPassphrase());
        return ExchangeFactory.INSTANCE.createExchange(specification);
    }

}