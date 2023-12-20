package com.example.matedicine.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.matedicine.databinding.ItemMedicineBinding;
import com.example.matedicine.model.Medicine;

import java.util.ArrayList;

public class MedicinAdapter extends RecyclerView.Adapter<MedicinAdapter.ViewHolder> {
    private ArrayList<Medicine> medicines;
    private Activity context;

    public interface OnItemClickListener {
        void onItemClick(String id);
    }

    private OnItemClickListener listener;
    public MedicinAdapter(ArrayList<Medicine> list, Activity context, OnItemClickListener listener){
        this.medicines = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMedicineBinding binding = ItemMedicineBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        Glide.with(context).load(medicine.getImage()).into(holder.binding.imgMed);
        holder.binding.tvSubject.setText(medicine.getTitle());
        holder.binding.tvPills.setText(medicine.getAmount());
        holder.binding.tvTime.setText(medicine.getTime());
        holder.binding.tvEat.setText(medicine.getPills());
        holder.binding.itemCard.setOnClickListener(v -> {
            listener.onItemClick(medicine.getId());
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {
        final ItemMedicineBinding binding;

        ViewHolder(ItemMedicineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
