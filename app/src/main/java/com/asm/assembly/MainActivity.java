package com.asm.assembly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.asm.assembly.interpreter.Operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    private Toolbar toolbar;
    private EditText editText;

    private Dialog openDialog;
    private FilesAdapter filesAdapter;
    private RecyclerView recyclerView;

    private static final String TEMPLATE = "cmain:\n";
    private long delay = 5000;
    private long last_edit = 0;
    private Handler handler;
    /*private Runnable inputChecker = new Runnable() {
        @Override
        public void run() {
            if(System.currentTimeMillis() > (last_edit + delay - 500))
            {
                //DO STUFF
                SpannableStringBuilder ssb = tokenizeKeyWords(editText.getText().toString());
                int currentIndex = editText.getSelectionEnd();
                try {
                    editText.setText(ssb);
                    editText.setSelection(currentIndex);
                }catch (Exception e)
                {
                    Log.d("error", "Keyword tokenizer error setting the selection cursor");
                    editText.setSelection(currentIndex - 1);
                }
            }
        }
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        editText = findViewById(R.id.plain_text_input);
        editText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE|
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setTypeface(Typeface.MONOSPACE);

        //Set template string to edittext
        editText.setText(TEMPLATE);

        handler = new Handler();

        //when we change the text find keywords which we can highlight with a color
        editText.addTextChangedListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_run:
                Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
                String [] parts = editText.getText().toString().split("\n");
                String message = Arrays.stream(parts).filter(n -> !n.contains(".asm")).reduce((a, b) -> a + "\n" + b).get();
                intent.putExtra("TASK", message);
                startActivity(intent);
                break;
            case R.id.action_save:
                //Inflate save dialog popup
                View saveDialog = getLayoutInflater().inflate(R.layout.dialog_save_popup, null);

                //Create new custom AlertDialog with adb
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setView(saveDialog);

                final EditText fileNameText = saveDialog.findViewById(R.id.file_name);
                adb.setTitle("Save");
                adb.setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        save(fileNameText.getText().toString());
                    }
                });

                adb.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //Create and show
                AlertDialog alertDialog = adb.create();
                alertDialog.show();
                break;
            case R.id.action_open:
                prepareFileRecyclerView();
                break;
        }
        return true;
    }

    public void save(String fileName)
    {
        try {
            OutputStreamWriter out =
                    new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            out.write(editText.getText().toString());
            out.close();
            Snackbar.make(editText, "Saved", Snackbar.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Snackbar.make(editText, "Error saving file", Snackbar.LENGTH_SHORT).show();
        }
    }

    public String Open(String fileName)
    {
        String content = "";
        if (FileExists(fileName)) {
            try {
                InputStream in = openFileInput(fileName);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    return reader.lines().filter(s -> s != null).reduce((a, b) -> a + "\n" + b).get();
                }
            } catch (FileNotFoundException e) {
                Snackbar.make(editText, "File: " + fileName + "could not be located", Snackbar.LENGTH_LONG).show();
            }catch (Exception e)
            {
                Snackbar.make(editText, "Unexpected Error occured", Snackbar.LENGTH_LONG).show();
            }
        }
        return "";
    }

    public boolean FileExists(String name) {
        return getBaseContext().getFileStreamPath(name).exists();
    }

    public SpannableStringBuilder tokenizeKeyWords(String text)
    {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        String [] instructions = text.split("\n");
        try {
            for (int i = 0; i < instructions.length; i++) {
                String inst = instructions[i];
                String[] structs = getInstructionSet(inst);
                SpannableString spannable;

                if (structs[0].contains("file:") || structs[0].equals("section")) {
                    String stream = Arrays.stream(structs).reduce((a, b) -> a + " " + b).get();
                    spannable = new SpannableString(stream);
                    ForegroundColorSpan fc = new ForegroundColorSpan(
                            getApplicationContext().getColor(R.color.sectionColor));
                    spannable.setSpan(fc, 0, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    ssb.append(spannable);
                } else {

                    if (structs.length == 1) // if its a label
                    {
                        if (structs[0].contains(":") || structs[0].equals("ret")) {
                            spannable = new SpannableString(structs[0]);
                            ForegroundColorSpan fc = new ForegroundColorSpan(
                                    getApplicationContext().getColor(R.color.redModifiedStateRegister));
                            spannable.setSpan(fc, 0, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            ssb.append(spannable);
                        } else {
                            ssb.append(structs[0]);
                        }
                    } else if (structs.length == 2) {
                        String opcode = structs[0];
                        if (Arrays.asList(Commands.oneOperandCodes).contains(opcode)) // if its mul, div...
                        {
                            spannable = new SpannableString(opcode);
                            ForegroundColorSpan fc = new ForegroundColorSpan(
                                    getApplicationContext().getColor(R.color.colorFlagsBackground));
                            spannable.setSpan(fc, 0, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            ssb.append(spannable);
                            ssb.append(" ");
                            spannable = new SpannableString(structs[1]);
                            fc = new ForegroundColorSpan(
                                    getApplicationContext().getColor(R.color.colorSelectedInstruction));
                            spannable.setSpan(fc, 0, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            ssb.append(spannable);
                        } else {
                            ssb.append(structs[0] + " " + structs[1]);
                        }
                    } else if (structs.length == 3) {
                        String opcode = structs[0];
                        if (Arrays.asList(Commands.twoOperandCodes).contains(opcode)) // if add, sub...
                        {
                            spannable = new SpannableString(opcode);
                            ForegroundColorSpan fc = new ForegroundColorSpan(
                                    getApplicationContext().getColor(R.color.colorFlagsBackground));
                            spannable.setSpan(fc, 0, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            ssb.append(spannable);
                            ssb.append(" ");
                            spannable = new SpannableString(structs[1]);
                            fc = new ForegroundColorSpan(
                                    getApplicationContext().getColor(R.color.colorSelectedInstruction));
                            spannable.setSpan(fc, 0, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            ssb.append(spannable);
                            ssb.append(", ");
                            spannable = new SpannableString(structs[2]);
                            spannable.setSpan(fc, 0, spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            ssb.append(spannable);

                        } else {
                            ssb.append(structs[0] + " " + structs[1] + ", " + structs[2]);
                        }
                    }
                }
                if (i != instructions.length - 1)
                    ssb.append("\n");
            }
        }catch (Exception e)
        {
            Log.d("ERROR", "error tokenizing keywords");
        }
        return ssb;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /**handler.removeCallbacks(inputChecker);*/
    }

    @Override
    public void afterTextChanged(Editable s) {

        if(s.length() > 0)
        {
            //DO STUFF
            /**last_edit = System.currentTimeMillis();
            handler.postDelayed(inputChecker, delay);
             */
        }
    }

    /**OPEN A FILE DIRECTORY DIALOG*/
    public void prepareFileRecyclerView()
    {
        openDialog = new Dialog(this);
        openDialog.setContentView(R.layout.dialog_open_popup);
        recyclerView = openDialog.findViewById(R.id.my_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        filesAdapter = new FilesAdapter(prepareFiles(), this);
        recyclerView.setAdapter(filesAdapter);

        openDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        openDialog.show();
    }

    @Override
    public void onClick(View v) {
        //RECYCLER VIEW ITEM CLICK LISTENER
        if(v.getId() == R.id.card_view_item) {
            String fileName = ((TextView) v.findViewById(R.id.file_name)).getText().toString();
            System.out.println(fileName);
            String content = Open(fileName);
            editText.setText(content);
            openDialog.dismiss();
        }else if(v.getId() == R.id.delete_button)
        {
            //Get position of the view from the tag, which was given in FileAdapter
            final int pos = (int) v.getTag();
            View cardView = recyclerView.getLayoutManager().findViewByPosition(pos);
            //Get the fileName of the file
            final String fileName = ((TextView) cardView.findViewById(R.id.file_name)).getText().toString();



            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Delete " + fileName + "?");
            //alertDialog.setMessage("Delete file?");

            //Delete Button
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    boolean deleted = getBaseContext().deleteFile(fileName);
                    if(deleted) {
                        filesAdapter.notifyItemRemoved(pos);
                    }else
                    {
                        Snackbar.make(recyclerView, "deleting " + fileName + " was not successful",
                                Snackbar.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });

            //Cancel Button
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    /**CHECK FILE AVAILABILITY*/
    private String[] prepareFiles()
    {
        File directory;
        directory = getFilesDir();
        File[] files = directory.listFiles();
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName();
        }
        return names;
    }


    /**INSTRUCTION SET*/
    private String [] getInstructionSet(String instruction)
    {
        try {
            //if its not a label
            if (!instruction.contains(":")) {
                //If its an instruction with two operands eg. MOV EAX, 0x1
                if (instruction.contains(",")) {
                    String command = instruction.split(",")[0].split(" ")[0].toLowerCase();
                    String[] operands = instruction.replaceAll(command, "")
                            .replaceAll(" ", "").split(",");
                    for (int i = 0; i < operands.length; i++) {
                        if (!(operands[i].contains("0x") || operands[i].contains("0X")))
                            operands[i] = operands[i].toLowerCase();
                    }
                    return new String[]{command, operands[0], operands[1]};
                } else //INSTRUCTIONS LIKE MUL EBX
                {
                    return new String[]{instruction.split(" ")[0].toLowerCase(),
                            instruction.split(" ")[1].toLowerCase()};
                }
            } else {
                return new String[]{instruction};
            }
        }catch (Exception e)
        {
            return new String[]{instruction};
        }
    }


}
