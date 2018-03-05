package com.creatio.fixer.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.creatio.fixer.Helper;
import com.creatio.fixer.Objects.OPieces;
import com.creatio.fixer.Pieces;
import com.creatio.fixer.R;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Layge on 03/07/2017.
 */


public class ADPieces extends RecyclerView.Adapter<ADPieces.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<OPieces> list;
    ArrayList<OPieces> filteredData;
    String id_sale, id_service;
    private ItemFilter mFilter = new ItemFilter();

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<OPieces> listf = list;

            int count = listf.size();
            final ArrayList<OPieces> nlist = new ArrayList<OPieces>(count);

            String filterableString, secondFilterable;

            for (int i = 0; i < count; i++) {
                filterableString = listf.get(i).getName();
                secondFilterable = listf.get(i).getCode();
                if (filterableString.toLowerCase().contains(filterString) || secondFilterable.toLowerCase().contains(filterString)) {
                    nlist.add(new OPieces(listf.get(i).getId_piece(), listf.get(i).getName(), listf.get(i).getDescription(), listf.get(i).getId_store(), listf.get(i).getStatus(), listf.get(i).getPrice(), listf.get(i).getName_store(), listf.get(i
                    ).getImage(), listf.get(i).getCode()));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<OPieces>) results.values;
            notifyDataSetChanged();
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView countryText;
        public TextView popText;
        public TextView txtName;
        public TextView txtFerr;
        public TextView txtPrice;
        public TextView txtCode;
        public CardView card;
        public ImageView imgPiece;

        public MyViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtCode = (TextView) view.findViewById(R.id.txtCode);
            txtFerr = (TextView) view.findViewById(R.id.txtFerr);
            card = (CardView) view.findViewById(R.id.card);
            imgPiece = (ImageView) view.findViewById(R.id.imgPiece);
        }
    }

    public ADPieces(Context context, ArrayList<OPieces> list, String id_service, String id_sale) {
        this.context = context;
        this.list = list;
        this.filteredData = list;
        this.id_sale = id_sale;
        this.id_service = id_service;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://api.fixerplomeria.com/v1/";
                if (Helper.debug) {
                    url = "http://apitest.fixerplomeria.com/v1/";
                }
                AndroidNetworking.post(url + "InsertPieces")
                        .addBodyParameter("id_sale", id_sale)
                        .addBodyParameter("id_service", id_service)
                        .addBodyParameter("id_piece", filteredData.get(position).getId_piece())
                        .build().getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
                ((Pieces) context).Cerrar();
            }
        });
        holder.txtPrice.setText("$ " + filteredData.get(position).getPrice());
        holder.txtName.setText(filteredData.get(position).getName());
        holder.txtFerr.setText(filteredData.get(position).getName_store());
        holder.txtCode.setText(filteredData.get(position).getCode());
        Glide.with(context)
                .load(list.get(position).getImage())
                .error(R.drawable.tuberia_dummy)
                .into(holder.imgPiece);
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_pieces, parent, false);


        return new MyViewHolder(v);
    }
}
