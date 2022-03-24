package id.bl.blcom.iate.presentation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rezkyatinnov.kyandroid.session.Session;
import com.rezkyatinnov.kyandroid.session.SessionNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.bl.blcom.iate.R;

public class DonateFragment extends Fragment {

    @BindView(R.id.webViewDonate)
    WebView webViewDonate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donate, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        String token = "";
        try {
            token = Session.get("token").getValue();
        } catch (SessionNotFoundException e) {
            e.printStackTrace();
        }
        webViewDonate.loadUrl("http://iate.boardinglabs.id/static/how_to_donate.html?token="+token);
        Log.d("url", "http://iate.boardinglabs.id/static/how_to_donate.html?token="+token);
    }
}
