package app.screenreader.tabs.gestures

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import app.screenreader.R
import app.screenreader.adapters.headerAdapterDelegate
import app.screenreader.adapters.textResourceAdapterDelegate
import app.screenreader.adapters.trainingAdapterDelegate
import app.screenreader.extensions.*
import app.screenreader.helpers.Accessibility
import app.screenreader.helpers.Preferences
import app.screenreader.model.Gesture
import app.screenreader.model.Header
import app.screenreader.services.ScreenReaderService
import app.screenreader.widgets.ListFragment
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class GesturesFragment : ListFragment() {

    override val items = listOf(
        R.string.gestures_description,
        Header(R.string.gestures_one_finger_swipe),
        Gesture.ONE_FINGER_TOUCH,
        Gesture.ONE_FINGER_SWIPE_RIGHT,
        Gesture.ONE_FINGER_SWIPE_LEFT,
        Gesture.ONE_FINGER_SWIPE_UP,
        Gesture.ONE_FINGER_SWIPE_DOWN,
        Header(R.string.gestures_two_fingers_swipe),
        Gesture.TWO_FINGER_SWIPE_UP,
        Gesture.TWO_FINGER_SWIPE_DOWN,
        Gesture.TWO_FINGER_SWIPE_RIGHT,
        Gesture.TWO_FINGER_SWIPE_LEFT,
        Header(R.string.gestures_three_fingers_swipe),
        Gesture.THREE_FINGER_SWIPE_UP,
        Gesture.THREE_FINGER_SWIPE_DOWN,
        Header(R.string.gestures_one_finger_tap),
        Gesture.ONE_FINGER_DOUBLE_TAP,
        Gesture.ONE_FINGER_DOUBLE_TAP_HOLD,
        Header( R.string.gestures_two_fingers_tap),
        Gesture.TWO_FINGER_TAP,
        Gesture.TWO_FINGER_DOUBLE_TAP,
        Gesture.TWO_FINGER_DOUBLE_TAP_HOLD,
        Gesture.TWO_FINGER_TRIPLE_TAP,
        Header(R.string.gestures_three_fingers_tap),
        Gesture.THREE_FINGER_TAP,
        Gesture.THREE_FINGER_DOUBLE_TAP,
        Gesture.THREE_FINGER_DOUBLE_TAP_HOLD,
        Gesture.THREE_FINGER_TRIPLE_TAP,
        Header(R.string.gestures_four_fingers_tap),
        Gesture.FOUR_FINGER_TAP,
        Gesture.FOUR_FINGER_DOUBLE_TAP,
        Gesture.FOUR_FINGER_DOUBLE_TAP_HOLD,
        Header(R.string.gestures_shortcuts),
        Gesture.ONE_FINGER_SWIPE_DOWN_THEN_RIGHT,
        Gesture.ONE_FINGER_SWIPE_DOWN_THEN_LEFT,
        Gesture.ONE_FINGER_SWIPE_UP_THEN_LEFT,
        Gesture.ONE_FINGER_SWIPE_LEFT_THEN_UP,
        Gesture.ONE_FINGER_SWIPE_RIGHT_THEN_DOWN,
        Gesture.ONE_FINGER_SWIPE_LEFT_THEN_DOWN,
        Gesture.ONE_FINGER_SWIPE_RIGHT_THEN_LEFT,
        Gesture.ONE_FINGER_SWIPE_LEFT_THEN_RIGHT,
        Gesture.ONE_FINGER_SWIPE_UP_THEN_DOWN,
        Gesture.ONE_FINGER_SWIPE_DOWN_THEN_UP,
    )

    override val adapter = ListDelegationAdapter(
        textResourceAdapterDelegate(),
        headerAdapterDelegate(),
        trainingAdapterDelegate<Gesture> { gesture ->
            onGestureClicked(gesture)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.practice, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_practice) {
            onPracticeClicked()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            adapter.notifyDataSetChanged()
            activity?.requestReview()
        }
    }

    private fun onGestureClicked(gesture: Gesture) {
        if (Accessibility.screenReader(context)) {
            context?.showDialog(R.string.service_talkback_enabled_title, R.string.service_talkback_enabled_message)
            return
        }

        startActivity<GestureActivity>(REQUEST_CODE_SINGLE) {
            setGesture(gesture)
        }
    }

    private fun onPracticeClicked() {
        if (Accessibility.screenReader(context)) {
            context?.showDialog(R.string.service_talkback_enabled_title, R.string.service_talkback_enabled_message)
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle(context?.getSpannable(R.string.gestures_practice_title))
            .setMessage(context?.getSpannable(R.string.gestures_practice_message))
            .setPositiveButton(context?.getSpannable(R.string.gestures_practice_with_instructions)) { _, _ ->
                startPractice(true)
            }
            .setNegativeButton(context?.getSpannable(R.string.gestures_practice_without_instructions)) { _, _ ->
                startPractice(false)
            }
            .setNeutralButton(context?.getSpannable(R.string.action_cancel)) { _, _ ->
                // Cancels dialog
            }
            .show()
    }

    private fun startPractice(instructions: Boolean) {
        val context = this.context ?: return

        if (Accessibility.screenReader(context)) {
            if (!ScreenReaderService.isEnabled(context)) {
                ScreenReaderService.enable(context, instructions)
                return
            }
        }

        val gestures = Gesture.randomized()

        startActivity<GestureActivity>(REQUEST_CODE_MULTIPLE) {
            setGestures(gestures)
            setInstructions(instructions)
        }
    }

    companion object {
        private const val REQUEST_CODE_SINGLE = 1
        private const val REQUEST_CODE_MULTIPLE = 2
    }
}