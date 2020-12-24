package com.asm.assembly;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DebugActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TableLayout tableLayout;
    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        //Initialize Toolbar and TableLayout
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        tableLayout = findViewById(R.id.instruction_table);
        initialise();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_debug, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_step:
                interpreter.debug();
                break;
            case R.id.action_step_over:
                interpreter.stepOver();
                break;
        }
        return true;
    }

    /**INITIALISE*/
    public void initialise()
    {
        //Get the Instructions from the last Activity
        Intent intent = getIntent();
        String message = intent.getStringExtra("TASK");
        String[] tasks = message.split("\n");
        //Empty Lines filtered
        List<String> taskList = Arrays.stream(tasks).filter(str -> !str.equals("")).collect(Collectors.toList());
        tasks = taskList.toArray(new String[taskList.size()]);
        interpreter = new Interpreter(tasks, this, tableLayout);
        //For every instruction create a new TableRow inside TableLayout and inflate a new layout
        for(int i = 0; i < tasks.length; i++)
        {
            TableRow tableRow = new TableRow(getApplicationContext());
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,10,0,0);
            tableRow.setLayoutParams(params);

            View tableCell = getLayoutInflater().inflate(R.layout.table_cell,tableRow , true);
            TextView textView = tableCell.findViewById(R.id.text_container);
            textView.setText(tasks[i]);
            //If its a label
            if(tasks[i].contains(":"))
                textView.setTextColor(ContextCompat.getColorStateList(this, R.color.labelColor));

            tableLayout.addView(tableRow);
        }
    }
}
