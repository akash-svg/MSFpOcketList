package com.msfpocketlist

import com.msfpocketlist.ui.common.Contact
import com.msfpocketlist.ui.common.clean
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactTest {
    @Test
    fun cleanDropsApiPlaceholders() {
        assertNull(null.clean())
        assertNull("null".clean())
        assertNull("NULL".clean())
        assertNull(" - ".clean())
        assertNull("".clean())
        assertEquals("01711", " 01711 ".clean())
    }

    @Test
    fun matchesIsCaseInsensitiveAcrossFields() {
        val c = Contact(1, "Ayesha Rahman", "Medical Coordinator", "Medical", "01711000000", null, "ayesha@msf.org")
        assertTrue(c.matches("ayesha"))
        assertTrue(c.matches("COORDINATOR"))
        assertTrue(c.matches("1711"))
        assertTrue(c.matches("MSF.ORG"))
        assertFalse(c.matches("logistics"))
    }
}
