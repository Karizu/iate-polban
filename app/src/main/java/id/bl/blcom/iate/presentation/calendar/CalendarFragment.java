package id.bl.blcom.iate.presentation.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.calendarview.CalendarView;
import com.rezkyatinnov.kyandroid.localdata.LocalData;
import com.rezkyatinnov.kyandroid.reztrofit.ErrorResponse;
import com.rezkyatinnov.kyandroid.reztrofit.RestCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.EventHelper;
import id.bl.blcom.iate.models.ApiResponse;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.models.response.EventCalendarResponse;
import id.bl.blcom.iate.presentation.event.EventActivity;
import io.realm.Realm;
import okhttp3.Headers;

public class CalendarFragment extends Fragment implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
CalendarView.OnMonthChangeListener{

    @BindView(R.id.event_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.calendarView)
    CalendarView mCalendarView;
    @BindView(R.id.tvPrevMonth)
    TextView tvPrevMonth;
    @BindView(R.id.tvCurrMonth)
    TextView tvCurrMonth;
    @BindView(R.id.tvNextMonth)
    TextView tvNextMonth;

    private EventListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Calendar activeDate;
    private String groupId;
    private String groupName;
    private String dateSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        EventTitleItemDecoration sectionItemDecoration =
                new EventTitleItemDecoration(getResources().getDimensionPixelSize(R.dimen.event_header_height),
                        true,
                        getSectionCallback());
        mRecyclerView.addItemDecoration(sectionItemDecoration);

        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);

        Realm realm = LocalData.getRealm();
        Profile profile = realm.where(Profile.class).findFirst();
        groupId = profile.getGroupId();
        groupName = profile.getGroup().getName();

        Date date = new Date();
        activeDate = Calendar.getInstance();
        activeDate.setTime(date);
        initCalendarData();
        updateCalendarViewData();

        // specify an adapter (see also next example)
        return view;
    }

    private void updateCalendarViewData(){
        try {
            if (getArguments().getString("groupId") != null) {
                groupName = getArguments().getString("groupName");
                EventHelper.getEventsFilter(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH) + 1, getArguments().getString("groupId"), new RestCallback<ApiResponse<List<EventCalendarResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<EventCalendarResponse>> body) {
                        try {
                            setCalendarScheme(body.getData());
                            mAdapter = new EventListAdapter(body.getData());
                            ((EventListAdapter) mAdapter).onEventItemClicked = (eventId) -> {
                                Intent myIntent = new Intent(getActivity(), EventActivity.class);
                                myIntent.putExtra("EVENT_ID", eventId);
                                startActivity(myIntent);
                            };
                            Calendar current = activeDate;
//                            tvEventListHeader.setText("EVENT "
//                                    + new SimpleDateFormat("MMMM", Locale.getDefault()).format(current.getTime()).toUpperCase()
//                                    + " " + current.get(Calendar.YEAR)+ "\n" +groupName);
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {

                    }

                    @Override
                    public void onCanceled() {

                    }
                });
            } else {
                EventHelper.getEventsFilter(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH) + 1, groupId, new RestCallback<ApiResponse<List<EventCalendarResponse>>>() {
                    @Override
                    public void onSuccess(Headers headers, ApiResponse<List<EventCalendarResponse>> body) {
                        try {
                            setCalendarScheme(body.getData());
                            mAdapter = new EventListAdapter(body.getData());
                            ((EventListAdapter) mAdapter).onEventItemClicked = (eventId) -> {
                                Intent myIntent = new Intent(getActivity(), EventActivity.class);
                                myIntent.putExtra("EVENT_ID", eventId);
                                startActivity(myIntent);
                            };
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFailed(ErrorResponse error) {

                    }

                    @Override
                    public void onCanceled() {

                    }
                });
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    private void initCalendarData() {
        Calendar current = activeDate;

        Calendar nextMo = Calendar.getInstance();
        nextMo.setTime(activeDate.getTime());
        nextMo.add(Calendar.MONTH, 1);

        Calendar prevMo = Calendar.getInstance();
        prevMo.setTime(activeDate.getTime());
        prevMo.add(Calendar.MONTH, -1);
        tvCurrMonth.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(current.getTime()).toUpperCase());
        tvPrevMonth.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(prevMo.getTime()).toUpperCase());
        tvNextMonth.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(nextMo.getTime()).toUpperCase());
//        tvEventListHeader.setText("EVENT "
//                + new SimpleDateFormat("MMMM", Locale.getDefault()).format(current.getTime()).toUpperCase()
//                + " " + current.get(Calendar.YEAR)+ "\n" +groupName);

    }

    protected void setCalendarScheme(List<EventCalendarResponse> data) {
        int year = activeDate.get(Calendar.YEAR);
        int month = activeDate.get(Calendar.MONTH)+1;

        Map<String, com.haibin.calendarview.Calendar> map = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (EventCalendarResponse event : data) {
            try {
                Date date = format.parse(event.getDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int day = cal.get(Calendar.DATE);
                map.put(getSchemeCalendar(year, month, day, 0xFFFF0000, String.valueOf(event.getTotal())).toString(),
                        getSchemeCalendar(year, month, day, 0xFFFF0000, String.valueOf(event.getTotal())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mCalendarView.setSchemeDate(map);
    }

    private com.haibin.calendarview.Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new com.haibin.calendarview.Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
        if (mAdapter != null) {
            if (isClick){
                Date date = new Date(calendar.getTimeInMillis());
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                dateSelected = sdf.format(date);

//                tvEventListHeader.setText("EVENT "+ dateSelected+ "\n" +groupName);
                ((EventListAdapter) mAdapter).updateData(calendar);
            }
        }
    }

    @Override
    public void onMonthChange(int year, int month) {
        activeDate.set(year, month-1, 1);

        initCalendarData();
        updateCalendarViewData();
    }

    @OnClick(R.id.tvNextMonth)
    public void onTvNextMonthClicked(){
//        activeDate.set(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH)+1, 1);

        mCalendarView.scrollToNext();
    }

    @OnClick(R.id.tvPrevMonth)
    public void onTvPrevMonthClicked(){
//        activeDate.set(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH)-1, 1);
//
//        initCalendarData();
//        updateCalendarViewData();
        mCalendarView.scrollToPre();
    }

    @Override
    public void onYearChange(int year) {

    }

    private EventTitleItemDecoration.SectionCallback getSectionCallback() {
        return new EventTitleItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0;
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                Calendar current = activeDate;
                if (dateSelected != null){
                    return "EVENT "+ dateSelected+ "\n" +groupName;
                }
                else{
                    return "EVENT "
                            + new SimpleDateFormat("MMMM", Locale.getDefault()).format(current.getTime()).toUpperCase()
                            + " " + current.get(Calendar.YEAR)+ "\n" +groupName;
                }
            }
        };
    }
}
