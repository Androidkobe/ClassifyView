package com.anarchy.classify.simple.widget;

import android.graphics.Point;

import com.anarchy.classify.ChangeInfo;
import com.anarchy.classify.simple.SimpleAdapter;

import java.util.List;

/**
 * Version 1.0
 * <p>
 * Date: 16/6/7 10:29
 * Author: zhendong.wu@shoufuyou.com
 * <p>
 * Copyright © 2014-2016 Shanghai Xiaotu Network Technology Co., Ltd.
 */
public interface CanMergeView {
    /**
     * 进入merge状态
     */
    void onMergeStart();

    /**
     * 离开merge状态
     */
    void onMergeCancel();

    /**
     * 结束merge事件
     */
    void onMerged();

    /**
     * 开始merge动画
     * @param duration  动画持续时间
     */
    void startMergeAnimation(int duration);

    /**
     * 准备merge
     * @return 返回新添加的view 应该放置在布局中的位置坐标
     */
    ChangeInfo prepareMerge();
    /**
     * 设置适配器
     * @param simpleAdapter
     */
    void setAdapter(SimpleAdapter simpleAdapter);

    /**
     * 初始化
     * @param list
     */
    void init(int parentIndex,List list);

    int getOutlinePadding();
}