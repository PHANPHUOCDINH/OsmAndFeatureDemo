package FavoritePlace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SettingDialogFragment extends DialogFragment {
    OnSwitchListener mListener;
    public interface OnSwitchListener{
        void onOn();
        void onOff();
    }
    public SettingDialogFragment(OnSwitchListener listener)
    {
        mListener=listener;
    }
    Switch aSwitch;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aSwitch =view.findViewById(R.id.switch1);
        if(com.example.favoriteplace.MainActivity.isFavorite)
            aSwitch.setChecked(true);
        else
            aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    com.example.favoriteplace.MainActivity.isFavorite=true;
                    aSwitch.setChecked(true);
                    mListener.onOn();
                }
                else
                {
                    com.example.favoriteplace.MainActivity.isFavorite=false;
                    aSwitch.setChecked(false);
                    mListener.onOff();
                }
            }
        });
    }

    @Override
    public void onResume() {
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().setCancelable(true);
        super.onResume();
    }
}
