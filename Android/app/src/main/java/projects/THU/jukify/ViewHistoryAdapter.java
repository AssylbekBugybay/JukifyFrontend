package projects.THU.jukify;

import android.content.Context;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter for ViewHistory
 */
public class ViewHistoryAdapter extends ArrayAdapter {
    /**
     * Song data
     */
    private ArrayList<HashMap<String,String>> queueData;
    private Context context;
    private int resource;
    private boolean check = false;
    int count;

    public ViewHistoryAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects){
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.queueData=objects;
        this.count = objects.size();
    }

    @Override
    public int getCount(){
        return count;
    }

    @Override
    public Object getItem(int position){
        return queueData.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if(listItemView == null)
            listItemView = LayoutInflater.from(context).inflate(R.layout.historyview_item,parent,false);

        HashMap<String, String> queueItem = queueData.get(position);
        ImageView image = (ImageView)listItemView.findViewById(R.id.history_trackCover_img);
        Picasso.get().load(queueItem.get("trackUrl")).into(image);

        TextView name = (TextView) listItemView.findViewById(R.id.history_trackName);
        name.setText(queueItem.get("trackName"));

        TextView album = (TextView) listItemView.findViewById(R.id.history_trackAlbum);
        album.setText(queueItem.get("trackAlbum"));

        TextView artist = (TextView) listItemView.findViewById(R.id.history_trackArtist);
        artist.setText(queueItem.get("trackArtist"));

        //Enable automatic horizontal scrolling for long texts
        name.setSelected(true);
        album.setSelected(true);
        artist.setSelected(true);

        return listItemView;
    }
}
