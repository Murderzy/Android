package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        tvHistory = findViewById(R.id.tvHistory);
        tvResult = findViewById(R.id.tvResult);
        tvHistory.setText("");
        tvResult.setText("0");

        findViewById(R.id.btn0).setOnClickListener(this::digitClick);
        findViewById(R.id.btn1).setOnClickListener(this::digitClick);
        findViewById(R.id.btn2).setOnClickListener(this::digitClick);
        findViewById(R.id.btn3).setOnClickListener(this::digitClick);
        findViewById(R.id.btn4).setOnClickListener(this::digitClick);
        findViewById(R.id.btn5).setOnClickListener(this::digitClick);
        findViewById(R.id.btn6).setOnClickListener(this::digitClick);
        findViewById(R.id.btn7).setOnClickListener(this::digitClick);
        findViewById(R.id.btn8).setOnClickListener(this::digitClick);
        findViewById(R.id.btn9).setOnClickListener(this::digitClick);


        findViewById(R.id.btnPlusMinus).setOnClickListener(this::pmClick);

        findViewById(R.id.btnComma).setOnClickListener(this::dotClick);
    }

    private void dotClick(View v)  //  десятичная точка
    {
        String result = tvResult.getText().toString();
        result += ".";
        tvResult.setText(result);
    }

    private void pmClick(View v)  //  изменение знака
    {
        String result = tvResult.getText().toString();

        if(result.startsWith("-"))
        {
            result = (new StringBuilder(result).insert(0,"")).toString();  //  ?
        }
        else
        {
            result = (new StringBuilder(result).insert(0,"-")).toString();
        }

        tvResult.setText(result);
    }

    private void digitClick(View v)
    {
        String result = tvResult.getText().toString();
        if(result.length()>= 10)return;
        String digit = ((Button)v).getText().toString();


        if(result.equals("0"))
        {
            result=digit;
        }
        else {
            result += digit;
        }

        tvResult.setText(result);
    }
}