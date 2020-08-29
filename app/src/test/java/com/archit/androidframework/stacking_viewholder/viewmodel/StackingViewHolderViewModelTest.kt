package com.archit.androidframework.stacking_viewholder.viewmodel

import com.archit.androidframework.base.BaseActionListener
import com.archit.androidframework.util.CoroutineHelper
import com.archit.androidframework.util.CoroutineTestHelper
import com.archit.androidframework.util.MockitoKotlinTestHelper
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.random.Random

/**
 * Created on 8/29/20.
 */

@RunWith(PowerMockRunner::class)
@PrepareForTest(
    StackingViewHolderViewModel::class,
    Random::class,
    Random.Default::class
)
class StackingViewHolderViewModelTest : MockitoKotlinTestHelper() {
    private lateinit var subject: StackingViewHolderViewModel
    private val coroutineHelper = CoroutineTestHelper()

    @Mock
    private lateinit var mockedBaseActionListener: BaseActionListener<StackingViewHolderViewModel.Action>

    @Mock
    private lateinit var mockedRandom: Random.Default

    @Before
    fun setUp() {
        PowerMockito.mockStatic(Random::class.java)
        PowerMockito.field(Random::class.java, "Default").set(mockedRandom, mockedRandom)
        subject = Mockito.spy(StackingViewHolderViewModel(coroutineHelper))
        subject.actionPerformer = mockedBaseActionListener
    }

    @Test
    fun `test getNewData success response`() = coroutineHelper.testScope.runBlockingTest {
        val data = listOf("asd1", "asd2", "asd3", "asd4", "asd5", "asd6")
        given(mockedRandom.nextBoolean()).willReturn(true)
        subject.getNewData()
        advanceTimeBy(3100L)
        Mockito.verify(mockedBaseActionListener)
            .performAction(StackingViewHolderViewModel.Action.LOAD_DATA, data)
    }

    @Test
    fun `test getNewData fail response`() = coroutineHelper.testScope.runBlockingTest {
        given(mockedRandom.nextBoolean()).willReturn(false)
        subject.getNewData()
        advanceTimeBy(3100L)
        Mockito.verify(mockedBaseActionListener)
            .performAction(StackingViewHolderViewModel.Action.LOAD_DATA_FAIL, null)
    }
}