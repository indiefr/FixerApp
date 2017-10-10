package com.creatio.fixer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.SupportedCardTypesView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;

public class CardForm extends AppCompatActivity implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener {

    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY};
    Bundle extras;
    private SupportedCardTypesView mSupportedCardTypesView;
    private Button btnPagar;
    protected com.braintreepayments.cardform.view.CardForm mCardForm;
    private SharedPreferences pref;
    private boolean oxxo = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        pref = PreferenceManager.getDefaultSharedPreferences(CardForm.this);

        btnPagar = (Button) findViewById(R.id.btnPagar);
        mSupportedCardTypesView = (SupportedCardTypesView) findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        extras = getIntent().getExtras();
        oxxo = extras.getBoolean("oxxo");

        if (oxxo){
            SaveOrder();
        }else{
            if (pref.getBoolean("conekta", false)) {
                mCardForm.setVisibility(View.GONE);
                SaveOrder();

            }
        }
        mCardForm = (com.braintreepayments.cardform.view.CardForm) findViewById(R.id.card_form);
        mCardForm.isCardScanningAvailable();
        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .postalCodeRequired(false)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("")
                .actionLabel("ENVIAR")
                .setup(this);

        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CardForm.this);

                if (mCardForm.isValid()) {
                    Activity activity = CardForm.this;

                    Conekta.setPublicKey("key_BA9Y55Dxqy5UEzvyLz63cxw");
                    Conekta.setApiVersion("1.0.0");
                    Conekta.collectDevice(activity);

                    Card card = new Card(
                            pref.getString("name", "No registro") + " " + pref.getString("last_name", "Sin registro"),
                            mCardForm.getCardNumber(), mCardForm.getCvv(),
                            mCardForm.getExpirationMonth(),
                            mCardForm.getExpirationYear());
                    Token token = new Token(activity);

                    token.onCreateTokenListener(new Token.CreateToken() {
                        @Override
                        public void onCreateTokenReady(JSONObject data) {
                            try {
                                //Send the id to the webservice.
                                SaveOrder();

                            } catch (Exception err) {
                                //Do something on error
                            }
                        }
                    });

                    token.create(card);
                } else {
                    mCardForm.validate();
                    Toast.makeText(CardForm.this, "Invalido", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void SaveOrder() {
        final ProgressDialog dialog = new ProgressDialog(CardForm.this);
        dialog.setMessage("Guardando información");
        dialog.show();
        final Map<String, ?> allEntries = pref.getAll();
        String services = "";
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().contains("id_service")) {
                services += entry.getValue().toString() + ",";
            }
        }
        services = services.replaceFirst(".$", "");
        final String id_user = pref.getString("id_user", "0");
        double total = Float.parseFloat(extras.getString("subtotal")) * 1.16;
        //process Intent......

        AndroidNetworking.post("http://api.fixerplomeria.com/v1/SaveOrder")
                .addBodyParameter("id_specialist", extras.getString("id_specialist"))
                .addBodyParameter("init_date", extras.getString("init_date"))
                .addBodyParameter("hour_date", String.valueOf(extras.getInt("hour_date")))
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("services", services)
                .addBodyParameter("subtotal", extras.getString("subtotal"))
                .addBodyParameter("total", String.valueOf(total))
                .addBodyParameter("lat_lng", extras.getString("latlng"))
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CardForm.this);
                String name = pref.getString("name", "Sin registro") + " " + pref.getString("last_name", "Sin registro");
                Helper.SendNotification(extras.getString("id_specialist"), "Solicitud de servicio", "El usuario " + name + " esta solicitado un servicio.", "0");
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    if (entry.getKey().contains("id_service")) {
                        SharedPreferences.Editor edit = pref.edit();
                        edit.remove(entry.getKey());
                        edit.putInt("badge", 0);
                        edit.apply();
                    }
                }
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        final String id_sale = object.optString("id_sale");
                        Helper.WriteLog(CardForm.this, "Solicitó servicio con número de orden: " + id_sale);
                        if (oxxo){
                            ConektaOrderOxxo(id_sale);
                        }else{
                            ConektaOrder(id_sale);
                        }

                        dialog.dismiss();
                        // custom dialog
                        final Dialog dialog = new Dialog(CardForm.this);
                        dialog.setContentView(R.layout.alert_fixer);
                        // set the custom dialog components - text, image and button
                        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                        TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                        txtTitle.setText("¡Gracias por usar nuestro servicio!");
                        txtMsj.setText("Tu solicitud ha sido guardada exitosamente No. " + id_sale);


                        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
                        btnCancelar.setVisibility(View.INVISIBLE);
                        // if button is clicked, close the custom dialog
                        btnAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Login error", error.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Helper.ShowAlert(CardForm.this, "¡Gracias por usar nuestro servicio!", "Tu solicitud ha sido guardada exitosamente", 0);

    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
        }
    }

    @Override
    public void onCardFormSubmit() {
        if (mCardForm.isValid()) {
            Activity activity = CardForm.this;

            Conekta.setPublicKey("key_BA9Y55Dxqy5UEzvyLz63cxw");
            Conekta.setApiVersion("1.0.0");
            Conekta.collectDevice(activity);

            Card card = new Card(
                    pref.getString("name", "No registro") + " " + pref.getString("last_name", "Sin registro"),
                    mCardForm.getCardNumber(), mCardForm.getCvv(),
                    mCardForm.getExpirationMonth(),
                    mCardForm.getExpirationYear());
            Token token = new Token(activity);

            token.onCreateTokenListener(new Token.CreateToken() {
                @Override
                public void onCreateTokenReady(JSONObject data) {
                    try {
                        //Send the id to the webservice.
                        //SaverOrder();
                        Log.e("Dta", data.toString());
                        AndroidNetworking.post("http://api.fixerplomeria.com/v1/ConektaCustumer")
                                .addBodyParameter("id_user", pref.getString("id_user", "0"))
                                .addBodyParameter("token_id", data.getString("id"))
                                .addBodyParameter("phone", mCardForm.getMobileNumber())
                                .setPriority(Priority.IMMEDIATE)
                                .build().getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("Dta desc", response);
                                if (response.contains("id") || response.contains("creado")) {
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putBoolean("conekta", true);
                                    editor.apply();
                                    SaveOrder();

                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
                    } catch (Exception err) {
                        //Do something on error
                    }
                }
            });

            token.create(card);
        } else {
            mCardForm.validate();
            Toast.makeText(this, "Invalido", Toast.LENGTH_SHORT).show();

        }
    }

    public void ConektaOrder(String id_sale) {
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/ConektaOrder")
                .addBodyParameter("id_user", pref.getString("id_user", "0"))
                .addBodyParameter("id_sale",id_sale)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Order desc", response);

            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
    public void ConektaOrderOxxo(String id_sale) {
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/ConektaOrderOxxo")
                .addBodyParameter("id_user", pref.getString("id_user", "0"))
                .addBodyParameter("id_sale",id_sale)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Order reference", response);


            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

//        if (item.getItemId() == R.id.card_io_item) {
//            mCardForm.scanCard(this);
//            return true;
//        }

        return false;
    }

}
