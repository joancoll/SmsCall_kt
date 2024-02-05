package cat.dam.andy.smscall_kt

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var etSms: EditText
    private lateinit var tvPhone: TextView
    private lateinit var btnSms: Button
    private lateinit var btnCall: Button
    private lateinit var btnDelete: Button
    private val permissionManager = PermissionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initPermissions()
        initListeners()
    }

    private fun initViews() {
        etSms = findViewById(R.id.et_sms)
        tvPhone = findViewById(R.id.tv_phone)
        btnSms = findViewById(R.id.btn_send_sms)
        btnCall = findViewById(R.id.btn_send_call)
        btnDelete = findViewById(R.id.btn_delete_digit)
    }

    private fun initPermissions() {
        permissionManager.addPermission(
            Manifest.permission.CALL_PHONE,
            getString(R.string.callPermissionInfo),
            getString(R.string.callPermissionNeeded),
            getString(R.string.callPermissionDenied),
            getString(R.string.callPermissionThanks),
            getString(R.string.callPermissionSettings)
        )
        permissionManager.addPermission(
            Manifest.permission.SEND_SMS,
            getString(R.string.smsPermissionInfo),
            getString(R.string.smsPermissionNeeded),
            getString(R.string.smsPermissionDenied),
            getString(R.string.smsPermissionThanks),
            getString(R.string.smsPermissionSettings)
        )
    }

    private fun initListeners() {
        btnSms.setOnClickListener {
            if (!permissionManager.hasPermission(Manifest.permission.SEND_SMS)) {
                permissionManager.askForThisPermission(Manifest.permission.SEND_SMS)
            } else {
                    it.hideKeyboard()
                    handleSms()
                }
            }

        btnCall.setOnClickListener {
            if (!permissionManager.hasPermission(Manifest.permission.CALL_PHONE)) {
                permissionManager.askForThisPermission(Manifest.permission.CALL_PHONE)
            } else {
                handleCall()
            }
        }

        btnDelete.setOnClickListener {
            if (tvPhone.length() != 0) {
                tvPhone.text = tvPhone.text.toString().substring(0, tvPhone.length() - 1)
            }
        }
    }

    private fun handleSms() {
        val message = etSms.text.toString()
        val phone_number = tvPhone.text.toString()
        if (isValidSms(message) && isValidPhoneNumber(phone_number)) {
            sendSms(phone_number, message)
        } else {
            if (!isValidPhoneNumber(phone_number)) {
                showToast(resources.getString(R.string.phone_wrong_format))
            } else {
                showToast(resources.getString(R.string.sms_wrong_format))
            }
        }
    }


    private fun sendSms(phoneNumber: String, smsText: String) {
        try {
            val smsManager:SmsManager
            if (Build.VERSION.SDK_INT>=31) {
                //if SDK is greater that or equal to 31 then
                //this is how we will initialize the SmsManager
                smsManager = this.getSystemService(SmsManager::class.java)
            }
            else{
                //if user's SDK is less than 31 then
                //SmsManager will be initialized like this
                smsManager = SmsManager.getDefault()
            }
            // calling send text message
            smsManager.sendTextMessage(phoneNumber, null, smsText, null, null)
            showToast(resources.getString(R.string.message_sent))
        } catch (e: Exception) {
            showToast(
                """
                ${e.message}!
                ${R.string.sms_failed_to_send}
                """.trimIndent()
            )
            e.printStackTrace()
        }
    }

    private fun handleCall() {
        val phone_number_pattern = tvPhone.text.toString()
        if (isValidPhoneNumber(phone_number_pattern)) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone_number_pattern"))
            startActivity(intent)
        } else {
            showToast(resources.getString(R.string.phone_wrong_format))
        }
    }

    private fun isValidPhoneNumber(phone_number: String): Boolean {
        val pattern = Pattern.compile(PHONE_NUMBER_PATTERN)
        return pattern.matcher(phone_number).matches()
    }

    private fun isValidSms(sms: String): Boolean {
        val pattern = Pattern.compile(SMS_PATTERN)
        return pattern.matcher(sms).matches()
    }

    fun onClickNumbers(view: View) {
        val digit: Int
        if (tvPhone.length() < MAX_PHONE_LENGTH) {
            digit = indexOf(view.id)
            val phone = StringBuilder(tvPhone.text.toString())
            phone.append(digit)
            tvPhone.text = phone.toString()
        }
    }

    private fun indexOf(viewId: Int): Int {
        var i = 0
        var found = false
        while (i < btnNumbers.size && !found) {
            if (btnNumbers[i] == viewId) found = true else i++
        }
        return if (i == btnNumbers.size) -1 else i
    }

    private fun showToast(text: String) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    companion object {
        private const val MAX_PHONE_LENGTH = 18
        private const val MAX_SMS_LENGTH = 160
        // Aquesta expressió regular valida números de telèfon espanyols amb o sense el signe de més inicial
        // El primer dígit del número de telèfon ha de ser 6, 7, 8 o 9.
        // Accepta exactament 8 dígits (0-9) després del primer dígit.
        // i pot gestionar diferents formats comuns. Pots adaptar-la segons les teves necessitats específiques.
        private const val PHONE_NUMBER_PATTERN = "^(\\+34|0034|34)?[6789]\\d{8}\$"
        //Aquesta expressió regular assegura que el missatge de SMS estigui format
        //per una combinació de lletres, dígits, espais en blanc i caràcters de puntuació,
        // i que la seva longitud estigui dins del rang permès.
        private const val SMS_PATTERN = "^[\\p{L}0-9\\s\\p{Punct}]{1,$MAX_SMS_LENGTH}$"
        private val btnNumbers = intArrayOf(
            R.id.btn_num_0, R.id.btn_num_1, R.id.btn_num_2, R.id.btn_num_3, R.id.btn_num_4,
            R.id.btn_num_5, R.id.btn_num_6, R.id.btn_num_7, R.id.btn_num_8, R.id.btn_num_9
        )
    }
}