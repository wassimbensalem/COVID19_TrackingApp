package com.example.coronavirus.ui.doctors;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DoctorsViewModel extends  ViewModel{

    private MutableLiveData<String> mText;

    public DoctorsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is doctors fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
