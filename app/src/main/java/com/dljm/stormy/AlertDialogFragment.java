package com.dljm.stormy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Bundle args = getArguments() == null ? Bundle.EMPTY : getArguments();
        final Context context = getActivity();
        //get the message content set in MainActivity AlertUserAboutError(string text)
        String message = args.getString("message_key");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //set the alert fragment content
        builder.setTitle(getString(R.string.error_title))
        .setMessage(message)
        .setPositiveButton(getString(R.string.error_button_ok), null);

        return builder.create();
    }
}
