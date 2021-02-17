package de.schildbach.wallet.rates;

import androidx.annotation.Nullable;

import com.squareup.moshi.Moshi;

import java.util.List;

import retrofit2.Call;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

/**
 * @author Samuel Barbosa
 */
public class XazabRatesClient extends RetrofitClient implements ExchangeRatesClient {

    private static XazabRatesClient instance;

    public static XazabRatesClient getInstance() {
        if (instance == null) {
            instance = new XazabRatesClient();
        }
        return instance;
    }

    private XazabRatesService xazabRatesService;

    private XazabRatesClient() {
        super("https://api.get-spark.com/");
        Moshi moshi = moshiBuilder.add(new ExchangeRateListMoshiAdapter()).build();
        retrofit = retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        xazabRatesService = retrofit.create(XazabRatesService.class);
    }

    @Override
    @Nullable
    public List<ExchangeRate> getRates() throws Exception {
        List<ExchangeRate> rates = xazabRatesService.getRates().execute().body();
        if (rates == null || rates.isEmpty()) {
            throw new IllegalStateException("Failed to fetch prices from XazabRates source");
        }
        return rates;
    }

    private interface XazabRatesService {
        @GET("list")
        Call<List<ExchangeRate>> getRates();
    }

}
