package com.murgupluoglu.qrreader

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/*
*  Created by Mustafa Ürgüplüoğlu on 23.07.2020.
*  Copyright © 2020 Mustafa Ürgüplüoğlu. All rights reserved.
*/

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.murgupluoglu.qrreader.test", appContext.packageName)
    }
}