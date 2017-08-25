package com.creatio.fixer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);
        btnPagar = (Button)findViewById(R.id.btnPagar);
        mSupportedCardTypesView = (SupportedCardTypesView) findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        extras = getIntent().getExtras();
        mCardForm = (com.braintreepayments.cardform.view.CardForm) findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("La tarjeta quedará guardada para futuras compras.")
                .actionLabel("ENVAR")
                .setup(this);
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Helper.InitOrder(extras.getString("id_sale","0"),"0");
                // Helper.SendNotification(extras.getString("id_sepecialist","0"),"Orden autorizada","Solicitud de servicio autorizada","0");
                // finish();
                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CardForm.this);
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
                        .addBodyParameter("hour_date", String.valueOf(extras.getString("hour_date")))
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
                        Helper.SendNotification(extras.getString("id_specialist"),"Solicitud de servicio", "El usuario " + name + " esta solicitado un servicio.","0");
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
                                Helper.ShowAlert(CardForm.this, "¡Gracias por usar nuestro servicio!", "Tu solicitud ha sido guardada con el número de orden: " + id_sale, 0);
                                finish();
//                                Activity activity = CardForm.this;
//
//                                Conekta.setPublicKey("key_BA9Y55Dxqy5UEzvyLz63cxw");
//                                Conekta.collectDevice(activity);
//
//                                Card card = new Card("Fulanito Pérez", "4242424242424242", "332", "11", "2020");
//                                Token token = new Token(activity);
//
//                                token.onCreateTokenListener(new Token.CreateToken() {
//                                    @Override
//                                    public void onCreateTokenReady(JSONObject data) {
//                                        try {
//                                            String token_card = data.optString("id");
//                                            //Send the id to the webservice.
//
//                                        } catch (Exception err) {
//                                            //Do something on error
//                                        }
//                                    }
//                                });
//
//                                token.create(card);
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
        });
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
            Toast.makeText(this, "Pago autorizado", Toast.LENGTH_SHORT).show();
            Helper.ShowAlert(CardForm.this,"Finalizado","El pago autorizado y el especialista ha recibido la orden",0);
            finish();
        } else {
            mCardForm.validate();
            Toast.makeText(this, "Invalido", Toast.LENGTH_SHORT).show();

        }
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
