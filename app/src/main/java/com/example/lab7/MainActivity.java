package com.example.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TicTacToe game;
    MediaPlayer click, gameover, music;
    SharedPreferences settings;

    DrawView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = new TicTacToe();
        click = MediaPlayer.create(this, R.raw.click);
        gameover = MediaPlayer.create(this, R.raw.gameover);
        music = MediaPlayer.create(this, R.raw.background);
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                music.start();
            }
        });
        settings = getSharedPreferences("Settings", MODE_PRIVATE);

        drawView = new DrawView(this, game, click ,gameover, music, settings);

        setContentView(drawView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setttings_menu, menu);
        MenuItem music = menu.findItem(R.id.musicCheck);
        MenuItem sounds = menu.findItem(R.id.soundsCheck);
        music.setChecked(settings.getBoolean("Music", true));
        sounds.setChecked(settings.getBoolean("Sounds", true));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = settings.edit();
        switch(item.getItemId()) {
            case R.id.musicCheck:
                item.setChecked(!item.isChecked());
                editor.putBoolean("Music", item.isChecked());
                editor.apply();

                if (!item.isChecked()) {
                    music.stop();
                    try {
                        music.prepare();
                        music.seekTo(0);
                    }
                    catch (Throwable t) {
                        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else music.start();
                break;
            case R.id.soundsCheck:
                item.setChecked(!item.isChecked());
                editor.putBoolean("Sounds", item.isChecked());
                editor.apply();
                break;

            case R.id.restart:
                game.restart();
                drawView.invalidate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

class DrawView extends View {
    static int width;
    static int height;
    Paint pen = new Paint();

    TicTacToe game;
    int state;

    MediaPlayer click, gameover, music;
    SharedPreferences settings;

    public DrawView(Context context, TicTacToe game, MediaPlayer click, MediaPlayer gameover, MediaPlayer music, SharedPreferences sharedPreferences) {
        super(context);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        this.game = game;
        this.click = click;
        this.gameover = gameover;
        this.music = music;
        settings = sharedPreferences;

        if (settings.getBoolean("Music", true)) this.music.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        pen.setColor(Color.GREEN);
        pen.setStrokeWidth(10);

        // Horizontal lines
        canvas.drawLine(0, width / 3f, width, width / 3f, pen);
        canvas.drawLine(0, 2 * width / 3f, width, 2 * width / 3f, pen);
        // Vertical lines
        canvas.drawLine(width / 3f, 0, width / 3f, width, pen);
        canvas.drawLine(2 * width / 3f, 0, 2 * width / 3f, width, pen);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (game.get(i, j) == 1) drawX(canvas, i * width / 3, j * width / 3);
                else if (game.get(i, j) == 2) drawO(canvas, i * width / 3, j * width / 3);
            }
        }

        String status = "";
        state = game.getStatus();
        pen.setColor(Color.BLUE);
        switch (state) {
            case -1:
                if (game.getPlayer() == 1) status = "Ход первого игрока";
                else status = "Ход второго игрока";
                break;
            case 0:
                status = "Ничья!";
                if (settings.getBoolean("Sounds", true)) gameover.start();
                break;
            case 1:
                canvas.drawLine(width / 6f, 0, width / 6f, width, pen);
                break;
            case 2:
                canvas.drawLine(width / 2f, 0, width / 2f, width, pen);
                break;
            case 3:
                canvas.drawLine(5 * width / 6f, 0, 5 * width / 6f, width, pen);
                break;
            case 4:
                canvas.drawLine(0, width / 6f, width, width / 6f, pen);
                break;
            case 5:
                canvas.drawLine(0, width / 2f, width, width / 2f, pen);
                break;
            case 6:
                canvas.drawLine(0, 5 * width / 6f, width, 5 * width / 6f, pen);
                break;
            case 7:
                canvas.drawLine(0, 0, width, width, pen);
                break;
            case 8:
                canvas.drawLine(0, width, width, 0, pen);
                break;
        }

        if (state > 0 && state < 9) {
            if (settings.getBoolean("Sounds", true)) gameover.start();
            if (game.getPlayer() == 2) status = "Первый игрок победил!";
            else status = "Второй игрок победил!";
        }

        pen.setColor(Color.BLACK);
        pen.setTextSize(50);
        canvas.drawText(status, 10, width + 50, pen);
    }

    public void drawX(Canvas canvas, int x, int y) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        canvas.drawBitmap(bitmap, null, new RectF(x, y, x + width / 3f, y + width / 3f), pen);
    }

    public void drawO(Canvas canvas, int x, int y) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o);
        canvas.drawBitmap(bitmap, null, new RectF(x, y, x + width / 3f, y + width / 3f), pen);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game.isActive()) {
            float x = event.getX();
            float y = event.getY();

            int row = -1, column = -1;
            if (x >= 0 && x <= width / 3f) row = 0;
            else if (x >= width / 3f + 10 && x <= 2 * width / 3f + 10) row = 1;
            else if (x >= 2 * width / 3f + 20) row = 2;

            if (y >= 0 && y <= width / 3f) column = 0;
            else if (y >= width / 3f + 10 && y < 2 * width / 3f + 10) column = 1;
            else if (y >= 2 * width / 3f + 20 && y < width) column = 2;

            if (row == -1 || column == -1) return true;

            if (game.get(row, column) == 0) {
                if (settings.getBoolean("Sounds", true)) click.start();
                game.set(row, column);
                game.changePlayer();
                invalidate();
            }
        }

        return true;
    }
}
