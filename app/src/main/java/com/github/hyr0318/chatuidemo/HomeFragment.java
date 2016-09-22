package com.github.hyr0318.chatuidemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.hyphenate.easeui.EaseConstant;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * 作者：hyr on 2016/9/20 14:29
 * 邮箱：2045446584@qq.com
 */
public class HomeFragment extends Fragment implements HomeAdapter.OnItemClickListener {

    RecyclerView recyclerView;


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.inject(getActivity());
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container,false);

        recyclerView = (RecyclerView) inflate.findViewById(R.id.list);

        initList();

        return inflate;
    }


    private void initList() {

        recyclerView.addItemDecoration(
            new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        HomeAdapter homeAdapter = new HomeAdapter(getData(), getContext());
        homeAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(homeAdapter);

    }




    private List<HomeItem> getData() {
        List<HomeItem> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            HomeItem homeItem = new HomeItem();
            homeItem.setName("hyr" + i);
            data.add(homeItem);
        }

        return data;
    }


    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
        HomeItem homeItem = (HomeItem) o;

        startActivity(new Intent(getContext(),ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID,homeItem.getName()));

    }
}
