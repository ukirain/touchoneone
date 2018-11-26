package edu.spbstu.threerow;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.graphics.Paint.Align;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


//****************************************************************
//class RefreshHandler
//****************************************************************
class RefreshHandler extends Handler 
{
	ViewGame	m_viewGame;
	
	public RefreshHandler(ViewGame v)
	{
		m_viewGame = v;
	}

	public void handleMessage(Message msg) 
	{
		m_viewGame.update();
		m_viewGame.invalidate();
	}
	
	public void sleep(long delayMillis) 
	{
		this.removeMessages(0);
	    sendMessageDelayed(obtainMessage(0), delayMillis);
	}
};

class Recta{
	Rect r;
	boolean selected;
	Paint color;
	int ord;

	Recta(int left, int top, int right, int bottom, int order){
		r = new Rect(left, top, right, bottom);
		selected = false;
		color = new Paint();
		color.setColor(Color.BLUE);
		ord = order;
	}

	void select(){
		selected = true;
		color.setColor(Color.GREEN);
	}

	void unselect(){
		selected = false;
		color.setColor(Color.BLUE);
	}
}



//****************************************************************
//class Icon
//****************************************************************
class Icon 
{
	public	static final int STATE_SIT			= 0;
	public	static final int STATE_MOVE			= 1;
	public	static final int STATE_DROP 		= 2;
	public	static final int STATE_DISAPPEAR	= 3;
	public	static final int STATE_REMOVED		= 4;
	
	public 	int		m_indexBitmap;		// in m_bitmapIcon[]
	public 	int		m_state;			// one of STATE_SIT, ...
	public 	int		m_timeStart;		// animation start time		
	public 	int		m_timeEnd;			// animation end time
	public 	int 	m_cellSrc;			// from cell
	public 	int		m_cellDst;			// to cell
};


public class ViewGame extends View
{
	// ************************************************************************
	// CONST
	// ************************************************************************
	// CONST

	private static final int TIME_GAME_STATE_APPEAR 	= 600;
	private static final int CELL_SWAP_TIME				= 200;
	private static final int TIME_DISAPPEAR				= 400;
	private static final int TIME_DROP					= 400;

	private static final int HINT_WAIT_TIME				= 5000;
	private static final int TIME_SHOW_COMBO_MSG		= 1000;

	private static final int UPDATE_TIME_MS 			= 30;

	private static final int BUBBLE_LIFE_TIME			= 8000;
	private static final int NUM_BUBBLES				= 32;

	private static final int GAME_STATE_FIELD_APPEAR	= 0;
	private static final int GAME_STATE_PLAY			= 1;

	private static final int ICON_STAR 			= 0;
	private static final int ICON_DOLPHIN		= 1;
	private static final int ICON_WHALE			= 2;
	private static final int ICON_CRAB 			= 3;
	private static final int ICON_GOLDFISH		= 4;
	private static final int ICON_ZEBFISH		= 5;
	private static final int ICON_OCTOPUS		= 6;
	private static final int ICON_HORSE			= 7;

	private static final int ICON_COUNT			= 8;

	private static final int NUM_CELLS			= 6;




	// ************************************************************************
	// DATA
	// ************************************************************************

	private boolean			m_active = false;
	private RefreshHandler	m_refresh;
	ActivityMain			m_app;


	int						m_timeCur;
	int						m_timeStateStart;
	int						m_timeSunRotate;

	int						m_gameState;

	int						m_touchState;
	int						m_touchCellIndex;


	boolean					m_needCheck3;
	int						m_gameScore;
	int						m_numGameMoves;


	int						m_numPossibleSwaps = 0;
	int						m_possibleSwapIndex0, m_possibleSwapIndex1;

	int						m_timeComboStart 	= 0;
	int						m_timeComboEnd		= 0;
	int						m_comboMult			= 0;
	boolean gc = true;

    Paint					m_paintTextCombo;
    Paint					m_paintTextUnder;
    Paint					m_paintTextOrder;
    int						m_scrW, m_scrH;

	private Paint mPaint;

	private Paint p;
	boolean m_gameOver = false;
	private Recta recta[];

	int numRect = 10;
	boolean m_win;
	boolean play;

    public int width;
    public  int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint   mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    String					m_strWin;
    String					m_strGameOver;
    String					m_strContinue;
    private int nextOrder;


    // ************************************************************************
	// METHODS
	// ************************************************************************
	
