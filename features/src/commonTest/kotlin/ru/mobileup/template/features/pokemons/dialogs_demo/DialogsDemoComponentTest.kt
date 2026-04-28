package ru.mobileup.template.features.pokemons.dialogs_demo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import ru.mobileup.template.core_testing.utils.OutputCapturer
import ru.mobileup.template.features.pokemons.createDialogsDemoComponent
import ru.mobileup.template.features.pokemons.presentation.dialogs_demo.DialogsDemoComponent
import ru.mobileup.template.features.utils.integrationTest

class DialogsDemoComponentTest : FunSpec({

    context("Dialogs demo screen") {

        integrationTest("opens standard dialog when standard dialog is requested") {
            val component = setupComponent { createDialogsDemoComponent(it, {}) }

            component.onStandardDialogClick()
            runCurrent()

            component.standardDialogControl.dialogSlot.value.child shouldNotBe null
        }

        integrationTest("opens custom dialog when custom dialog is requested") {
            val component = setupComponent { createDialogsDemoComponent(it, {}) }

            component.onCustomDialogClick()
            runCurrent()

            component.customDialogControl.dialogSlot.value.child shouldNotBe null
        }

        integrationTest("opens non-dismissible bottom sheet with user dismissal disabled") {
            val component = setupComponent { createDialogsDemoComponent(it, {}) }

            component.onNonDismissibleBottomSheetClick()
            runCurrent()

            component.bottomSheetControl.dialogSlot.value.child shouldNotBe null
            component.bottomSheetControl.dismissableByUser.value shouldBe false
        }

        integrationTest("shows message over bottom sheet without closing it") {
            val component = setupComponent { createDialogsDemoComponent(it, {}) }

            component.onMessageBottomSheetClick()
            runCurrent()
            component.onShowMessageOverSheetClick()

            testMessageService.last shouldNotBe null
            component.bottomSheetControl.dialogSlot.value.child shouldNotBe null
        }

        integrationTest("emits back output when back is clicked") {
            val capturer = OutputCapturer<DialogsDemoComponent.Output>()
            val component = setupComponent { createDialogsDemoComponent(it, capturer) }

            component.onBackClick()

            capturer.last shouldBe DialogsDemoComponent.Output.BackRequested
        }
    }
})
