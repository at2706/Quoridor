package edu.nyu.at2706.quoridor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class GameView extends View {
    private Context mContext;
    private Paint mPaint;
    private float[] gridPoints;
    int numColumns = 9;
    int numRows = 9;
    private float width, height, cellLen;

    Point player = new Point(5, 1);
    Point opponent = new Point(5, 9);
    Point ghost = null;
    PointF tap = new PointF();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(15);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int numLines = numColumns + numRows - 2;
        width = getWidth();
        height = getHeight();
        cellLen = Math.min(width / (float)numColumns, height / (float)numRows);
        gridPoints = new float[numLines * 2 * 4];

        int i = -1;
        for(int dx = 1; dx < numColumns; dx++){
            gridPoints[++i] = cellLen * dx;
            gridPoints[++i] = 0;
            gridPoints[++i] = cellLen * dx;
            gridPoints[++i] = height;
        }

        for(int dy = 1; dy < numRows; dy++){
            gridPoints[++i] = 0;
            gridPoints[++i] = cellLen * dy;
            gridPoints[++i] = width;
            gridPoints[++i] = cellLen * dy;
        }

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int length = Math.min(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(length, length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLines(gridPoints, mPaint);
        PointF playerCanvas = gridToCanvas(player);
        PointF opponentCanvas = gridToCanvas(opponent);
        float radius = cellLen / 2 - 7.5f;

        mPaint.setColor(Color.RED);
        canvas.drawCircle(playerCanvas.x, playerCanvas.y, radius, mPaint);
        if(ghost != null){
            PointF ghostCanvas = gridToCanvas(ghost);
            Log.e("d", Float.toString(ghostCanvas.x) + ", " + Float.toString(ghostCanvas.y));
            mPaint.setAlpha(128);
            canvas.drawCircle(ghostCanvas.x, ghostCanvas.y, radius, mPaint);
            mPaint.setAlpha(255);
        }
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(opponentCanvas.x, opponentCanvas.y, radius, mPaint);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("Action Down", Float.toString(x) + ", " + Float.toString(y));
                tap = new PointF(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.e("Action Move", Float.toString(tap.x) + ", " + Float.toString(tap.y));


                break;
            case MotionEvent.ACTION_UP:

                float dx = x - tap.x;
                float dy = y - tap.y;
                if(Math.abs(dx) > cellLen  || Math.abs(dy) > cellLen) {
                    if(Math.abs(dx) > Math.abs(dy)) {
                        if(dx > 0) {
                            Log.e("d", "Right;");
                            setGhost(player.x + 1, player.y);
                        }
                        else {
                            Log.e("d", "Left;");
                            setGhost(player.x - 1, player.y);
                        }
                    }
                    else {
                        if(dy > 0) {
                            Log.e("d", "Down;");
                            setGhost(player.x, player.y - 1);
                        }
                        else {
                            Log.e("d", "Up;");
                            setGhost(player.x, player.y + 1);
                        }
                    }
                    tap = new PointF();
                }
                else {
                    Log.e("d", "Tap;");
                    dismissGhost();
                }
                break;
        }
        invalidate();
        return true;
    }

    public PointF gridToCanvas(Point p) {
        return new PointF((p.x - 0.5f) * cellLen, height - ((p.y - 0.5f) * cellLen));
    }

    public void setPlayer(int x, int y) {
        if(x > 0 && x <= numColumns)
            player.x = x;
        if(y > 0 && y <= numRows)
            player.y = y;
    }

    public void setGhost(int x, int y) {
        if(x > 0 && x <= numColumns && y > 0 && y <= numRows) {
            ghost = new Point(x, y);
            setAction(mContext.getString(R.string.move));
        }
        else {
            dismissGhost();
        }
    }

    public void dismissGhost(){
        ghost = null;
        setAction(mContext.getString(R.string.none));
    }

    public void setAction(String action){
        (getParent()).setText(action);
    }

}
