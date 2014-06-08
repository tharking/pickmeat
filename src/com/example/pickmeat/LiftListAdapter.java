package com.example.pickmeat;

import java.util.ArrayList;

import com.example.pickmeat.DataAccess;
import com.example.pickmeat.DataAccess.DataHelper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.view.ViewPager.OnPageChangeListener;


public class LiftListAdapter extends BaseExpandableListAdapter {

    Context context; 
    int layoutResourceId;  
    ArrayList<LocationPool> locations;
    
    public LiftListAdapter(Context context, ArrayList<LocationPool> locations) {
//        super(context, layoutResourceId, data);
        this.context = context;
        this.locations = locations;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
     return locations.get(groupPosition).liftList.get(childPosition);
    }
    
    @Override
    public long getChildId(int groupPosition, int childPosition) {
     return childPosition;
    }
    
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, 
      View view, ViewGroup parent) {
      
     Lift lift = (Lift) getChild(groupPosition, childPosition);
     LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     view = infalInflater.inflate(R.layout.widget_lift_item, parent, false);

     MyPagerAdapter pagerAdapter = new MyPagerAdapter();
     pagerAdapter.lift = lift;
     pagerAdapter.groupPosition = groupPosition;
     pagerAdapter.childPosition = childPosition;
     ViewPager pager = (ViewPager) view.findViewById(R.id.viewPager);
     pager.setAdapter (pagerAdapter);
     pager.setCurrentItem(0);
     pager.setOnPageChangeListener(pagerAdapter);
     return view;
     
    }
    
    @Override
    public int getChildrenCount(int groupPosition) {
    	return locations.get(groupPosition).liftList.size();
    }
    
    @Override
    public Object getGroup(int groupPosition) {
     return locations.get(groupPosition);
    }
    
    @Override
    public int getGroupCount() {
     return locations.size();
    }
    
    @Override
    public long getGroupId(int groupPosition) {
     return groupPosition;
    }
    
    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
      ViewGroup parent) {
      
     LocationPool location = (LocationPool) getGroup(groupPosition);
     if (view == null) {
      LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inf.inflate(R.layout.widget_location, null);
     }
     ((TextView) view.findViewById(R.id.location)).setText(location.location + " (" + location.liftList.size() + ")");
      
     return view;
    }
    
    @Override
    public boolean hasStableIds() {
     return true;
    }
    
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
     return true;
    }
    
    private class MyPagerAdapter extends PagerAdapter implements OnPageChangeListener {

        public View my_collection;
        public View acceptView;
    	public Lift lift;
        public int groupPosition;
        public int childPosition;


        public int getCount() {
                return 2;
        }

        public Object instantiateItem(View collection, int position) {

                my_collection = collection;
        		LayoutInflater inflater = (LayoutInflater) collection.getContext()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = null;
                switch (position) {
                case 0:
                    view = inflater.inflate (R.layout.widget_lift_item_one, null);
                    TextView txtLiftee = (TextView)view.findViewById(R.id.txtLiftee);
               	 	txtLiftee.setText(lift.liftee);
  //             	 	Toast.makeText(collection.getContext(), "creating view_1 for "+meal.title, Toast.LENGTH_SHORT).show();
               	 	View midLayout = (View)view.findViewById(R.id.liftMidLayout);
	               	 int[] tagT_array = new int[2]; 
	               	 tagT_array[0] = groupPosition;
	               	 tagT_array[1] = childPosition;
	               	 midLayout.setTag(tagT_array);
	               	 ((TextView)view.findViewById(R.id.txtFrom)).setText(lift.from_lat+"-"+lift.from_long);
	               	 ((TextView)view.findViewById(R.id.txtTo)).setText(lift.to);
	               	 ((TextView)view.findViewById(R.id.txtTime)).setText(DataAccess.getHourMinuteStringFromDate(lift.time));
                        break;
                case 1:
                    view = inflater.inflate (R.layout.widget_lift_item_accept, null);
//               	 	Toast.makeText(collection.getContext(), "creating view_2 for "+meal.title, Toast.LENGTH_SHORT).show();
	               	 acceptView = (TextView) view.findViewById(R.id.acceptView);
	               	 int[] tag2_array = new int[2]; 
	               	 tag2_array[0] = groupPosition;
	               	 tag2_array[1] = childPosition;
	               	 acceptView.setTag(tag2_array);
                        break;
                }

        
                ((ViewPager) collection).addView(view, 0);

                return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView((View) arg2);

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == ((View) arg1);

        }

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			switch (position){
			case 0:
//	     	 	Toast.makeText(my_collection.getContext(), "page 2", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				acceptView.performClick();
//				Toast.makeText(my_collection.getContext(), "page 2", Toast.LENGTH_SHORT).show();
				break;
			}
			
		}



    }
}
