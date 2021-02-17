package de.schildbach.wallet.rates;

import com.squareup.moshi.Moshi;

import org.xazab.wallet.common.data.BigDecimalAdapter;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

/**
 * @author Samuel Barbosa
 */
public class XazabCasaClient extends RetrofitClient {

    private static XazabCasaClient instance;
    private XazabCasaService service;

    public static XazabCasaClient getInstance() {
        if (instance == null) {
            instance = new XazabCasaClient("https://xazab.casa/");
        }
        return instance;
    }

    private XazabCasaClient(String baseUrl) {
        super(baseUrl);

        Moshi moshi = moshiBuilder.add(new BigDecimalAdapter()).build();
        retrofit = retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        service = retrofit.create(XazabCasaService.class);
    }

    public Response<XazabCasaResponse> getRates() throws IOException {
        return service.getRates().execute();
    }

    private interface XazabCasaService {
        @GET("api/?cur=VES")
        Call<XazabCasaResponse> getRates();
    }

}
