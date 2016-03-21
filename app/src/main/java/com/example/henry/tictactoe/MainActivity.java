package com.example.henry.tictactoe;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private boolean turnX = true;
    private boolean gameWon = false;
    private int turns = 0;
    private int xWins = 0;
    private int oWins = 0;
    private int gameField[][] = new int[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFile();
        updateScore();

    }

    public void btnClick(View view) {
        if(gameWon) return;
        //no more empty spots
        if(turns == 9) {
            Toast.makeText(this, "No winner! Please reset game.", Toast.LENGTH_LONG).show();
        }

        ImageButton clickedBtn = (ImageButton) view;
        String btnCord = view.getTag().toString();

        int success = updateField(btnCord);

        //clicked on a box that was already clicked
        if(success == -1) {
            if(turnX)
                Toast.makeText(this,
                        "Box is not empty. X please go again",
                        Toast.LENGTH_SHORT
                ).show();
            else
                Toast.makeText(this,
                        "Box is not empty, O please go again",
                        Toast.LENGTH_SHORT
                ).show();
            return;
        }

        if(turnX) {
            clickedBtn.setImageResource(R.drawable.x);
        }
        else {
            clickedBtn.setImageResource(R.drawable.o);
        }
        turns++; //update turns

        int win = checkWin();
        if(win == 1) {
            if(turnX) {
                Toast.makeText(
                        this,
                        "X wins!!",
                        Toast.LENGTH_LONG
                ).show();
                xWins++;
            }
            else {
                Toast.makeText(
                        this,
                        "O wins!!",
                        Toast.LENGTH_LONG
                ).show();
                oWins++;
            }
            updateScore(); //updates winner score
            gameWon = true;
        }

        if(turns == 9) {
            Toast.makeText(this, "No winner! Please reset game.", Toast.LENGTH_LONG).show();
            return;
        }

        turnX = !turnX;
    }


    //checks if the btn is already marked
    public int updateField(String coordinates) {
        //extracts the x and y coordinates from the string
        int x = Integer.parseInt(coordinates.substring(0,1));
        int y = Integer.parseInt(coordinates.substring(2, coordinates.length()));

        int markerX = 1;
        int markerO = 2;

        if(gameField[x][y] != 0) {
            return -1; //btn clicked was not empty
        }
        else {
            //checks who's turn it is and marks it
            if(turnX) {
                gameField[x][y] = markerX;
            }
            else {
                gameField[x][y] = markerO;
            }
        }
        return 1; //successful
    }

    //updates score box
    public void updateScore() {
        TextView winText = (TextView) findViewById(R.id.xScore);
        winText.setText("X score: " + xWins);
        winText = (TextView) findViewById(R.id.oScore);
        winText.setText("O Score: " + oWins);
    }

    //checks if player has won
    public int checkWin() {
        //check horizontal win
        for(int i = 0; i < gameField.length; i++) {
            if(gameField[i][0] != 0 &&
                    gameField[i][0] == gameField[i][1] &&
                    gameField[i][0] == gameField[i][2]) {
                return 1;
            }
        }

        //check vertical win
        for(int i = 0; i < gameField[0].length; i++) {
            if(gameField[0][i] != 0 &&
                    gameField[0][i] == gameField[1][i] &&
                    gameField[0][i] == gameField[2][i]) {
                return 1;
            }
        }

        //checks for diagonal wins
        if(gameField[0][0] != 0 &&
                gameField[0][0] == gameField[1][1] &&
                gameField[0][0] == gameField[2][2] ||
                gameField[1][1] != 0 &&
                gameField[2][0] == gameField[1][1] &&
                gameField[2][0] == gameField[0][2]) {
            return 1;
        }

        return 0;
    }

    public void newGame(View view) {
        turnX = true;
        turns = 0;
        gameWon = false;
        gameField = new int[3][3];
        clearGameField();
    }

    //resets the imagebuttons
    public void clearGameField() {
        TableLayout table = (TableLayout) findViewById(R.id.table);
        if(table == null) return;
        //loops through table
        for(int i = 0; i < table.getChildCount(); i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            //loops through tablerow
            for(int j = 0; j < row.getChildCount(); j++) {
                ImageButton btn = (ImageButton) row.getChildAt(j);
                btn.setImageResource(0); //reset images
            }
        }
    }

    //opens the file for scores
    public void openFile() {
        Scanner scan;
        try {
            //opens the file
            scan = new Scanner(openFileInput("score.txt"));

            //reads scores
            if(scan.hasNextInt())
                xWins = scan.nextInt();
            if(scan.hasNextInt())
                oWins = scan.nextInt();
        } catch (IOException ioe) {
            Log.d("File", "Could not read");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        PrintStream ps;
        try {
            ps = new PrintStream(openFileOutput("score.txt", MODE_PRIVATE));
            ps.println(xWins);
            ps.print(oWins);
            ps.close();
        } catch(IOException ioe) {
            Log.d("File", "Invalid file");
        }
    }

    //resets the score
    public void resetScore(View view) {
        xWins = 0;
        oWins = 0;
        updateScore();
    }
}
