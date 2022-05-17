package projects.THU.jukify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter for song queue
 */
public class ListCustomAdapter extends ArrayAdapter {

    /**
     * Data of playlists
     */
    private ArrayList<HashMap<String,String>> playListData;
    private Context context;
    private int resource;
    private HashMap<String,String> hashMap;
    int count;

    /**
     * Constructor
     * @param context Application context
     * @param resource Resouce for view
     * @param objects Objects to be displayed
     */
    public ListCustomAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects){
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.playListData=objects;
        this.count = objects.size();
    }

    @Override
    public int getCount(){
        return count;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if(listItemView == null)
            listItemView = LayoutInflater.from(context).inflate(R.layout.playlist_view_item,parent,false);

        HashMap<String, String> playList = playListData.get(position);

        ImageView image = (ImageView)listItemView.findViewById(R.id.playlistCover_img);
        Picasso.get().load(playList.get("url")).into(image);

        TextView name = (TextView) listItemView.findViewById(R.id.playlistName);
        name.setText(playList.get("name"));

        TextView track = (TextView) listItemView.findViewById(R.id.playlistTrack);
        track.setText(playList.get("total"));

        return listItemView;
    }
}
