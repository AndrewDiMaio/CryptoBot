import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.Exchange;

import java.io.IOException;
import java.math.BigDecimal;

public class AlertBotTest {

    @Test
    public void generateAlertBreakdownTest() throws IOException {
        Exchange exchange = AlertBot.generateExchange(new UserInfo());
        Alerts.generateAlert("BREAKDOWN", exchange, "cbcryptoalert@gmail.com");
    }

    @Test
    public void generateAlertBreakoutTest() throws IOException {
        Exchange exchange = AlertBot.generateExchange(new UserInfo());
        Alerts.generateAlert("BREAKOUT", exchange, "cbcryptoalert@gmail.com");
    }

    @Test
    public void generateExchangeTest(){
        Exchange exchange = AlertBot.generateExchange(new UserInfo());
        System.out.println(exchange.getExchangeSymbols());
    }

    @Test
    public void statusBreakoutTest(){
        Assert.assertEquals("BREAKOUT", Alerts.status(new BigDecimal(5555555), new BigDecimal(200), new BigDecimal(5000000)));
    }

    @Test
    public void statusBreakdownTest(){
        Assert.assertEquals("BREAKDOWN", Alerts.status(new BigDecimal(1111), new BigDecimal(12000), new BigDecimal(100000)));
    }
}