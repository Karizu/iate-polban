package id.bl.blcom.iate.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.bl.blcom.iate.R;
import id.bl.blcom.iate.api.ApiInterface;
import id.bl.blcom.iate.models.MemberProfile;
import id.bl.blcom.iate.models.Profile;
import id.bl.blcom.iate.presentation.direktori.MemberDetailActivity;
import id.bl.blcom.iate.presentation.direktori.MemberDetailFragment;

public class AdapterListMenuDirectory extends RecyclerView.Adapter<AdapterListMenuDirectory.ViewHolder> {
    private List<MemberProfile> profiles;
    private Context context;
    private boolean isFromNearMe = false;

    public AdapterListMenuDirectory(List<MemberProfile> profiles, Context context){
        this.profiles = profiles;
        this.context = context;
    }

    public AdapterListMenuDirectory(List<MemberProfile> profiles, Context context, boolean isFromNearMe){
        this.profiles = profiles;
        this.context = context;
        this.isFromNearMe = isFromNearMe;
    }

    @Override
    public AdapterListMenuDirectory.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_member_directory, parent, false);

        return new AdapterListMenuDirectory.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AdapterListMenuDirectory.ViewHolder holder, int position){
        MemberProfile profile = profiles.get(position);
        Log.d("onBindViewHolder", "" + position);
        Glide.with(context).applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).load(ApiInterface.BASE_URL_IMAGE + profile.getPicture()).into(holder.imageView);
        holder.fullName.setText(profile.getFullname());
        holder.region.setText(profile.getGroup()!=null?profile.getGroup().getName():"-");
        if (isFromNearMe) {
            String distance = profile.getDistance_in_km().length() > 4 ? profile.getDistance_in_km().substring(0, 5) : profile.getDistance_in_km();
            holder.in_km.setVisibility(View.VISIBLE);
            holder.region.setText(profile.getGroup_name());
            holder.in_km.setText("Dalam " + distance + " Km");
        }

        holder.memberLayout.setOnClickListener(view -> {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar dateNow = Calendar.getInstance();
            Calendar birthDate = Calendar.getInstance();
            try {
                if (profile.getBirthDate() != null)
                    birthDate.setTime(format.parse(profile.getBirthDate()));
                Log.d("Birth Date", profile.getBirthDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Bundle bundle = new Bundle();
            bundle.putString("picture", ApiInterface.BASE_URL_IMAGE + profile.getPicture());
            bundle.putString("fullname", profile.getFullname());
            bundle.putString("dso", profile.getGroup()!=null?profile.getGroup().getName():profile.getGroup_name());
            bundle.putString("phoneNumber", profile.getPhoneNumber());
            bundle.putString("email", profile.getEmail());
            bundle.putString("age", dateNow.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR) + " Tahun");
            bundle.putString("status", profile.getWork());
            bundle.putString("shirtSize", profile.getShirtSize() != null ? profile.getShirtSize() : "-");
            bundle.putString("hobby", profile.getHobby() != null ? profile.getHobby() : "");
            bundle.putString("interest", profile.getInterests() != null ? profile.getInterests() : "");
            bundle.putString("address", profile.getAddress() != null ? profile.getAddress() : "");
            bundle.putString("whatsapp", profile.getWhatsupNumber() != null ? profile.getWhatsupNumber() : "");
            bundle.putString("line", profile.getLineAccount() != null ? profile.getLineAccount() : "");
            bundle.putString("bbm", profile.getPinBbm() != null ? profile.getPinBbm() : "");
            bundle.putString("facebook", profile.getFacebookAccount() != null ? profile.getFacebookAccount() : "");
            bundle.putString("twitter", profile.getTwitterAccount() != null ? profile.getTwitterAccount() : "");
            bundle.putString("instagram", profile.getInstagramAccount() != null ? profile.getInstagramAccount() : "");
            bundle.putString("privacy", profile.getPrivacyConfig());
            bundle.putString("member_id", profile.getMember_id() != null ? profile.getMember_id() : "-");

            if (isFromNearMe) {
                Profile profile1 = new Profile();
                profile1.setPicture(profile.getPicture());
                profile1.setFullname(profile.getFullname());
                profile1.setGroup(profile.getGroup());
                profile1.setPhoneNumber(profile.getPhoneNumber());
                profile1.setEmail(profile.getEmail());
                profile1.setWork(profile.getWork());
                profile1.setShirtSize(profile.getShirtSize());
                profile1.setHobby(profile.getHobby());
                profile1.setInterests(profile.getInterests());
                profile1.setAddress(profile.getAddress());
                profile1.setWhatsupNumber(profile.getWhatsupNumber());
                profile1.setLineAccount(profile.getLineAccount());
                profile1.setPinBbm(profile.getPinBbm());
                profile1.setFacebookAccount(profile.getFacebookAccount());
                profile1.setTwitterAccount(profile.getTwitterAccount());
                profile1.setInstagramAccount(profile.getInstagramAccount());
                profile1.setPrivacyConfig(profile.getPrivacyConfig());
                profile1.setMember_id(profile.getMember_id());


                Intent intent = new Intent(context, MemberDetailActivity.class);
                String json = new Gson().toJson(profile1);
                Log.d("json", json);
                intent.putExtra("json", json);
                intent.putExtra("age", dateNow.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR) + " Tahun");
                context.startActivity(intent);
            } else {
                Fragment fragment = new MemberDetailFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = profile.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.screen_area, fragment, "member list");
                fragmentTransaction.addToBackStack("member list");

                fragmentTransaction.commit();
            }

        });
    }

    @Override
    public int getItemCount(){ return profiles.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView fullName, in_km;
        public TextView region;
        private LinearLayout memberLayout;

        public ViewHolder(View v){
            super(v);

            imageView = (ImageView) v.findViewById(R.id.profile_image);
            fullName = (TextView) v.findViewById(R.id.directory_full_name);
            in_km = (TextView) v.findViewById(R.id.in_km);
            region = (TextView) v.findViewById(R.id.directory_region);
            memberLayout = v.findViewById(R.id.layout_member);
        }
    }

    public void updateData(List<MemberProfile> newProfiles){
        profiles = new ArrayList<>();
        profiles.addAll(newProfiles);
        notifyDataSetChanged();
    }
}
