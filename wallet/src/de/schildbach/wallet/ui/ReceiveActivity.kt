package de.schildbach.wallet.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.schildbach.wallet.ui.receive.ReceiveDetailsDialog
import de.schildbach.wallet.ui.send.EnterAmountSharedViewModel
import de.schildbach.wallet_test.R
import org.bitcoinj.core.Coin
import org.xazab.wallet.common.InteractionAwareActivity

class ReceiveActivity : InteractionAwareActivity() {

    private lateinit var enterAmountSharedViewModel: EnterAmountSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, EnterAmountFragment.newInstance(Coin.ZERO))
                    .commitNow()
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        setTitle(R.string.receive_title)

        enterAmountSharedViewModel = ViewModelProviders.of(this).get(EnterAmountSharedViewModel::class.java)
        enterAmountSharedViewModel.maxButtonVisibleData.value = false
        enterAmountSharedViewModel.buttonTextData.call(R.string.receive_title)
        enterAmountSharedViewModel.messageTextData.value = R.string.receive_enter_amount_message
        enterAmountSharedViewModel.buttonClickEvent.observe(this, Observer {
            val xazabAmount = enterAmountSharedViewModel.xazabAmount
            val fiatAmount = enterAmountSharedViewModel.exchangeRate?.coinToFiat(xazabAmount)
            val dialog = ReceiveDetailsDialog.createDialog(xazabAmount, fiatAmount)
            dialog.show(supportFragmentManager, "ReceiveDetailsDialog")
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
