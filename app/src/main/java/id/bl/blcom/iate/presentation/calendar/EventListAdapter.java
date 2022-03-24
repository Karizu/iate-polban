package id.bl.blcom.iate.presentation.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.models.response.EventCalendarResponse;
import id.bl.blcom.iate.models.response.EventDataResponse;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private static final int TYPE_EMPTY = 1021;
    private static final int TYPE_ITEM = 1011;
    private List<EventCalendarResponse> mDataset;
    private List<EventDataResponse> mEventData;
    private List<EventDataResponse> mEventTemp;
    private Context mContext;
    public OnEventItemClicked onEventItemClicked;

    public void updateData(com.haibin.calendarview.Calendar calendar) {
        List<EventDataResponse> newEventData = new ArrayList<>();
        Date date = new Date(calendar.getTimeInMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateFilter = sdf.format(date);

        for (EventDataResponse event: mEventTemp){
            if (event.getDate() != null){
                if (event.getDate().equals(dateFilter)){
                    newEventData.add(event);
                }
            }
        }

        mEventData = new ArrayList<>();
        mEventData.addAll(newEventData);
        if (mEventData.size() == 0){
            EventDataResponse emptyEvent = new EventDataResponse();
            emptyEvent.setDescription("--EMPTY--");
            mEventData.add(emptyEvent);
        } else {
            if (mEventData.get(0).getDescription().equalsIgnoreCase("--EMPTY--")){
                mEventData.remove(0);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventTitle;
        public TextView tvEventTime;
        public TextView tvEventDate;
        public TextView tvEventAddress;
        public ImageView ivEventDetail;
        public ViewHolder(View v) {
            super(v);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvEventAddress = itemView.findViewById(R.id.tvEventAddress);
            ivEventDetail = itemView.findViewById(R.id.ivEventDetail);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
        }
    }

    public EventListAdapter(List<EventCalendarResponse> myDataset) {
        mDataset = myDataset;
        mEventData = getUpcomingEventData(myDataset);
        if (mEventData.size() == 0){
            EventDataResponse emptyEvent = new EventDataResponse();
            emptyEvent.setDescription("--EMPTY--");
            mEventData.add(emptyEvent);
        }
        mEventTemp = getEventData(myDataset);

        if (mEventTemp.size() == 0){
            EventDataResponse emptyEvent = new EventDataResponse();
            emptyEvent.setDescription("--EMPTY--");
            mEventTemp.add(emptyEvent);
        }
    }

    private List<EventDataResponse> getEventData(List<EventCalendarResponse> eventCalendarResponses){
        List<EventDataResponse> result = new ArrayList<>();
        for(EventCalendarResponse eventCalendarResponse:eventCalendarResponses){
            for(EventDataResponse eventDataResponse:eventCalendarResponse.getData()){
                result.add(eventDataResponse);
            }
        }
        return result;
    }

    private List<EventDataResponse> getUpcomingEventData(List<EventCalendarResponse> eventCalendarResponses){
        List<EventDataResponse> result = new ArrayList<>();
        for(EventCalendarResponse eventCalendarResponse:eventCalendarResponses){
            for(EventDataResponse eventDataResponse:eventCalendarResponse.getData()){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dateEvent = null;
                try {
                    dateEvent = sdf.parse(eventDataResponse.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!new Date().after(dateEvent)){
                    result.add(eventDataResponse);
                }
            }
        }
        return result;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        mContext = parent.getContext();
        View v = null;
        if(viewType == TYPE_EMPTY) {
             v = LayoutInflater.from(mContext)
                    .inflate(R.layout.event_list_item, parent, false);

        } else  {
            v = LayoutInflater.from(mContext)
                    .inflate(R.layout.event_empty_list_item, parent, false);
        }
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (!mEventData.get(position).getDescription().equalsIgnoreCase("--EMPTY--")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDateFormat = null;
            Date dateSelected = null;
            try {
                dateSelected = sdf.parse(mEventData.get(position).getDate());
                simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvEventTitle.setText(mEventData.get(position).getTitle());
            holder.tvEventAddress.setText(mEventData.get(position).getPlace());
            holder.tvEventTime.setText("Waktu " + mEventData.get(position).getTime());
            holder.tvEventDate.setText(simpleDateFormat.format(dateSelected));
            holder.ivEventDetail.setOnClickListener(view -> {
                onEventItemClicked.onClick(mEventData.get(position).getId());
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mEventData != null & mEventData.get(position) != null && !mEventData.get(position).getDescription().equalsIgnoreCase("--EMPTY--")){
            return TYPE_EMPTY;
        }

        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mEventData.size();
    }

    interface OnEventItemClicked{
        void onClick(String eventId);
    }
}