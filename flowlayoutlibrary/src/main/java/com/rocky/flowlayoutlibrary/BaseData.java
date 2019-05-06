package com.rocky.flowlayoutlibrary;

import java.util.List;

/**
 * @author rocky
 * @date 2019/5/6.
 * description：分组数据基类
 */
public class BaseData<T> {
    private String title;//组名
    private List<T> items;//具体的内容列表

    public BaseData() {
    }

    public BaseData(String title, List<T> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
