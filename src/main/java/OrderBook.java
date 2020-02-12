import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderBook implements Runnable {
    private MarketDataService marketDataService;
    private org.knowm.xchange.dto.marketdata.OrderBook orderBook;

    //Constructor
    OrderBook(Exchange exchange) throws IOException {
        this.marketDataService = exchange.getMarketDataService();
        this.orderBook = marketDataService.getOrderBook(CurrencyPair.BTC_USD);

    }

    @Override
    public void run() {
        try {
            generateOrderbook();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateOrderbook() throws IOException {

        BigDecimal price;
        int one = 0;
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Order Book | Current Price: ").xAxisTitle("USD").yAxisTitle("BTC").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        JFrame sw = new SwingWrapper(chart).displayChart();
        updateOrderBook(sw, chart, marketDataService);
        one++;
        while (one > 0) {
            try {
                Thread.sleep(300);
                price = marketDataService.getTicker(CurrencyPair.BTC_USD).getLast();
                chart.setTitle("Order Book | Current Price: " + price);
                updateOrderBook(sw, chart, marketDataService);
                one++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOrderBook(JFrame sw, XYChart chart, MarketDataService marketDataService) throws IOException {
        chart.removeSeries("asks");
        chart.removeSeries("bids");

        double currentPrice = marketDataService.getTicker(CurrencyPair.BTC_USD).getLast().doubleValue();

        // BIDS
        List<Number> xData = new ArrayList<>();
        List<Number> yData = new ArrayList<>();
        BigDecimal accumulatedBidUnits = new BigDecimal("0");
        for (LimitOrder limitOrder : orderBook.getBids()) {
            if (limitOrder.getLimitPrice().doubleValue() > (currentPrice * .95)) {
                xData.add(limitOrder.getLimitPrice());
                accumulatedBidUnits = accumulatedBidUnits.add(limitOrder.getRemainingAmount());
                yData.add(accumulatedBidUnits);
            }
        }
        Collections.reverse(xData);
        Collections.reverse(yData);
        // Bids
        XYSeries series = chart.addSeries("bids", xData, yData);
        series.setMarker(SeriesMarkers.NONE);
        // ASKS
        xData = new ArrayList<>();
        yData = new ArrayList<>();
        BigDecimal accumulatedAskUnits = new BigDecimal("0");
        for (LimitOrder limitOrder : orderBook.getAsks()) {
            if (limitOrder.getLimitPrice().doubleValue() < currentPrice * 1.05) {
                xData.add(limitOrder.getLimitPrice());
                accumulatedAskUnits = accumulatedAskUnits.add(limitOrder.getRemainingAmount());
                yData.add(accumulatedAskUnits);
            }
        }
        // Asks Series
        series = chart.addSeries("asks", xData, yData);
        series.setMarker(SeriesMarkers.NONE);
        sw.repaint();
    }
}
