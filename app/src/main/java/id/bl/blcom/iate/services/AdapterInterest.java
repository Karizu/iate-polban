package id.bl.blcom.iate.services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import id.bl.blcom.iate.R;

public class AdapterInterest extends BaseAdapter {

    private Context mContext;
    private List<String> mInterest;
    private String selectedInterest;

    public AdapterInterest(Context c, List<String> interests, String selected) {
        mContext = c;
        mInterest = interests;
        selectedInterest = selected;
    }

    public int getCount() {
        return mInterest.size();
    }

    public String getItem(int position) {
        return mInterest.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = inflater.inflate(R.layout.item_interest, null);

            TextView textView =  gridView
                    .findViewById(R.id.interest_text);
            textView.setText(mInterest.get(position));
            if (selectedInterest != null){
                if(selectedInterest.contains(mInterest.get(position))){
                    textView.setBackgroundResource(R.color.darkRed);
                }
            }
        } else {
            gridView = convertView;
        }

        return gridView;

    }



}
