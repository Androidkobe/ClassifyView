package com.anarchy.classify.simple;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.anarchy.classify.MergeInfo;
import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;
import com.anarchy.classify.adapter.SubAdapterReference;
import com.anarchy.classify.simple.widget.CanMergeView;

import java.util.List;

/**
 * 一种常用的方式，主层级与负层级使用相同的布局元素创建
 * 对于副层级定为List形式。在内部处理好移动的数据迁移
 */
public abstract class PrimitiveSimpleAdapter<Sub, VH extends PrimitiveSimpleAdapter.ViewHolder> implements BaseSimpleAdapter {
    private static final int MODE_SHIFT = 30;
    public static final int TYPE_MASK = 0x3 << MODE_SHIFT;
    public static final int TYPE_UNDEFINED = 0;
    public static final int TYPE_MAIN = 1 << MODE_SHIFT;
    public static final int TYPE_SUB = 2 << MODE_SHIFT;
    private SimpleMainAdapter mSimpleMainAdapter;
    private SimpleSubAdapter mSimpleSubAdapter;

    public PrimitiveSimpleAdapter() {
        mSimpleMainAdapter = new SimpleMainAdapter();
        mSimpleSubAdapter = new SimpleSubAdapter();
    }

    @Override
    public BaseMainAdapter getMainAdapter() {
        return mSimpleMainAdapter;
    }

    @Override
    public BaseSubAdapter getSubAdapter() {
        return mSimpleSubAdapter;
    }

    @Override
    public boolean isShareViewPool() {
        return true;
    }

    public void notifyItemInsert(int position){
        mSimpleMainAdapter.notifyItemInserted(position);
    }

    /**
     * 通知数据变化
     * @param position
     */
    public void notifyItemChanged(int position){
        mSimpleMainAdapter.notifyItemChanged(position);
    }

    /**
     * 通知数据变化
     * @param position
     * @param count
     */
    public void notifyItemRangeChanged(int position,int count){
        mSimpleMainAdapter.notifyItemRangeChanged(position,count);
    }

    /**
     * 通知添加数据
     * @param position
     * @param count
     */
    public void notifyItemRangeInsert(int position,int count){
        mSimpleMainAdapter.notifyItemRangeInserted(position,count);
    }

    /**
     * 通知触发数据变动
     */
    public void notifyDataSetChanged(){
        mSimpleMainAdapter.notifyDataSetChanged();
    }


