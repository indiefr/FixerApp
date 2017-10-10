package com.creatio.fixer.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.creatio.fixer.Objects.OEvidence;
import com.creatio.fixer.Objects.OPieces;
import com.creatio.fixer.Pieces;
import com.creatio.fixer.R;
import com.facebook.internal.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Layge on 03/07/2017.
 */


public class ADEvidence extends RecyclerView.Adapter<ADEvidence.MyViewHolder> {
    Context context;
    ArrayList<OEvidence> list;
    String id_sale;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public ImageView img;
        public ImageButton btnDelete;

        public MyViewHolder(View view) {
            super(view);

            btnDelete  = (ImageButton) view.findViewById(R.id.btnDelete);
            img        = (ImageView) view.findViewById(R.id.img);
            card       = (CardView) view.findViewById(R.id.card);
        }
    }

    public ADEvidence(Context context, ArrayList<OEvidence> list, String id_sale) {
        this.context = context;
        this.list = list;
        this.list = list;
        this.id_sale = id_sale;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //Llenado
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("La imagen seleccionada se eliminara de la base de datos.");
                alert.setTitle("¿Estás seguro?");
                alert.setPositiveButton("Si, seguro.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id_evidence = list.get(position).getId_evidence();
                        AndroidNetworking.upload("http://api.fixerplomeria.com/v1/DeleteEvidence")
                                .addMultipartParameter("id_evidence", id_evidence)
                                .setPriority(Priority.MEDIUM)
                                .build().getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("UPLOADIMAGE", response);
                                list.remove(position);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onError(ANError error) {
                                // handle error
                                Log.e("UPLOADIMAGE error", error.toString());
                            }
                        });
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();


            }
        });
        Log.e("Image",list.get(position).getName());
        Picasso.with(context)
                .load(list.get(position).getName())
                .error(R.drawable.banio)
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_evidence, parent, false);


        return new MyViewHolder(v);
    }
}
