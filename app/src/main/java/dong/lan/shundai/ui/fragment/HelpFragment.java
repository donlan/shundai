package dong.lan.shundai.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import dong.lan.shundai.R;
import dong.lan.shundai.adapter.base.HelpAdapter;
import dong.lan.shundai.bean.Help;
import dong.lan.shundai.bean.User;
import dong.lan.shundai.ui.FragmentBase;
import dong.lan.shundai.ui.HelpInfoActivity;
import dong.lan.shundai.util.CollectionUtils;
import dong.lan.shundai.view.xlist.XListView;

/**
 * Created by 梁桂栋 on 2015/12/21.
 */
public class HelpFragment extends FragmentBase implements AdapterView.OnItemClickListener, XListView.IXListViewListener {

    private int limit = 10;
    private String type;
    private String city;
    private boolean me;
    private XListView mListView;
    private HelpAdapter adapter;
    private TextView noHelp;
    private int count = 0;
    List<BmobQuery<Help>> queries = new ArrayList<>();

    public HelpFragment init(String style, String City, boolean ME) {
        type = style;
        city = City;
        me = ME;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.framgment_help, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (XListView) getView().findViewById(R.id.style_list);
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        noHelp = (TextView) getView().findViewById(R.id.no_help);
        BmobQuery<Help> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("Tag", type);
        queries.add(query1);
        if (city != null && !city.equals("")) {
            BmobQuery<Help> query2 = new BmobQuery<>();
            query2.addWhereEqualTo("City", city);
            queries.add(query2);
        }
        if (me) {
            BmobQuery<Help> query = new BmobQuery<>();
            query.addWhereEqualTo("user", BmobUserManager.getInstance(getActivity()).getCurrentUser(User.class));
            queries.add(query);
        }
        freshData();
    }


    private void freshData() {
        BmobQuery<Help> query = new BmobQuery<Help>();
        query.setLimit(limit);
        query.include("user");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.and(queries);
        query.order("-createdAt,-updatedAt");
        query.findObjects(getActivity(), new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {
                if (CollectionUtils.isNotNull(list)) {
                    if (list.size() < limit)
                        mListView.setPullLoadEnable(false);
                    adapter = new HelpAdapter(getActivity(), list);
                    count = count + list.size();
                    mListView.setAdapter(adapter);
                    noHelp.setVisibility(View.GONE);
                } else {
                    ShowToast("没有顺带信息");
                    noHelp.setVisibility(View.VISIBLE);
                }
                refreshPull();
            }

            @Override
            public void onError(int i, String s) {
                refreshPull();
            }
        });

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), HelpInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tag", type);
        bundle.putBoolean("me", me);
        bundle.putSerializable("help", (Help) parent.getAdapter().getItem(position));
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    @Override
    public void onRefresh() {
            freshData();
    }

    @Override
    public void onLoadMore() {
        BmobQuery<Help> query = new BmobQuery<Help>();
        query.setLimit(limit);
        query.setSkip(count);
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.and(queries);
        query.include("user");
        query.order("-createAt,-updatedAt");
        query.findObjects(getActivity(), new FindListener<Help>() {
            @Override
            public void onSuccess(List<Help> list) {
                if (CollectionUtils.isNotNull(list)) {
                    count = count + list.size();
                    adapter.addAll(list);
                    mListView.setAdapter(adapter);
                    mListView.setSelection(adapter.getCount() - list.size());

                } else {
                    ShowToast("加载完成");
                }
                mListView.setPullLoadEnable(true);
                refreshLoad();

                mListView.setSelection(adapter.getCount() - list.size());
            }

            @Override
            public void onError(int i, String s) {
                refreshLoad();
            }
        });
    }

    private void refreshLoad() {
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
        }
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }
}
