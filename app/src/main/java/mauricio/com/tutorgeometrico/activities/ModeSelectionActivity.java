package mauricio.com.tutorgeometrico.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import mauricio.com.tutorgeometrico.R;

public class ModeSelectionActivity extends AppCompatActivity {

    private AppCompatButton modeAdmin;
    private AppCompatButton modeCommonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mode_selection);

        modeAdmin = findViewById(R.id.mode_admin);
        modeCommonUser = findViewById(R.id.mode_user);

        modeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ModeSelectionActivity.this, MainActivity.class));
            }
        });

        modeCommonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ModeSelectionActivity.this, UserActivity.class));
            }
        });

    }
}
