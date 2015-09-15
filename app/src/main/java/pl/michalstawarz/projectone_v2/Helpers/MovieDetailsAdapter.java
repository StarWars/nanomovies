package pl.michalstawarz.projectone_v2.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pl.michalstawarz.projectone_v2.MovieDetailFragment;
import pl.michalstawarz.projectone_v2.R;

/**
 * Created by propr_000 on 13.09.2015.
 */
public class MovieDetailsAdapter extends ArrayAdapter {
    public MovieDetailsAdapter(Context context, ArrayList trailers) {
        super(context, 0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_2, parent, false);
        }

        TextView trailerTitle = (TextView) convertView.findViewById(R.id.text1);
        TextView trailerDesc = (TextView) convertView.findViewById(R.id.text2);


        if(getItem(position).getClass() == MovieDetailFragment.Trailer.class) {
            MovieDetailFragment.Trailer trailer = (MovieDetailFragment.Trailer)getItem(position);
            trailerTitle.setText(trailer.name);
            trailerDesc.setText(trailer.site);
        } else if (getItem(position).getClass() == MovieDetailFragment.Review.class) {
            MovieDetailFragment.Review review = (MovieDetailFragment.Review)getItem(position);
            trailerTitle.setText(review.author);
            trailerDesc.setText(review.content);
        }

        return convertView;
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
