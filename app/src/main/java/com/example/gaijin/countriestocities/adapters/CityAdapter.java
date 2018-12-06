package com.example.gaijin.countriestocities.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.gaijin.countriestocities.R;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<String> cities;

    public List<String> getCities() {
        return cities;
    }

    public CityAdapter() {
    }

    public CityAdapter(List<String> cities) {
        this.cities = cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.city_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String city = cities.get(i);
        try {
            viewHolder.cityName.setText(city);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        int size = 0;
        try {
            size = cities.size();
        } catch (Exception ex) {
        }
        return size;
    }

    /*This interfaces and next function, need for binding on clicked item position*/
    private OnItemClickListener ItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener tasksItemClickListener) {
        this.ItemClickListener = tasksItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView cityName;
        CardView cityCard;

        public ViewHolder(View itemView) {
            super(itemView);
            cityName = (TextView) itemView.findViewById(R.id.city_name);
            cityCard = (CardView) itemView.findViewById(R.id.city_card);
            cityName.setOnClickListener(this);
            cityCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (ItemClickListener != null) {
                ItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

}
