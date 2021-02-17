package de.schildbach.wallet.rates;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Samuel Barbosa
 */
public class XazabRatesSecondFallback implements ExchangeRatesClient {

    private static XazabRatesSecondFallback instance;
    private static final String VES_CURRENCY_CODE = "VES";
    private List<String> excludedRates = Arrays.asList("BTC", "BCH", "XAG", "XAU", "VEF");

    public static XazabRatesSecondFallback getInstance() {
        if (instance == null) {
            instance = new XazabRatesSecondFallback();
        }
        return instance;
    }

    private XazabRatesSecondFallback() {

    }

    @Nullable
    @Override
    public List<ExchangeRate> getRates() throws Exception {
        List<BitPayRate> rates = BitPayClient.getInstance().getRates().body().getRates();
        BigDecimal xazabCentralPrice = XazabCentralClient.getInstance().getXazabBtcPrice().body().getRate();
        BigDecimal poloniexPrice = PoloniexClient.getInstance().getRates().body().getRate();
        BigDecimal xazabVesPrice = LocalBitcoinsClient.getInstance().getRates().body().getXazabVesPrice();

        if (rates == null || rates.isEmpty() || (xazabCentralPrice == null && poloniexPrice == null)) {
            throw new IllegalStateException("Failed to fetch prices from Fallback2");
        }

        BigDecimal xazabBtcRate = null;
        if (poloniexPrice.compareTo(BigDecimal.ZERO) > 0) {
            if (xazabCentralPrice.compareTo(BigDecimal.ZERO) > 0) {
                xazabBtcRate = xazabCentralPrice.add(poloniexPrice).divide(BigDecimal.valueOf(2));
            } else {
                xazabBtcRate = poloniexPrice;
            }
        } else if (xazabCentralPrice.compareTo(BigDecimal.ZERO) > 0) {
            xazabBtcRate = xazabCentralPrice;
        }

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        for(BitPayRate rate : rates) {
            if (!excludedRates.contains(rate.getCode())) {
                if (VES_CURRENCY_CODE.equalsIgnoreCase(rate.getCode()) && xazabVesPrice != null
                        && xazabVesPrice.compareTo(BigDecimal.ZERO) > 0) {
                    exchangeRates.add(new ExchangeRate(rate.getCode(), xazabVesPrice.toPlainString()));
                    continue;
                }
                exchangeRates.add(new ExchangeRate(rate.getCode(),
                        xazabBtcRate.multiply(rate.getRate()).toPlainString()));
            }
        }

        return exchangeRates;
    }

}