	public ViewGame(ActivityMain app)
	{
		super(app);
		m_app = app;
		m_refresh = new RefreshHandler(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

        m_paintTextCombo = new Paint();
        m_paintTextCombo.setColor(0xFFFFEEFF);
        m_paintTextCombo.setStyle(Style.FILL);
        m_paintTextCombo.setAntiAlias(true);
        m_paintTextCombo.setTextAlign(Align.CENTER);

        m_paintTextUnder = new Paint();
        m_paintTextUnder.setColor(0xFFFFEEFF);
        m_paintTextUnder.setStyle(Style.FILL);
        m_paintTextUnder.setAntiAlias(true);
        m_paintTextUnder.setTextAlign(Align.CENTER);

        m_paintTextOrder = new Paint();
        m_paintTextOrder.setColor(0xFFFFEEFF);
        m_paintTextOrder.setStyle(Style.FILL);
        m_paintTextOrder.setAntiAlias(true);
        m_paintTextOrder.setTextAlign(Align.CENTER);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        m_strWin		= app.getString(R.string.str_win);
        m_strGameOver		= app.getString(R.string.str_gameOver);
        m_strContinue		= app.getString(R.string.str_continue);
        play = false;


        recta = new Recta[numRect];
        p = new Paint();

        for(int i = 0; i < numRect; i += 1) {
            recta[i] = new Recta( i * 50 , i * 50, i * 50 + 50, i * 50 + 50, i);
        }


        nextOrder = 0;

		setOnTouchListener(app);
		gameRestart();
	}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        p.setColor(Color.RED);
        p.setStrokeWidth(10);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (m_scrW < 0)
            prepareScreenValues(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        for(int i = 0; i < numRect; i++) {
            canvas.drawRect(recta[i].r.left, recta[i].r.top, recta[i].r.right, recta[i].r.bottom, recta[i].color);
            canvas.drawText(Integer.toString(recta[i].ord), recta[i].r.left + (recta[i].r.right - recta[i].r.left) / 2 , recta[i].r.top - (recta[i].r.top - recta[i].r.bottom) / 2, m_paintTextOrder);
        }
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);

        if(!gc){
            drawComboMessage(canvas);
            gameRestart();
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        play = true;
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        if(gc == false){
            try {
                Thread.sleep(250);
                m_gameOver = false;
                m_win = false;
                gc = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        float R = 2;
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            float L = (float)Math.sqrt((x - mX) * (x - mX) + (y - mY) * (y - mY));
            float bias = 1 - R / L;
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX  + 100 * ((x - mX) / dx), mY + 100 * ((y - mY) / dy), R, Path.Direction.CW);
        }
    }

    private void collider(float x, float y) {

        Region rPath = new Region(); //path of our finger
        Region clipPath = new Region(0, 0, this.getWidth(), this.getHeight());
        rPath.setPath(mPath, clipPath);

        Region circlePath = new Region(); //path of our finger
        circlePath.setPath(mPath, clipPath);

        //rects
        for(int i = 0; i < numRect; i++) {
            if (recta[i].r.contains((int) x, (int) y)) {
                //pick the square
                if(!recta[i].selected) {
                    if (i == nextOrder) {
                        recta[i].select();
                        nextOrder++;
                        break;
                    } else {
                        m_gameOver = true;
                        break;
                    }
                }

            }
        }

        //самопересечение
        if(!m_gameOver) {
            if (rPath.contains((int) x, (int) y)) {
                //crash
                m_gameOver = true;
            } else {
                //not crash
                m_gameOver = false;
            }
        }
        gc = gameContinuum();
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        mPath.reset();
        nextOrder = 0;
        play = false;
        for(int i = 0; i < numRect; i++) {
            recta[i].unselect();
        }
    }

	private void gameRestart()
	{
		m_timeCur = (int)(System.currentTimeMillis() & 0xffffffff);
		
		m_gameState = GAME_STATE_FIELD_APPEAR;
		m_timeCur = m_timeStateStart = -1;
		m_touchState = 0;
		m_touchCellIndex = -1;
		m_needCheck3 = false;
		m_gameScore = 0;
		m_numGameMoves = 0;
		m_numPossibleSwaps = 0;
		m_possibleSwapIndex0 = m_possibleSwapIndex1 = -1;

		m_timeComboStart 	= 0;
		m_timeComboEnd		= 0;
		m_comboMult			= 0;
		m_timeSunRotate     = -100000;
        touch_up();

	}
	
	public boolean performClick()
	{
		boolean b = super.performClick();
		return b;
	}
	public void start()
	{
		m_active 	= true;
		m_refresh.sleep(UPDATE_TIME_MS);

		//Log.d("THREE", "ViewGame::start");
	}
	public void onPause()
	{
		//Log.d("THREE", "ViewGame::onPause");

		m_active 	= false;

	}
	public void onDestroy()
	{
		Log.d("THREE", "ViewGame::onDestroy");

	}


	public void update()
	{
		if (!m_active)
			  return;
		// send next update to game
		if (m_active)
			m_refresh.sleep(UPDATE_TIME_MS);

	}

	public boolean onTouch(int x, int y, int evtType) {
        switch (evtType) {
            case AppIntro.TOUCH_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case AppIntro.TOUCH_MOVE:
                if(play) {
                    touch_move(x, y);
                    collider(x, y);
                    invalidate();
                }
                break;
            case AppIntro.TOUCH_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

	public boolean gameContinuum(){


		if(m_gameOver){
			mPaint.setColor(Color.RED);
		} else {
			mPaint.setColor(Color.YELLOW);
		}

		m_win = true;
		for(int i = 0; i < numRect; i++){
			if(recta[i].selected == false){
				m_win = false;
				break;
			}
		}
		if(m_win == true){
			return false;
		}
		if(m_gameOver == true){
            return false;
		}
		return true;
	}



    public void drawComboMessage(Canvas canvas)
    {

        int cx = m_scrW >> 1;
        int cy = m_scrH >> 1;

        String str;
        if(m_win){
            str = m_strWin;
        } else if(m_gameOver){
            str = m_strGameOver;
        } else {
            str = "";
        }
        String strContinue = m_strContinue;
        Rect rText = new Rect();
        float size = 16.0f + 32.0f;
        m_paintTextCombo.setTextSize(size);
        m_paintTextCombo.getTextBounds(str, 0, str.length(), rText);

        m_paintTextUnder.setTextSize(size - 32.0f);
        m_paintTextUnder.getTextBounds(str, 0, str.length(), rText);
        int h = rText.height();
        int w = rText.width();

        // Draw text
        m_paintTextCombo.setAlpha(255);
        canvas.drawText(str,  cx + canvas.getWidth() / 2,  cy + canvas.getHeight() / 2, m_paintTextCombo);
        canvas.drawText(strContinue,  cx + canvas.getWidth() / 2,  cy + canvas.getHeight() / 2 + 40, m_paintTextUnder);

    }

    private void prepareScreenValues(Canvas canvas)
    {
        // Get canvas (screen) size
        m_scrW = canvas.getWidth();
        m_scrH = canvas.getHeight();

    }
}
