import org.knowm.xchange.Exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Locale;

import static org.knowm.xchange.currency.CurrencyPair.BTC_USD;

class BreakCheck {
    private BigDecimal price = BigDecimal.valueOf(5000);
    private BigDecimal min = BigDecimal.valueOf(500);
    private BigDecimal max = BigDecimal.valueOf(10000);
    private Exchange exchange;

    Exchange getExchange() {
        return exchange;
    }

    void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    BigDecimal getPrice() {
        return price;
    }

    void setPrice(BigDecimal price) {
        this.price = price;
    }

    BigDecimal getMin() {
        return min;
    }

    void setMin(BigDecimal min) {
        this.min = min;
    }

    BigDecimal getMax() {
        return max;
    }

    void setMax(BigDecimal max) {
        this.max = max;
    }

    String breakCheck(Exchange exchange) throws IOException {
        setPrice(exchange.getMarketDataService().getTicker(BTC_USD).getLast());
        setMin(exchange.getMarketDataService().getTicker(BTC_USD).getLow());
        setMax(exchange.getMarketDataService().getTicker(BTC_USD).getHigh());
        setExchange(exchange);
        return breakCheck();
    }

    String breakCheck() throws IOException {
        if (price.compareTo(min) > 0 && price.compareTo(max) < 0) {
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
                return "OK";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return AlertBot.status(price, min, max);
    }
}