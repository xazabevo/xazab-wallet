package de.schildbach.wallet.rates;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Samuel Barbosa
 */
public class XazabRatesFirstFallback implements ExchangeRatesClient {

    private static XazabRatesFirstFallback instance;
    private static final String VES_CURRENCY_CODE = "VES";

    public static XazabRatesFirstFallback getInstance() {
        if (instance == null) {
            instance = new XazabRatesFirstFallback();
        }
        return instance;
    }

    private XazabRatesFirstFallback() {

    }

    @Nullable
    @Override
    public List<ExchangeRate> getRates() throws Exception {
        BitcoinAverageClient btcAvgClient = BitcoinAverageClient.getInstance();
        CryptoCompareClient cryptoCompareClient = CryptoCompareClient.getInstance();

        List<ExchangeRate> rates = btcAvgClient.getGlobalIndices().body();
        Rate xazabBtcRate = cryptoCompareClient.getXazabCustomAverage().body();

        BigDecimal xazabVesPrice = XazabCasaClient.getInstance().getRates().body().getXazabVesPrice();

        if (rates == null || rates.isEmpty() || xazabBtcRate == null || xazabVesPrice == null) {
            throw new IllegalStateException("Failed to fetch prices from Fallback1");
        }

        boolean vesRateExists = false;
        for (ExchangeRate rate : rates) {
            if (VES_CURRENCY_CODE.equalsIgnoreCase(rate.getCurrencyCode())) {
                vesRateExists = true;
                if (xazabVesPrice.compareTo(BigDecimal.ZERO) > 0) {
                    rate.setRate(xazabVesPrice.toPlainString());
                }
            } else {
                BigDecimal currencyBtcRate = new BigDecimal(rate.getRate());
                rate.setRate(currencyBtcRate.multiply(xazabBtcRate.getRate()).toPlainString());
            }
        }
        if (!vesRateExists) {
            rates.add(new ExchangeRate(VES_CURRENCY_CODE, xazabVesPrice.toPlainString()));
        }

        return rates;
    }

}
