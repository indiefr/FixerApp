package com.creatio.fixer.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.creatio.fixer.Detalles;
import com.creatio.fixer.Helper;
import com.creatio.fixer.MainActivity;
import com.creatio.fixer.Objects.OPieces;
import com.creatio.fixer.Objects.OServices;
import com.creatio.fixer.R;

import java.util.ArrayList;

/**
 * Created by Layge on 04/07/2017.
 */

public class ADDetalles extends BaseAdapter implements Filterable {
    Context context;
    ArrayList<OServices> list;
    ArrayList<OServices> filteredData;
    Detalles fragment;
    String type = "0";
    private ADDetalles.ItemFilter mFilter = new ADDetalles.ItemFilter();
    public ADDetalles(Context context, ArrayList<OServices> list, ArrayList<OServices> filteredData, Detalles fragment) {
        this.context = context;
        this.list = list;
        this.filteredData = filteredData;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.list_detalles, parent, false);
        final Button btnReparar = (Button) itemView.findViewById(R.id.btnReparar);

        final TextView txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        final TextView txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
        final TextView txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
        final ImageView image_profile = (ImageView)itemView.findViewById(R.id.image_profile);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String service = pref.getString("id_service" + filteredData.get(position).getId_service(), "0");
        btnReparar.setVisibility(View.VISIBLE);
        if (!service.equalsIgnoreCase("0")) {
            btnReparar.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(list.get(position).getImage())
                .error(R.drawable.tuberia_dummy)
                .into(image_profile);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_detail);
                // set the custom dialog components - text, image and button
                TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                txtTitle.setText(list.get(position).getTitle());
                txtMsj.setText(list.get(position).getDesc());

                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                ImageView profile_image = (ImageView) dialog.findViewById(R.id.image_profile);
                profile_image.setImageDrawable(image_profile.getDrawable());

                        // if button is clicked, close the custom dialog
                        btnAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                            }
                        });

                dialog.show();
            }
        });
        btnReparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_fixer);
                // set the custom dialog components - text, image and button
                TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                txtTitle.setText("Tipo de reparación");
                txtMsj.setText("Selecciona el tipo de reparación que el especialista va a realizar.");

                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                btnAceptar.setText("Nueva");
                Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
                btnCancelar.setText("Reinstalación");

                // if button is clicked, close the custom dialog
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        type = "0";
                        fragment.Reparar();
                        ((MainActivity) context).ChangeBadge(filteredData.get(position).getId_service(), type);
                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        type = "1";
                        fragment.Reparar();
                        ((MainActivity) context).ChangeBadge(filteredData.get(position).getId_service(), type);
                    }
                });
                dialog.show();
                v.setVisibility(View.INVISIBLE);


            }
        });
        txtDesc.setText(filteredData.get(position).getDesc());
        txtTitle.setText(filteredData.get(position).getTitle());
        txtPrice.setText(Helper.formatDecimal(Double.parseDouble(String.valueOf(Integer.parseInt(filteredData.get(position).getTime_new()) * 2.23))));
        return itemView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<OServices> listf = list;

            int count = listf.size();
            final  ArrayList<OServices> nlist = new ArrayList<OServices>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = listf.get(i).getTitle();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(new OServices(listf.get(i).getId_service(),listf.get(i).getImage(),listf.get(i).getTitle(),listf.get(i).getDesc(),listf.get(i).getTime_pre(),listf.get(i).getTime_new(),listf.get(i).getType(),listf.get(i).getPieces()));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<OServices>) results.values;
            notifyDataSetChanged();
        }

    }
}
