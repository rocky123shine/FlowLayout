package com.rocky.rockyflowlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.rocky.flowlayoutlibrary.RockySuspensionDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author rocky
 * @date 2019/5/6.
 * description：测试悬浮
 */
public class TestSuspensionActivity extends AppCompatActivity {

    private RecyclerView productView;
    private List<TeamData> classifies = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_suspension);

        productView = (RecyclerView) findViewById(R.id.product_view);
        productView.setLayoutManager(new LinearLayoutManager(this));
        classifies.add(new TeamData("颜色",
                Arrays.asList(new Des("红色"),
                new Des("白色"),
                new Des("蓝色"),
                new Des("橘黄色"),
                new Des("格调灰"),
                new Des("深色"),
                new Des("咖啡色"))));
        classifies.add(new TeamData("尺寸", Arrays.asList(new Des("180"),
                new Des("175"),
                new Des("170"),
                new Des("165"),
                new Des("160"),
                new Des("155"),
                new Des("150"))));
        classifies.add(new TeamData("款式",
                Arrays.asList(new Des("男款"), new Des("女款"),
                        new Des("中年款"),
                        new Des("潮流款"),
                        new Des("儿童款"),
                        new Des("同志版"))));
        classifies.add(new TeamData("腰围", Arrays.asList(new Des("26"),
                new Des("27"),
                new Des("28"),
                new Des("29"),
                new Des("30"),
                new Des("31"),
                new Des("32"),
                new Des("33"),
                new Des("34"),
                new Des("35"))));
        classifies.add(new TeamData("肩宽", Arrays.asList(new Des("26"),
                new Des("27"),
                new Des("28"),
                new Des("29"),
                new Des("30"),
                new Des("31"),
                new Des("32"),
                new Des("33"),
                new Des("34"),
                new Des("35"))));
        classifies.add(new TeamData("臂长", Arrays.asList(new Des("26"),
                new Des("27"),
                new Des("28"),
                new Des("29"),
                new Des("30"),
                new Des("31"),
                new Des("32"),
                new Des("33"),
                new Des("34"),
                new Des("35"))));

        productView.setAdapter(new ProductAdapter(this, classifies));
        productView.addItemDecoration(new RockySuspensionDecoration<Des,TeamData>(this, classifies));

    }
}
