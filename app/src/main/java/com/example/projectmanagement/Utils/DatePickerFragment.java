package com.example.projectmanagement.Utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.projectmanagement.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DatePickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Bundle viewIdBundle = new Bundle();
    private int viewId;
    private String DateAndTimePickerResultKey = "DateAndTimePickerResultKey";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatePickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatePickerFragment newInstance(String param1, String param2) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();
        int yr = c.get(Calendar.YEAR);
        int mon = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        viewIdBundle = getArguments();
        viewId = viewIdBundle.getInt("datePickerType");
        return new DatePickerDialog(getActivity(), this, yr, mon, day);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Bundle result = new Bundle();
        result.putInt("resultYear", i);
        result.putInt("resultMonth", i1);
        result.putInt("resultDayOfMonth", i2);
        result.putString("resultType", "date");
        result.putInt("viewId", viewId);
        getParentFragmentManager().setFragmentResult(DateAndTimePickerResultKey, result);
    }
}