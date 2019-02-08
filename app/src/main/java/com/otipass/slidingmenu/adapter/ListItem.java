package com.otipass.slidingmenu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.otipass.adt67.R;
import com.otipass.slidingmenu.adapter.NavDrawerListCustomAdapter.RowType;

public class ListItem implements Item {
private final String         str1;
private final String         str2;
private int background;

public ListItem(String text1, String text2, int background) {
    this.str1 = text1;
    this.str2 = text2;
    this.background = background;
}

@Override
public int getViewType() {
    return RowType.LIST_ITEM.ordinal();
}

@Override
public View getView(LayoutInflater inflater, View convertView) {
    View view;
    if (convertView == null) {
        view = (View) inflater.inflate(R.layout.list_item_nav_drawer, null);
        // Do some initialization
    } else {
        view = convertView;
    }

    TextView text1 = (TextView) view.findViewById(R.id.list_content1);
    TextView text2 = (TextView) view.findViewById(R.id.list_content2);
    text1.setText(str1);
    text2.setText(str2);
    text1.setBackgroundResource(background);

    return view;
}

}
