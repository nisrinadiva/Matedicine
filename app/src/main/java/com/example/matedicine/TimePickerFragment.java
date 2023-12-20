package com.example.matedicine;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private DialogTimeListener mListener;

    public void setDialogTimeListener(DialogTimeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) {
            mListener = null;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar calendar = Calendar.getInstance();
        // Jam hari ini
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Menit dari jam hari ini
        int minute = calendar.get(Calendar.MINUTE);

        // True untuk format 24 jam, false untuk format 12 jam (am / pm)
        boolean formatHour24 = false;

        return new TimePickerDialog(getActivity(), this, hour, minute, formatHour24);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onDialogTimeSet(getTag(), hourOfDay, minute);
    }

    public interface DialogTimeListener {
        void onDialogTimeSet(String tag, int hourOfDay, int minute);
    }
}
