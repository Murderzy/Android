package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class activity_rates extends AppCompatActivity {

    private TextView tvJson;
    private String content;

    private List<Rate> rates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        tvJson = findViewById(R.id.tvJson);
        //loadUrl();

        new Thread(this::loadUrl).start();
    }

    private void loadUrl() {
        try (
                InputStream inputStream =
                        new URL("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json")
                                .openStream()) {
            int sym;
            StringBuilder sb = new StringBuilder();
            while ((sym = inputStream.read()) != -1) {
                sb.append((char) sym);
            }
            content = new String(
            sb.toString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8);

            new Thread(this::parseContent).start();
        } catch (MalformedURLException ex) {
            Log.d("loadUrl", "MalformedURLException");
        } catch (IOException ex) {
            Log.d("loadUrl", "IOException");
        }
        catch(android.os.NetworkOnMainThreadException ex)
        {
            Log.d("loadUrl","NetworkException");
        }
    }

    private void parseContent()
    {
        rates = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        try {
            JSONArray jrates = new JSONArray(content);
            for(int i = 0; i < jrates.length(); ++i)
            {
                //JSONObject rate = jrates.getJSONObject(i);
                rates.add(new Rate(jrates.getJSONObject(i)));
                //str.append(rates.toString());
            }
            //new Thread(this::showRates).start();
            runOnUiThread(this::showRates);
//                 runOnUiThread(()->
//               tvJson.setText(str.toString()));
        } catch (JSONException e) {
            Log.d("",e.getMessage());
        }
    }

    private void showRatesTxt()
    {
//        runOnUiThread(()->
//                tvJson.setText(str.toString()));
    }

    private void showRates()
    {
        TextView  t = new TextView(this);
        Drawable ratesBg = AppCompatResources.getDrawable(getApplicationContext(),R.drawable.rates_bg);
        Drawable oddRatesBg = AppCompatResources.getDrawable(getApplicationContext(),R.drawable.rates_bg_odd);
        LinearLayout container = findViewById(R.id.ratesContainer);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(7,5,7,5);

        LinearLayout.LayoutParams oddLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        oddLayoutParams.setMargins(7,5,7,5);
        oddLayoutParams.gravity = Gravity.END;
        String d ;
        boolean isOdd = true;
        for(Rate rate : this.rates)
        {
            d = rate.getExchangeDate();
            TextView tv = new TextView(this);
            tv.setText(rate.getTxt() + "\n" + rate.getCc() + " "+rate.getRate());
            isOdd = !isOdd;
            if(isOdd)
            {
                tv.setBackground(oddRatesBg);
                tv.setLayoutParams(oddLayoutParams);
            }
            //tv.setBackground(ratesBg);
            tv.setPadding(7,5,7,5);
            //tv.setLayoutParams(layoutParams);
            container.addView(tv);
            t.setText(d);
        }

    }

    class Rate{  //  ORM for JSON
        private int r030;
        private String txt;
        private double rate;
        private String cc;
        private String exchangeDate;

        public int getR030() {
            return r030;
        }

        public void setR030(int r030) {
            this.r030 = r030;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public String getCc() {
            return cc;
        }

        public void setCc(String cc) {
            this.cc = cc;
        }

        public String getExchangeDate() {
            return exchangeDate;
        }

        public void setExchangeDate(String exchangeDate) {
            this.exchangeDate = exchangeDate;
        }

        public Rate(JSONObject obj) throws JSONException {
            setR030(obj.getInt("r030"));
            setTxt(obj.getString("txt"));
            setRate(obj.getDouble("rate"));
            setCc(obj.getString("cc"));
            setExchangeDate(obj.getString("exchangedate"));
        }
    }

}

/*Работа с Internet

Основной объект URL("адрес")
Особенности:
Работа с сетью не разрещается из основного (UI) потока. Это приводит к
исключению android.os.NetworkOnMainThreadException
решение - запуск в отдельном потоке
для доступа к Интернету неоходимо системное разрешение
решение - указать в манифесте <uses-permission android:name="android.permission.INTERNET"/>
из другого потока нельзя обращаться к элементам интерфейса
(в том числе менять текст)
решение - runOnUiThread(()->
            tvJson.setText(sb.toString()));

по стандартам http передача данных производится в кодировке ascii()
а контент как правило реализуется в utf-8
решение - полученую строку нужно перекодировать: преобразовать в байты по кодировке iso
и создать из байт-массива новую строку по utf-8
 */