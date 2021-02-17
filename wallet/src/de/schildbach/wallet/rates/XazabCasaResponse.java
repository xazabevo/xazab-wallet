package de.schildbach.wallet.rates;

import com.squareup.moshi.Json;

import java.math.BigDecimal;

/**
 * @author Samuel Barbosa
 */
public class XazabCasaResponse {

    @Json(name = "xazabrate")
    private final BigDecimal xazabVesPrice;

    public XazabCasaResponse(BigDecimal xazabVesPrice) {
        this.xazabVesPrice = xazabVesPrice;
    }

    public BigDecimal getXazabVesPrice() {
        return xazabVesPrice;
    }

}
