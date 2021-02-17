package de.schildbach.wallet.util

import org.bitcoinj.utils.Fiat
import org.xazab.wallet.common.util.GenericUtils

val Fiat.currencySymbol: String
    get() = GenericUtils.currencySymbol(currencyCode)

