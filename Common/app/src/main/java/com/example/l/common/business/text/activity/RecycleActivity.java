package com.example.l.common.business.text.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.l.common.R;
import com.example.l.common.base.BaseActivity;
import com.example.l.common.base.BaseRecycleAdapter;
import com.example.l.common.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author:dongpo 创建时间: 6/20/2016
 * 描述:
 * 修改:
 */
public class RecycleActivity extends BaseActivity {
    @Bind(R.id.recycle_view)
    RecyclerView mRecyclerView;

    @Override
    protected void initView() {
        List<String> list = new ArrayList<>();
        list.add("你好啊");
        list.add("你好啊1");
        list.add("你好啊2");
        list.add("你好啊3");
        list.add("你好啊4");
        list.add("你好啊5");

        List<Integer> list1 = new ArrayList<>();
        list1.add(11);
        list1.add(22);
        list1.add(33);
        list1.add(44);
        list1.add(55);
        list1.add(66);
        list1.add(77);
        list1.add(88);

        GridLayoutManager layout = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layout);
        TextAdapter adapter = new TextAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.addItemType(list, R.layout.grid_item);
        //adapter.addItemType(list1, R.layout.grid_item1);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recycle;
    }


    public class TextAdapter extends BaseRecycleAdapter<String> {
        public TextAdapter(List<String> data, int layoutId) {
            super(data, layoutId);
        }

        public TextAdapter() {
            super();
        }

        @Override
        protected void setItemData(ViewHolder holder, int position, int viewType) {

            List<String> list = getData(R.layout.grid_item);
            List<Integer> list1 = getData(R.layout.grid_item1);
            List<?> current = getData(viewType);
            switch (viewType) {
                case R.layout.grid_item1:
                    Integer s = null;
                    s = (Integer) current.get(position - list.size());
                    holder.setText(R.id.recycle_text_image, s + "");
                    break;
                case R.layout.grid_item:
                    String str = (String) current.get(position);

                    holder.setText(R.id.descriptionTv, str);
                    break;
            }

            //holder.setText(R.id.descriptionTv, (String) data.get(position));

        }



    }


}
