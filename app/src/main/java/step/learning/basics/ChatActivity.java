package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private final String CHAT_URL = "https://diorama-chat.ew.r.appspot.com/story";
    private String content;

    private List<activity_rates.Rate> msg;
    private LinearLayout chatContainer;

    private EditText etMessage;

    private EditText etAuthor;

    private ChatMessage chatMessage;

    private List<ChatMessage> chatMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatContainer = findViewById(R.id.chatContainer);

        etAuthor = findViewById(R.id.etUserName);
        etMessage = findViewById(R.id.etMessage);
        new Thread(this::loadUrl).start();

        findViewById(R.id.imageButton).setOnClickListener(this::sendButtonClick);
    }

    private void sendButtonClick(View view)
    {
        this.chatMessage = new ChatMessage();
        chatMessage.setAuthor(etAuthor.getText().toString());
        chatMessage.setTxt(etMessage.getText().toString());
        new Thread(this::postChatMessage).start();
    }

    private void postChatMessage()
    {
        try{
            URL url = new URL(CHAT_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Accept","*/*");
            urlConnection.setChunkedStreamingMode(0);


            //body
            OutputStream body = urlConnection.getOutputStream();
            body.write(String.format("{\"author\":\"%s\",\"txt\":\"%s\"}",chatMessage.getAuthor(),
                    chatMessage.getTxt()).getBytes());
            body.flush();
            body.close();

            //send
            int responseCode = urlConnection.getResponseCode();
            if(responseCode != 200)
            {
                Log.d("postChatMessage","Response code: "+ responseCode);
                return;
            }
            InputStream reader = urlConnection.getInputStream();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int len;
            while((len = reader.read(chunk)) != -1)
            {
                bytes.write(chunk,0,len);
            }

            Log.d("postChatMessage",new String(bytes.toByteArray(),StandardCharsets.UTF_8));
            bytes.close();
            reader.close();
            urlConnection.disconnect();

        }
        catch(Exception ex)
        {
            Log.d("postChatMessage",ex.getMessage());
        }
        loadUrl();

    }


    private void loadUrl() {
        try (InputStream inputStream = new URL(CHAT_URL).openStream()) {
            int sym;
            StringBuilder sb = new StringBuilder();
            while ((sym = inputStream.read()) != -1) {
                sb.append((char) sym);
            }
            content = new String(
                    sb.toString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8);

            new Thread(this::parseContentMsg).start();
            //runOnUiThread(this::showChatMsg);
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
//        msg = new ArrayList<>();
//        StringBuilder str = new StringBuilder();
//        try {
//            JSONArray jrates = new JSONArray(content);
//            for(int i = 0; i < jrates.length(); ++i)
//            {
//                //JSONObject rate = jrates.getJSONObject(i);
//                msg.add(new activity_rates.Rate(jrates.getJSONObject(i)));
//                //str.append(rates.toString());
//            }
//            //new Thread(this::showRates).start();
//            runOnUiThread(this::showChatMsg);
////                 runOnUiThread(()->
////               tvJson.setText(str.toString()));
//        } catch (JSONException e) {
//            Log.d("",e.getMessage());
//        }
    }

    private void showChatMsg()
    {
        TextView tv = new TextView(this);
        String msg="";

        for(ChatMessage chatMessage : this.chatMessages)
        {
            msg += chatMessage.getMoment() +":"+chatMessage.getTxt()+"\n";
        }


        tv.setText(msg);
        chatContainer.addView(tv);
    }


    private void parseContentMsg()
    {

        try {    JSONObject js = new JSONObject(content);                        // получения массива
             JSONArray jRates = js.getJSONArray("data");               // с основного объекта
            if("success".equals(js.get("status"))) {
                // проверка статуса
                chatMessages = new ArrayList<>();
                  for(int i=0;i<jRates.length();++i)
                  {  chatMessages.add(new ChatMessage(jRates.getJSONObject(i)));
                  }
                  runOnUiThread(this::showChatMsg);
            }
            else {
                Log.d("parseContent", "Server responses status:"+js.getString(("status")));
            }
        }
        catch (JSONException e){    Log.d("parseContent",e.getMessage());}




    }

    private static class ChatMessage{
        private UUID id;
        private String author;
        private String txt;
        private Date moment;
        private UUID idReply;
        private String replyPreview;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }

        public Date getMoment() {
            return moment;
        }

        public void setMoment(Date moment) {
            this.moment = moment;
        }

        public UUID getIdReply() {
            return idReply;
        }

        public void setIdReply(UUID idReply) {
            this.idReply = idReply;
        }

        public String getReplyPreview() {
            return replyPreview;
        }

        public void setReplyPreview(String replyPreview) {
            this.replyPreview = replyPreview;
        }

        public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy KK:mm:ss a", Locale.US);

        public ChatMessage() {
        }

        public ChatMessage(JSONObject obj) throws JSONException {
            setId(UUID.fromString(obj.getString("id")));
            setAuthor(obj.getString("author"));
            setTxt(obj.getString("txt"));
            try{
                setMoment(dateFormat.parse(obj.getString("moment")));
            } catch (ParseException ex) {
                throw new JSONException("Invalid moment format" + obj.getString("moment"));
            }

            //optional
            if(obj.has("idReply"))
            {
                setIdReply(UUID.fromString(obj.getString("idReply")));
            }
            if(obj.has("replyPreview"))
            {
                setReplyPreview(obj.getString("replyPreview"));
            }
        }
    }
}