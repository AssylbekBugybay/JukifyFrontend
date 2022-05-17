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
 * Adapter for party history data
 */
public class PartyHistoryItemAdapter extends ArrayAdapter  {
    /**
     * Stores party data
     */
    private ArrayList<String> partyData;
    /**
     * Stores context of the application
     */
    private Context context;
    /**
     * Stores view resource
     */
    private int resource;
    private boolean check = false;
    int count;

    public PartyHistoryItemAdapter(Context context, int resource, ArrayList<String> objects){
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.partyData=objects;
        this.count = objects.size();
    }

    @Override
    public int getCount(){
        return count;
    }

    @Override
    public Object getItem(int position){
        return partyData.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if(listItemView == null)
            listItemView = LayoutInflater.from(context).inflate(R.layout.history_item,parent,false);
        String partyItem = partyData.get(position);
        TextView id = (TextView) listItemView.findViewById(R.id.historyPartyId);
        id.setText(partyData.get(position));
        return listItemView;
    }
}
