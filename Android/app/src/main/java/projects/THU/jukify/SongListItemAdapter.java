package projects.THU.jukify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Another adapter for Songlist
 */
public class SongListItemAdapter extends BaseAdapter {
    /**
     * Stores data about songs
     */
    private  ArrayList<SongListItem> songsData;

    public SongListItemAdapter(ArrayList songsData) {
        this.songsData = songsData;
    }

    @Override
    public int getCount() {
        return songsData.size();

    }

    @Override
    public SongListItem getItem(int position) {

        return songsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Context context = parent.getContext();

        String nameData = getItem(position).getName();
        String albumData = getItem(position).getAlbum();
        String authorData = getItem(position).getArtist();
        if (view == null) {
            SongListItem entry =  getItem(position);


            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.songs_view, null, false);


        }

        TextView name = view.findViewById(R.id.songName);
        TextView album = view.findViewById(R.id.songAlbum);
        TextView artist = view.findViewById(R.id.songArtist);

        name.setText(nameData);
        album.setText(albumData);
        artist.setText(authorData);



        return view;
    }
}
