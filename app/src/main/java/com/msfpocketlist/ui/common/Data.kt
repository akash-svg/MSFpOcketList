package com.msfpocketlist.ui.common

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.msfpocketlist.common.Constant
import com.msfpocketlist.data.Employee
import com.msfpocketlist.data.EmployeeAll
import com.msfpocketlist.data.EmployeeEm
import com.msfpocketlist.data.EmployeeHq
import com.msfpocketlist.data.UserInfo

/** One shape for every kind of person the API returns (HQ, emergency, project staff, profile). */
data class Contact(
    val id: Int?,
    val name: String?,
    val title: String?,
    val subtitle: String?,
    val phone1: String?,
    val phone2: String?,
    val email: String?,
    val avatar: String? = null,
    val mission: String? = null,
) {
    val photoUrl get() = avatar?.let { Constant.IMAGE_PATH + it }

    fun matches(query: String) = listOf(name, title, subtitle, phone1, phone2, email)
        .any { it?.contains(query, ignoreCase = true) == true }
}

/** The API uses "null" and "-" for missing values. */
fun String?.clean(): String? = this?.trim()?.takeUnless { it.isEmpty() || it == "-" || it.equals("null", true) }

fun EmployeeHq.toContact() = Contact(id, fullName.clean(), desgTitle.clean(), deptTitle.clean(), mobileNo1.clean(), mobileNo2.clean(), emailId.clean(), avatar.clean(), missionTitle.clean())
fun EmployeeEm.toContact() = Contact(id, fullName.clean(), desgTitle.clean(), deptTitle.clean(), mobileNo1.clean(), mobileNo2.clean(), emailId.clean(), avatar.clean(), missionTitle.clean())
fun Employee.toContact() = Contact(id, fullName.clean(), designation.clean(), department.clean(), mobileNo1.clean(), mobileNo2.clean(), emailId.clean(), mission = missionTitle.clean())
fun EmployeeAll.toContact() = Contact(id, fullName.clean(), designation.clean(), department.clean(), mobileNo1.clean(), mobileNo2.clean(), emailId.clean(), avatar.clean(), missionTitle.clean())
fun UserInfo.Profile.toContact() = Contact(id, fullName.clean(), designation.clean(), department.clean(), mobileNo1.clean(), mobileNo2.clean(), emailId.clean(), avatar.clean(), missionTitle.clean())

class ContactActions(val call: (String) -> Unit, val sms: (String) -> Unit, val email: (String) -> Unit)

/** Direct call when CALL_PHONE is granted, otherwise falls back to the dialer. */
@Composable
fun rememberContactActions(): ContactActions {
    val context = LocalContext.current
    var pending by remember { mutableStateOf<String?>(null) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        pending?.let { context.dial(it, direct = granted) }
        pending = null
    }
    return remember(context) {
        ContactActions(
            call = { number ->
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    context.dial(number, direct = true)
                } else {
                    pending = number
                    permission.launch(Manifest.permission.CALL_PHONE)
                }
            },
            sms = { context.open(Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", it, null))) },
            email = { context.open(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$it"))) },
        )
    }
}

private fun Context.dial(number: String, direct: Boolean) =
    open(Intent(if (direct) Intent.ACTION_CALL else Intent.ACTION_DIAL, Uri.parse("tel:$number")))

private fun Context.open(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No app found to handle this action", Toast.LENGTH_SHORT).show()
    }
}
