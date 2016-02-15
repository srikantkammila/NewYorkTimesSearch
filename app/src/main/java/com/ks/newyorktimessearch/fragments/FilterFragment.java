package com.ks.newyorktimessearch.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.ks.newyorktimessearch.R;
import com.ks.newyorktimessearch.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterFragment extends DialogFragment {

    @Bind(R.id.etBeginDate) EditText bgnDt;
    @Bind(R.id.radio_latest) RadioButton latestRdBtn;
    @Bind(R.id.radio_oldest) RadioButton oldestRdBtn;
    @Bind(R.id.cbArts) CheckBox artsCb;
    @Bind(R.id.cbSports) CheckBox sportsCb;
    @Bind(R.id.cbFashin)CheckBox fashionCb;
    Calendar myCalendar = Calendar.getInstance();

    public FilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterFragment newInstance(String title) {
        FilterFragment frag = new FilterFragment();
//        Bundle args = new Bundle();
//        args.putString("title", title);
//        frag.setArguments(args);
        frag.setStyle(STYLE_NO_TITLE, 0);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container);
        ButterKnife.bind(this, view);

        if (((MainActivity)this.getActivity()).savedFilter.size() > 0) {
            //reset date back to view
            Map<String, Object> savedFilter = ((MainActivity)this.getActivity()).savedFilter;
            Set<String> keys = savedFilter.keySet();
            Iterator<String> itr = keys.iterator();
            while(itr.hasNext()) {
                String key = itr.next();
                Object value = savedFilter.get(key);
                switch (Integer.parseInt(key)) {
                    case R.id.radio_latest:
                        if (value  instanceof Boolean){
                            latestRdBtn.setChecked((Boolean)value);
                        }
                        break;
                    case R.id.radio_oldest:
                        if (value  instanceof Boolean){
                            oldestRdBtn.setChecked((Boolean) value);
                        }
                        break;
                    case R.id.cbArts:
                        if (value  instanceof Boolean){
                            artsCb.setChecked((Boolean) value);
                        }
                        break;
                    case R.id.cbFashin:
                        if (value  instanceof Boolean){
                            fashionCb.setChecked((Boolean) value);
                        }
                        break;
                    case R.id.cbSports:
                        if (value  instanceof Boolean){
                            sportsCb.setChecked((Boolean)value);
                        }
                        break;
                    case R.id.etBeginDate:
                        if (value  instanceof Long && ((Long) value) > 0){
                            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
                            String displayDate = (Long)value > 0 ? ft.format(new Date((Long)value)) : "";
                            if (displayDate.length() > 0) {
                                bgnDt.setText(displayDate);
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        bgnDt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatepicker(v);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        // Fetch arguments from bundle and set title
//        String title = getArguments().getString("title", "Enter Name");
//        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void showDatepicker(View v) {

        Date dt = new Date();
        Map<String, Object> filter = ((MainActivity) (this.getActivity())).savedFilter;
        Long begindt = (Long)filter.get(Integer.toString(R.id.etBeginDate));
        if (begindt != null && begindt > 0) {
            dt.setTime(begindt);
        }
        myCalendar.setTime(dt);

        final View dateTimePicker = View.inflate(this.getContext(), R.layout.begin_date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();

        dateTimePicker.findViewById(R.id.clearDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateDueDate(0);
                alertDialog.dismiss();
            }
        });

        dateTimePicker.findViewById(R.id.setDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.dpBeginDate);

                myCalendar.set(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth());

                long time = myCalendar.getTimeInMillis();
                updateDueDate(time);
                alertDialog.dismiss();
            }
        });
        ((DatePicker)dateTimePicker.findViewById(R.id.dpBeginDate)).updateDate(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        alertDialog.setView(dateTimePicker);
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }

    private void updateDueDate(long time) {
        Map<String, Object> filter = ((MainActivity) (this.getActivity())).filter;
        filter.put(Integer.toString(R.id.etBeginDate), time);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
        String displayDate = time > 0 ? ft.format(new Date(time)) : "";
        bgnDt.setText(displayDate);

    }
}
