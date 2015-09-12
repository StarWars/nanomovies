package pl.michalstawarz.projectone_v2.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import pl.michalstawarz.projectone_v2.MovieDetailFragment;
import pl.michalstawarz.projectone_v2.R;

/**
 * Created by propr_000 on 13.09.2015.
 */
public class TrailersAdapter extends ArrayAdapter<MovieDetailFragment.Trailer> {
    public  TrailersAdapter(Context context, ArrayList<MovieDetailFragment.Trailer> trailers) {
        super(context, 0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieDetailFragment.Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_2, parent, false);
        }

        TextView trailerTitle = (TextView) convertView.findViewById(R.id.text1);
        TextView trailerDesc = (TextView) convertView.findViewById(R.id.text2);

        trailerTitle.setText(trailer.name);
        trailerDesc.setText(trailer.site);

        return convertView;
    }
}
