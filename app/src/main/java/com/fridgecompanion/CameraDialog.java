package com.fridgecompanion;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CameraDialog extends DialogFragment implements DialogInterface.OnClickListener {
    final String[] Options = {"Open Camera","Select from Gallery"};
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder window = new AlertDialog.Builder(getActivity());
        window.setTitle("Pick Profile Picture");
        window.setItems(Options, this);
        return window.create();
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        ((ItemEntryActivity) getActivity()).choosePhotoMethod(which);
    }
}