    /**
     * 创建view holder
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract VH onCreateViewHolder(ViewGroup parent, int viewType);


    /**
     * 用于显示{@link com.anarchy.classify.simple.widget.InsertAbleGridView} 的item布局
     *
     * @param parent       父View
     * @param convertView  缓存的View 可能为null
     * @param mainPosition 主层级位置
     * @param subPosition  副层级位置
     * @return
     */
    public  abstract View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition);

    /**
     * 返回主层级数量
     * @return
     */
    protected abstract int getItemCount();

    /**
     * 副层级的数量，用于主层级上的显示效果
     * @return
     */
    protected abstract int getSubItemCount(int parentPosition);

    /**
     * @see BaseMainAdapter#explodeItem(int, View)
     * @param position 主层级的位置
     * @param pressedView 被点击的view 如果不为空则为点击操作触发弹出副层级窗口
     * @return
     */
    protected abstract List<Sub> explode(int position,@Nullable View pressedView);

    /**
     * 在主层级触发move事件 在这里进行数据改变
     * @param selectedPosition 当前选择的item位置
     * @param targetPosition 要移动到的位置
     */
    protected abstract void onMove(int selectedPosition, int targetPosition);

    /**
     * 两个选项能否合并
     * @param selectPosition
     * @param targetPosition
     * @return
     */
    protected abstract boolean canMergeItem(int selectPosition, int targetPosition);
    /**
     * 合并数据处理
     * @param selectedPosition
     * @param targetPosition
     */
    protected abstract void onMerged(int selectedPosition,int targetPosition);

    /**
     * 从副层级移除的元素
     * @param sub
     * @return 返回的数为添加到主层级的位置
     */
    protected abstract int onLeaveSubRegion(Sub sub);
    /**
     * 主层级数据绑定
     *
     * @param holder
     * @param position
     */
    protected abstract void onBindMainViewHolder(VH holder, int position);

    /**
     * 副层级数据绑定
     *
     * @param holder
     * @param mainPosition
     * @param subPosition
     */
    protected abstract void onBindSubViewHolder(VH holder, int mainPosition, int subPosition);

    /**
     * 获取主副层级标记
     *
     * @param type
     * @return {@link #TYPE_UNDEFINED,#TYPE_MAIN,#TYPE_SUB}
     */
    public static int getSpecialType(int type) {
        return type & TYPE_MASK;
    }

    /**
     * 获取原始的view type
     *
     * @param type
     * @return
     */
    public static int getOriginTyoe(int type) {
        return type & (~TYPE_MASK);
    }

    /**
     * 默认返回false 不在view type上添加主副层级标记
     * 如果返回true 会在原有view type 上添加主副层级标记
     *
     * @return
     */
    protected boolean haveSpecialType() {
        return false;
    }

    /**
     * @param parentIndex
     * @param index       if -1  in main region
     */
    protected void onItemClick(View view, int parentIndex, int index) {
    }

    /**
     * 返回ItemType
     * @param parentPosition
     * @param subPosition
     * @return
     */
    protected int getItemType(int parentPosition, int subPosition) {
        return 0;
    }

    /**
     * 能否支持长按拖拽
     * @param mainPosition
     * @param subPosition
     * @return
     */
    protected boolean canDragOnLongPress(int mainPosition, int subPosition) {
        return true;
    }


    private class SimpleMainAdapter extends BaseMainAdapter<VH, SimpleSubAdapter> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = PrimitiveSimpleAdapter.this.onCreateViewHolder(parent, viewType);
            CanMergeView canMergeView = vh.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.setAdapter(PrimitiveSimpleAdapter.this);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            CanMergeView canMergeView = holder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.initOrUpdateMain(position,PrimitiveSimpleAdapter.this.getSubItemCount(position));
            }
            PrimitiveSimpleAdapter.this.onBindMainViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return PrimitiveSimpleAdapter.this.getItemCount();
        }


        @Override
        public int getItemViewType(int position) {
            int originType = PrimitiveSimpleAdapter.this.getItemType(position, -1);
            return PrimitiveSimpleAdapter.this.haveSpecialType() ? TYPE_MAIN | (originType & (~TYPE_MASK)) : originType;
        }

        @Override
        public boolean onMove(int selectedPosition, int targetPosition) {
            notifyItemMoved(selectedPosition, targetPosition);
            PrimitiveSimpleAdapter.this.onMove(selectedPosition,targetPosition);
            return true;
        }

        @Override
        public boolean canMergeItem(int selectedPosition, int targetPosition) {
            return PrimitiveSimpleAdapter.this.canMergeItem(selectedPosition, targetPosition);
        }

        @Override
        public int onLeaveSubRegion(int selectedPosition, SubAdapterReference<SimpleSubAdapter> subAdapterReference) {
            SimpleSubAdapter simpleSubAdapter = subAdapterReference.getAdapter();
            Sub sub = simpleSubAdapter.getData().remove(selectedPosition);
            int parentTargetPosition = PrimitiveSimpleAdapter.this.onLeaveSubRegion(sub);
            if(simpleSubAdapter.getParentPosition() != -1) notifyItemChanged(simpleSubAdapter.getParentPosition());
            return parentTargetPosition;
        }

        @Override
        public List<Sub> explodeItem(int position, View pressedView) {
            return PrimitiveSimpleAdapter.this.explode(position, pressedView);
        }

        @Override
        public boolean canDragOnLongPress(int position, View pressedView) {
            return PrimitiveSimpleAdapter.this.canDragOnLongPress(position, -1);
        }

        @Override
        public void onItemClick(int position, View pressedView) {
            PrimitiveSimpleAdapter.this.onItemClick(pressedView, position, -1);
        }

        @Override
        public boolean onMergeStart(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMergeStart();
            }
            return true;
        }

        @Override
        public void onMergeCancel(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMergeCancel();
            }
        }

        @Override
        public void onMerged(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.onMerged();
            }
            PrimitiveSimpleAdapter.this.onMerged(selectedPosition,targetPosition);
            notifyItemRemoved(selectedPosition);
            if(selectedPosition < targetPosition) {
                notifyItemChanged(targetPosition-1);
            }else {
                notifyItemChanged(targetPosition);
            }
        }

        @Override
        public MergeInfo onPrePareMerge(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition) {
            if (selectedViewHolder == null || targetViewHolder == null) return null;
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                ChangeInfo info = canMergeView.prepareMerge();
                info.paddingLeft = selectedViewHolder.getPaddingLeft();
                info.paddingRight = selectedViewHolder.getPaddingRight();
                info.paddingTop = selectedViewHolder.getPaddingTop();
                info.paddingBottom = selectedViewHolder.getPaddingBottom();
                info.outlinePadding = canMergeView.getOutlinePadding();
                float scaleX = ((float) info.itemWidth) / ((float) (selectedViewHolder.itemView.getWidth() - info.paddingLeft - info.paddingRight - 2 * info.outlinePadding));
                float scaleY = ((float) info.itemHeight) / ((float) (selectedViewHolder.itemView.getHeight() - info.paddingTop - info.paddingBottom - 2 * info.outlinePadding));
                float targetX = targetViewHolder.itemView.getLeft() + info.left + info.paddingLeft - (info.paddingLeft + info.outlinePadding) * scaleX;
                float targetY = targetViewHolder.itemView.getTop() + info.top + info.paddingTop - (info.paddingTop + info.outlinePadding) * scaleY;
                return new MergeInfo(scaleX, scaleY, targetX, targetY);
            }
            return null;
        }

        @Override
        public void onStartMergeAnimation(VH selectedViewHolder, VH targetViewHolder, int selectedPosition, int targetPosition, int duration) {
            CanMergeView canMergeView = targetViewHolder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.startMergeAnimation(duration);
            }
        }
    }

    private class SimpleSubAdapter extends BaseSubAdapter<VH> {
        private int mParentPosition = -1;
        private List<Sub> mData;

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = PrimitiveSimpleAdapter.this.onCreateViewHolder(parent, viewType);
            CanMergeView canMergeView = vh.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.setAdapter(PrimitiveSimpleAdapter.this);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            CanMergeView canMergeView = holder.getCanMergeView();
            if (canMergeView != null) {
                canMergeView.initOrUpdateSub(mParentPosition, position);
            }
            PrimitiveSimpleAdapter.this.onBindSubViewHolder(holder, mParentPosition, position);
        }

        @Override
        public int getItemCount() {
            if (mData == null) return 0;
            return mData.size();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void initData(int parentIndex, List data) {
            mParentPosition = parentIndex;
            mData = data;
            notifyDataSetChanged();
        }

        public List<Sub> getData() {
            return mData;
        }

        public int getParentPosition() {
            return mParentPosition;
        }

        @Override
        public boolean onMove(int selectedPosition, int targetPosition) {
            notifyItemMoved(selectedPosition, targetPosition);
            mData.add(targetPosition, mData.remove(selectedPosition));
            if(mParentPosition != -1) {
                mSimpleMainAdapter.notifyItemChanged(mParentPosition);
            }
            return true;
        }

        @Override
        public void onItemClick(int position, View pressedView) {
            PrimitiveSimpleAdapter.this.onItemClick(pressedView, mParentPosition, position);
        }

        @Override
        public int getItemViewType(int position) {
            int originType = PrimitiveSimpleAdapter.this.getItemType(position, -1);
            return PrimitiveSimpleAdapter.this.haveSpecialType() ? TYPE_SUB | (originType & (~TYPE_MASK)) : originType;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected CanMergeView mCanMergeView;
        private int paddingLeft;
        private int paddingRight;
        private int paddingTop;
        private int paddingBottom;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof CanMergeView) {
                mCanMergeView = (CanMergeView) itemView;
            } else if (itemView instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) itemView;
                paddingLeft = group.getPaddingLeft();
                paddingRight = group.getPaddingRight();
                paddingTop = group.getPaddingTop();
                paddingBottom = group.getPaddingBottom();
                //只遍历一层 寻找第一个符合条件的view
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child instanceof CanMergeView) {
                        mCanMergeView = (CanMergeView) child;
                        break;
                    }
                }
            }
        }

        public CanMergeView getCanMergeView() {
            return mCanMergeView;
        }

        public int getPaddingLeft() {
            return paddingLeft;
        }

        public int getPaddingRight() {
            return paddingRight;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }
    }
}