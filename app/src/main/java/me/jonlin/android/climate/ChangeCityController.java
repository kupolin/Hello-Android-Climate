package me.jonlin.android.climate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityController extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_city_layout);
        final EditText query_et = findViewById(R.id.queryET);
        // no actionbar
        ImageButton backButton = findViewById(R.id.backButton);

        //setting up listeners
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        query_et.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                //when user clicks done
                String newCity = query_et.getText().toString();
                Intent i = new Intent(getBaseContext(), WeatherController.class);
                i.putExtra("city", newCity);
                setResult(RESULT_OK, i);
                finish();
                return false;
            }
        });

    }
}
