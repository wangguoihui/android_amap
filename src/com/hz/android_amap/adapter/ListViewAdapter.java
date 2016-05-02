package com.hz.android_amap.adapter;

import java.util.List;
import com.amap.api.services.core.PoiItem;
import com.hz.android_amap.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private Context context;
	private int selectIndex;
	private List<PoiItem> poiItems;

	public ListViewAdapter(Context context, List<PoiItem> poiItems) {
		this.context = context;
		this.poiItems = poiItems;
	}

	@Override
	public int getCount() {
		return poiItems.size();
	}

	@Override
	public Object getItem(int position) {
		return poiItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		viewHolder = new ViewHolder();
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.lv_item, null);
		viewHolder.topTextView = (TextView) convertView
				.findViewById(R.id.item_top);
		viewHolder.bottomTextView = (TextView) convertView
				.findViewById(R.id.item_bottom);
		viewHolder.selectBtn = (Button) convertView
				.findViewById(R.id.select_address);

		viewHolder.topTextView.setText(poiItems.get(position).getTitle());
		viewHolder.bottomTextView.setText(poiItems.get(position)
				.getProvinceName()
				+ poiItems.get(position).getCityName()
				+ poiItems.get(position).getSnippet());

		convertView.setTag(viewHolder);
		convertView.setTag(R.id.LatLonPoint, poiItems.get(position)
				.getLatLonPoint());

		if (selectIndex == position) {
			viewHolder.selectBtn.setVisibility(View.VISIBLE);
		} else {
			viewHolder.selectBtn.setVisibility(View.GONE);
		}

		return convertView;
	}

	public void setSelectIndex(int position) {
		this.selectIndex = position;
		notifyDataSetChanged();
	}

	class ViewHolder {
		TextView topTextView;
		TextView bottomTextView;
		Button selectBtn;
	}
}















