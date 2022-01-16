package com.example.website.Providers;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.website.LogInfo;
import com.example.website.R;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<LogInfo> dataModelArrayList;

    public ListAdapter(Context context, ArrayList<LogInfo> dataModelArrayList) {

        this.context = context;
        this.dataModelArrayList = dataModelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return dataModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null, true);

            holder.Name = (TextView) convertView.findViewById(R.id.Name);
            holder.loggedIn = (TextView) convertView.findViewById(R.id.loggedIn);
            holder.login = (TextView) convertView.findViewById(R.id.loginHour);
            holder.logout = (TextView) convertView.findViewById(R.id.logoutHour);
            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        //Picasso.get().load(dataModelArrayList.get(position).getImgURL()).into(holder.iv);
        holder.Name.setText("שם: "+dataModelArrayList.get(position).getName());
        holder.loggedIn.setText("סטאטוס: "+dataModelArrayList.get(position).getIsLoggedIn());
        holder.login.setText("שעת התחברות: "+dataModelArrayList.get(position).getLoginHour());
        holder.logout.setText("שעת התנתקות: "+dataModelArrayList.get(position).getLogoutHour());


        return convertView;
    }

    private class ViewHolder {

        protected TextView Name, loggedIn,login,logout;
    }

}