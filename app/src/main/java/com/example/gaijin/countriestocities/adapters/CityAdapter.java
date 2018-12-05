package com.example.gaijin.countriestocities.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.gaijin.countriestocities.City;
import com.example.gaijin.countriestocities.R;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<City> cities;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    public CityAdapter(List<City> cities) {
        this.cities = cities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.city_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String city = cities.get(i).getName();
        viewHolder.cityName.setText(city);
    }


    @Override
    public int getItemCount() {
        return cities.size();
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

        @BindView(R.id.city_name)
        TextView cityName;
        @BindView(R.id.city_card)
        CardView cityCard;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.city_name, R.id.city_card})
        public void onClick(View view) {
            if (ItemClickListener != null) {
                ItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

}
