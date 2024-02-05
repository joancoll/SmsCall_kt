package cat.dam.andy.smscall_kt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import androidx.appcompat.app.AppCompatActivity

class SmsSentReceiver : BroadcastReceiver() {
    override fun onReceive(arg0: Context, arg1: Intent) {
            val resultCode = resultCode
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
//                    createToast(resources.getString(R.string.sms_send))
//                    etSms.setText("")
                }

                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {}
//                    createToast(
//                    resources.getString(R.string.sms_send)
//                )

                SmsManager.RESULT_ERROR_NO_SERVICE -> {}
////                createToast(
////                    resources.getString(R.string.sms_no_service)
////                )
//
                SmsManager.RESULT_ERROR_NULL_PDU ->{}
////                createToast(resources.getString(R.string.sms_null_PDU))
//                SmsManager.RESULT_ERROR_RADIO_OFF ->
////                createToast(resources.getString(R.string.sms_radio_off))
            }
        }
}