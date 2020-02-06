import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.Exchange;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class BreakCheckTest {

    @Test
    public void getExchange() {
        Exchange exchange = AlertBot.generateExchange(new UserInfo());
        BreakCheck bc = new BreakCheck();
        bc.setExchange(exchange);

        Exchange actual = bc.getExchange();

        Assert.assertEquals(exchange, actual);
    }

    @Test
    public void setExchange() {
        Exchange exchange = AlertBot.generateExchange(new UserInfo());
        BreakCheck bc = new BreakCheck();
        bc.setExchange(exchange);

        Exchange actual = bc.getExchange();

        Assert.assertEquals(exchange, actual);
    }

    @Test
    public void getPrice() {
        BreakCheck bc = new BreakCheck();
        bc.setPrice(new BigDecimal(500.00));

        BigDecimal expected = new BigDecimal(500.00);
        BigDecimal actual = bc.getPrice();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setPrice() {
        BreakCheck bc = new BreakCheck();
        bc.setPrice(new BigDecimal(500.00));

        BigDecimal expected = new BigDecimal(500.00);
        BigDecimal actual = bc.getPrice();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setGetMin() {
        BreakCheck bc = new BreakCheck();
        bc.setMin(new BigDecimal(11.11));

        BigDecimal expected = new BigDecimal(11.11);
        BigDecimal actual = bc.getMin();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setGetMax() {
        BreakCheck bc = new BreakCheck();
        bc.setMax(new BigDecimal(4444444.44));

        BigDecimal expected = new BigDecimal(4444444.44);
        BigDecimal actual = bc.getMax();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void breakOutCheck() throws IOException {
        BreakCheck bc = new BreakCheck();
        AlertBot.generateExchange(new UserInfo());
        bc.setPrice(new BigDecimal(3000000));

        String actual = bc.breakCheck();
        String expected = "BREAKOUT";

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void breakDownCheck() throws IOException {
        BreakCheck bc = new BreakCheck();
        AlertBot.generateExchange(new UserInfo());
        bc.setPrice(new BigDecimal(2));

        String actual = bc.breakCheck();
        String expected = "BREAKDOWN";

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void breakCheck1() throws IOException {
        //THIS TEST COULD FAIL IF THERE IS CURRENT VOLATILITY IN THE MARKET
        BreakCheck bc = new BreakCheck();
        Exchange exchange = AlertBot.generateExchange(new UserInfo());

        String actual = bc.breakCheck(exchange);
        String expected = "OK";

        Assert.assertEquals(expected, actual);
    }
}