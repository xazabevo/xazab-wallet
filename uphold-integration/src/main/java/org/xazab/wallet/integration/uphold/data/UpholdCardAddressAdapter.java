/*
 * Copyright 2015-present the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.xazab.wallet.integration.uphold.data;

import com.squareup.moshi.FromJson;

import java.util.Map;

public class UpholdCardAddressAdapter {

    @FromJson UpholdCardAddress fromJson(Map<String, String> json) {
        UpholdCardAddress cardAddress = new UpholdCardAddress();
        cardAddress.setWireId(json.get("wire"));
        cardAddress.setCryptoAddress(json.get("xazab"));
        return cardAddress;
    }

}