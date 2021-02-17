package de.schildbach.wallet.rates;

import androidx.annotation.Nullable;

import com.squareup.moshi.Moshi;

import org.xazab.wallet.common.data.BigDecimalAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

public class XazabRetailClient extends RetrofitClient implements ExchangeRatesClient {

    private static final String XAZAB_CURRENCY_SYMBOL = "XAZAB";

    private static XazabRetailClient instance;

    public static XazabRetailClient getInstance() {
        if (instance == null) {
            instance = new XazabRetailClient("https://rates.xazab.xyz/");
        }
        return instance;
    }

    private XazabRetailService service;

    private XazabRetailClient(String baseUrl) {
        super(baseUrl);

        Moshi moshi = moshiBuilder.add(new BigDecimalAdapter()).build();
        retrofit = retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        service = retrofit.create(XazabRetailService.class);
    }

    @Nullable
    @Override
    public List<ExchangeRate> getRates() throws Exception {
        Response<List<XazabRetailRate>> response = service.getRates().execute();
        List<XazabRetailRate> rates = response.body();

        if (rates == null || rates.isEmpty()) {
            throw new IllegalStateException("Failed to fetch prices from XazabRetail");
        }

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        for (XazabRetailRate rate : rates) {
            if (XAZAB_CURRENCY_SYMBOL.equals(rate.getBaseCurrency())) {
                exchangeRates.add(new ExchangeRate(rate.getQuoteCurrency(), rate.getPrice().toPlainString()));
            }
        }

        return exchangeRates;
    }

    private interface XazabRetailService {
        @GET("rates?source=xazabretail")
        Call<List<XazabRetailRate>> getRates();
    }

}
