package de.schildbach.wallet.rates;

import com.squareup.moshi.Moshi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

/**
 * @author Samuel Barbosa
 */
public class XazabCentralClient extends RetrofitClient {

    private static XazabCentralClient instance;

    public static XazabCentralClient getInstance() {
        if (instance == null) {
            instance = new XazabCentralClient("https://central.xazab.xyz/");
        }
        return instance;
    }

    private XazabCentralService service;

    private XazabCentralClient(String baseUrl) {
        super(baseUrl);

        Moshi moshi = moshiBuilder.add(new XazabCentralRateAdapter()).build();
        retrofit = retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        service = retrofit.create(XazabCentralService.class);
    }

    public Response<Rate> getXazabBtcPrice() throws IOException {
        return service.getXazabBtcPrice().execute();
    }

    private interface XazabCentralService {
        @GET("api/v1/public")
        Call<Rate> getXazabBtcPrice();
    }

}
