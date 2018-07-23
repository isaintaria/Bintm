package com.ng.socialtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ng.socialtest.base.BaseFragment;
import com.ng.socialtest.model.UserModel;

import butterknife.ButterKnife;


public class MyInfoFragment extends BaseFragment {

    private UserModel model;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View view =  inflater.inflate(R.layout.fragment_my_info, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        this.model = (UserModel) args.getSerializable("UserModel");
        if( this.model == null )
            throw new IllegalArgumentException("모델이 없음");
        return view;
    }

    public static MyInfoFragment NewInstance(UserModel model)
    {
        MyInfoFragment newFragment = new MyInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("UserModel", model);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    protected String getTitle() {
        return "내 정보";
    }
}
