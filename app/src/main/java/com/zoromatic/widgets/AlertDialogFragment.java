package com.zoromatic.widgets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

public class AlertDialogFragment extends DialogFragment {
    public AlertDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString("title");
        String message = args.getString("message");
        CharSequence[] items = args.getCharSequenceArray("items");
        Boolean editBox = args.getBoolean("editbox", false);
        Boolean locationDisabled = args.getBoolean("locationdisabled", false);
        Boolean deleteWidgets = args.getBoolean("deletewidgets", false);
        final EditText input = new EditText(getActivity());
        input.setId(R.id.text_id);

        String theme = Preferences.getMainTheme(getActivity());
        AlertDialog.Builder builder = null;

        if (items != null) {
            builder = new AlertDialog.Builder(getActivity(),
                    theme.compareToIgnoreCase("light") == 0 ? R.style.AppCompatAlertDialogStyleLight : R.style.AppCompatAlertDialogStyle)
                    .setTitle(title)
                    .setOnCancelListener(((ConfigureLocationActivity) getActivity()).dialogCancelListener)
                    .setOnKeyListener(((ConfigureLocationActivity) getActivity()).dialogKeyListener)
                    .setSingleChoiceItems(items, 0, ((ConfigureLocationActivity) getActivity()).dialogSelectLocationClickListener);
        } else if (editBox) {
            builder = new AlertDialog.Builder(getActivity(),
                    theme.compareToIgnoreCase("light") == 0 ? R.style.AppCompatAlertDialogStyleLight : R.style.AppCompatAlertDialogStyle)
                    .setTitle(getResources().getString(R.string.new_location))
                    .setMessage(getResources().getString(R.string.enter_location))
                    .setView(input)
                    .setPositiveButton(android.R.string.yes, ((ConfigureLocationActivity) getActivity()).dialogAddLocationClickListener)
                    .setNegativeButton(android.R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // Do nothing.
                                }
                            });
        } else if (locationDisabled) {
            builder = new AlertDialog.Builder(getActivity(),
                    theme.compareToIgnoreCase("light") == 0 ? R.style.AppCompatAlertDialogStyleLight : R.style.AppCompatAlertDialogStyle)
                    .setTitle(title)
                    .setMessage(message)
                    .setOnCancelListener(((ConfigureLocationActivity) getActivity()).dialogCancelListener)
                    .setOnKeyListener(((ConfigureLocationActivity) getActivity()).dialogKeyListener)
                    .setPositiveButton(android.R.string.yes, ((ConfigureLocationActivity) getActivity()).dialogLocationDisabledClickListener)
                    .setNegativeButton(android.R.string.no, ((ConfigureLocationActivity) getActivity()).dialogLocationDisabledClickListener);
        } else if (deleteWidgets) {
            builder = new AlertDialog.Builder(getActivity(),
                    theme.compareToIgnoreCase("light") == 0 ? R.style.AppCompatAlertDialogStyleLight : R.style.AppCompatAlertDialogStyle)
                    .setTitle(title)
                    .setMessage(message)
                    .setOnCancelListener(((ConfigureWidgetsActivity) getActivity()).dialogCancelListener)
                    .setOnKeyListener(((ConfigureWidgetsActivity) getActivity()).dialogKeyListener)
                    .setPositiveButton(android.R.string.yes, ((ConfigureWidgetsActivity) getActivity()).dialogDeleteWidgetClickListener)
                    .setNegativeButton(android.R.string.no, ((ConfigureWidgetsActivity) getActivity()).dialogDeleteWidgetClickListener);
        }

        AlertDialog dialog = builder.show();

        return dialog;
    }

    public void taskFinished() {
        // Make sure we check if it is resumed because we will crash if trying to dismiss the dialog
        // after the user has switched to another app.
        if (isResumed())
            dismiss();

        // Tell the fragment that we are done.
        //if (getTargetFragment() != null)
        //    getTargetFragment().onActivityResult(TASK_FRAGMENT, Activity.RESULT_OK, null);
    }
}
