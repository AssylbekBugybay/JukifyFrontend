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
 * Adapter for song data
 */
public class SongAdapter extends ArrayAdapter {
    /**
     * Stores data about songs
     */
    private ArrayList<HashMap<String,String>> songsData;
    private Context ctx;
    private int resource;
    private HashMap<String,String> hashmap;
    int count;

    public SongAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects){
        super(context, resource, objects);
        this.ctx=context;
        this.resource=resource;
        this.songsData=objects;
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
            listItemView = LayoutInflater.from(ctx).inflate(R.layout.songs_view,parent,false);

        HashMap<String, String> song = songsData.get(position);

        ImageView image = (ImageView)listItemView.findViewById(R.id.songview_img);
        try {
            Picasso.get().load(song.get("url")).into(image);
        } catch (Exception e){
            e.printStackTrace();
        }
        TextView name = (TextView) listItemView.findViewById(R.id.songName);

        TextView album = (TextView) listItemView.findViewById(R.id.songAlbum);
        TextView artist = (TextView) listItemView.findViewById(R.id.songArtist);

        name.setText(song.get("name"));
        album.setText(song.get("album"));
        artist.setText(song.get("artist"));

        return listItemView;
    }
}