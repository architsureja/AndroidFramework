package com.archit.androidframework.main.viewmodel

import com.archit.androidframework.base.BaseActionListener
import com.archit.androidframework.util.MockitoKotlinTestHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Created on 8/28/20.
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(
    MainDashboardViewModel::class
)
class MainDashboardViewModelTest : MockitoKotlinTestHelper() {
    private lateinit var subject: MainDashboardViewModel

    @Mock
    private lateinit var mockedBaseActionListener: BaseActionListener<MainDashboardViewModel.Action>

    @Before
    fun setUp() {
        subject = spy(MainDashboardViewModel())
        subject.actionPerformer = mockedBaseActionListener
    }

    @Test
    fun `test pickerFeatureClicked`() {
        subject.pickerFeatureClicked()
        verify(mockedBaseActionListener).performAction(MainDashboardViewModel.Action.START_PICKER_FLOW)
    }

    @Test
    fun `test stackingViewHolderFeatureClicked`() {
        subject.stackingViewHolderFeatureClicked()
        verify(mockedBaseActionListener).performAction(MainDashboardViewModel.Action.START_STACKING_VIEW_HOLDER_FLOW)
    }
}