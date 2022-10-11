package com.example.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
    }
}

class DrawView extends View {
    static int width;
    static int height;
    int state;
    static TicTacToe game = new TicTacToe();
    Paint pen = new Paint();

    public DrawView(Context context) {
        super(context);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        pen.setColor(Color.GREEN);
        pen.setStrokeWidth(10);

        canvas.drawLine(0, width / 3, width, width / 3, pen);
        canvas.drawLine(0, 2 * width / 3, width, 2 * width / 3, pen);
        canvas.drawLine(width / 3, 50, width / 3, width, pen);
        canvas.drawLine(2 * width / 3, 50, 2 * width / 3, width, pen);

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
                break;
            case 1:
                canvas.drawLine(width / 6, 0, width / 6, width, pen);
                break;
            case 2:
                canvas.drawLine(width / 2, 0, width / 2, width, pen);
                break;
            case 3:
                canvas.drawLine(5 * width / 6, 0, 5 * width / 6, width, pen);
                break;
            case 4:
                canvas.drawLine(0, width / 6, width, width / 6, pen);
                break;
            case 5:
                canvas.drawLine(0, width / 2, width, width / 2, pen);
                break;
            case 6:
                canvas.drawLine(0, 5 * width / 6, width, 5 * width / 6, pen);
                break;
            case 7:
                canvas.drawLine(0, 0, width, width, pen);
                break;
            case 8:
                canvas.drawLine(0, width, width, 0, pen);
                break;
        }

        if (state > -1 && state < 9) {
            if (game.getPlayer() == 2) status = "Первый игрок победил!";
            else status = "Второй игрок победил!";
        }

        pen.setColor(Color.BLACK);
        pen.setTextSize(50);
        canvas.drawText(status, 10, width + 50, pen);
    }

    public void drawX(Canvas canvas, int x, int y) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        canvas.drawBitmap(bitmap, null, new RectF(x, y, x + width / 3, y + width / 3), pen);
    }

    public void drawO(Canvas canvas, int x, int y) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o);
        canvas.drawBitmap(bitmap, null, new RectF(x, y, x + width / 3, y + width / 3), pen);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (game.isActive()) {
            float x = event.getX();
            float y = event.getY();

            int row = -1, column = -1;
            if (x >= 0 && x <= width / 3) row = 0;
            else if (x >= width / 3 + 10 && x <= 2 * width / 3 + 10) row = 1;
            else if (x >= 2 * width / 3 + 20) row = 2;

            if (y >= 0 && y <= width / 3) column = 0;
            else if (y >= width / 3 + 10 && y < 2 * width / 3 + 10) column = 1;
            else if (y >= 2 * width / 3 + 20) column = 2;

            if (game.get(row, column) == 0) {
                game.set(row, column);
                game.changePlayer();
                invalidate();
            }
        }

        return true;
    }
}
