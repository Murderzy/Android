package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = findViewById(R.id.exitButton);
        addButton.setOnClickListener(this::exitButtonClick);

        Button ratesButton = findViewById(R.id.ratesButton);
        ratesButton.setOnClickListener(this::ratesButtonClick);

        Button chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(this::chatButtonClick);

        findViewById( R.id.calcButton ).setOnClickListener( this::calcButtonClick ) ;
    }

    private void calcButtonClick( View v ) {
        Intent calcIntent = new Intent( this, CalcActivity.class ) ;
        startActivity( calcIntent ) ;
    }

    private void ratesButtonClick(View v)
    {
        Intent ratesIntent = new Intent(this,activity_rates.class);
        startActivity(ratesIntent);
    }

    private void chatButtonClick(View v)
    {
        Intent chatIntent = new Intent(this,ChatActivity.class);
        startActivity(chatIntent);
    }

    private void exitButtonClick(View v)
    {
//        TextView tvHello = findViewById(R.id.tvHello);
//        String txt = tvHello.getText().toString();
//        txt += "!";
//        tvHello.setText(txt);

        finish();
    }
}