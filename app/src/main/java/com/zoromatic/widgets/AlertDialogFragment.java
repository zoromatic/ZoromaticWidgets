package com.zoromatic.widgets;

import com.alertdialogpro.AlertDialogPro;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

public class AlertDialogFragment extends DialogFragment {
    public static final int TEXT_ID = 0x2906;

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
        input.setId(TEXT_ID);

        String theme = Preferences.getMainTheme(getActivity());
        AlertDialogPro.Builder builder = null;

        if (items != null) {
            builder = new AlertDialogPro.Builder(getActivity(),
                    theme.compareToIgnoreCase("light") == 0 ? R.style.Theme_AlertDialogPro_Material_Light : R.style.Theme_AlertDialogPro_Material)
                    .setTitle(title)
                    .setOnCancelListener(((ConfigureLocationActivity) getActivity()).dialogCancelListener)
                    .setOnKeyListener(((ConfigureLocationActivity) getActivity()).dialogKeyListener)
                    .setSingleChoiceItems(items, 0, ((ConfigureLocationActivity) getActivity()).dialogSelectLocationClickListener);
        } else if (editBox) {
            builder = new AlertDialogPro.Builder(getActivity(),
                    theme.compareToIgnoreCase("light") == 0 ? R.style.Theme_AlertDialogPro_Material_Light : R.style.Theme_AlertDialogPro_Material)
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
            builder = new AlertDialogPro.Builder(getActivity(),
                    theme.compareToIgnoreCase("light") == 0 ? R.style.Theme_AlertDialogPro_Material_Light : R.style.Theme_AlertDialogPro_Material)
                    .setTitle(title)
                    .setMessage(message)
                    .setOnCancelListener(((ConfigureLocationActivity) getActivity()).dialogCancelListener)
                    .setOnKeyListener(((ConfigureLocationActivity) getActivity()).dialogKeyListener)
                    .setPositiveButton(android.R.string.yes, ((ConfigureLocationActivity) getActivity()).dialogLocationDisabledClickListener)
                    .setNegativeButton(android.R.string.no, ((ConfigureLocationActivity) getActivity()).dialogLocationDisabledClickListener);
        } else if (deleteWidgets) {
            builder = new AlertDialogPro.Builder(getActivity(),
                    theme.compareToIgnoreCase("light") == 0 ? R.style.Theme_AlertDialogPro_Material_Light : R.style.Theme_AlertDialogPro_Material)
                    .setTitle(title)
                    .setMessage(message)
                    .setOnCancelListener(((ConfigureWidgetsActivity) getActivity()).dialogCancelListener)
                    .setOnKeyListener(((ConfigureWidgetsActivity) getActivity()).dialogKeyListener)
                    .setPositiveButton(android.R.string.yes, ((ConfigureWidgetsActivity) getActivity()).dialogDeleteWidgetClickListener)
                    .setNegativeButton(android.R.string.no, ((ConfigureWidgetsActivity) getActivity()).dialogDeleteWidgetClickListener);
        }

        AlertDialogPro dialog = builder.show();

        return dialog;
    }

    public void taskFinished() {
        // Make sure we check if it is resumed because we will crash if trying to dismiss the dialog
        // after the user has switched to another app.
        if (isResumed()) {
            dismiss();
        }

        // Tell the fragment that we are done.
        //if (getTargetFragment() != null)
        //    getTargetFragment().onActivityResult(TASK_FRAGMENT, Activity.RESULT_OK, null);
    }
}
