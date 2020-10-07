package my_view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import my_interface.GameOverInterface;
import my_interface.scoreInterface;

public class PinballGameView extends View {
    private int widthP,heightP;
    public static final int RADIUS = 50;// 圆的半径
    private static final int BOARD_THICKNESS = 18;
    private static int BOARD_SPEED = 25;
    private int board_offset;
    private Paint ballPaint,boardPaint;
    private float speedX,speedXMAX;//x轴上的速度
    private int timeY;//y轴动画时间（时间越短表明速度越快）
    private Rect ball_range;
    private Rect boardRect;
    private float ball_x,ball_y;
    private boolean isEnd;
    private int score;
    private long currentPlayTime;
    private scoreInterface scoreInterface;
    private GameOverInterface gameOverInterface;
    private boolean toLift,toRight;
    ValueAnimator downAnimator;
    ValueAnimator upAnimator;

    public PinballGameView(Context context) {
        super(context);
        init();
    }

    public PinballGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinballGameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        ballPaint = new Paint();
        ballPaint.setAntiAlias(true);
        ballPaint.setStyle(Paint.Style.FILL);
        ballPaint.setColor(Color.parseColor("#008577"));

        boardPaint =new Paint();
        boardPaint.setAntiAlias(true);
        boardPaint.setStyle(Paint.Style.FILL);
        boardPaint.setColor(Color.parseColor("#F9A825"));

        boardRect = new Rect();
        ball_range=new Rect();


        board_offset=0;
        timeY=4100;//4000毫秒
        speedX=0;

        isEnd = false;
        score=1;
        currentPlayTime=0;
        toLift=toRight=false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        boardRect.left=w/3;
        boardRect.right=w/3*2;
        boardRect.top=h-BOARD_THICKNESS-5;
        boardRect.bottom=h-5;

        ball_range.left=RADIUS;
        ball_range.right=w-RADIUS;
        ball_range.top=RADIUS;
        ball_range.bottom=boardRect.bottom-RADIUS;

        ball_x=w/2;
        ball_y=RADIUS;

        widthP=w;
        heightP=h;

        speedXMAX = 25;

        downAnimator=ValueAnimator.ofFloat(ball_range.top,ball_range.bottom);
        upAnimator=ValueAnimator.ofFloat(ball_range.bottom,ball_range.top);
        ballDown();
        ballUp();
    }

    private void ballDown(){
        downAnimator.setDuration(timeY);
        downAnimator.setInterpolator(new LinearInterpolator());
        downAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ball_y=(float)animation.getAnimatedValue();
                if(ball_x<=ball_range.left||ball_x>=ball_range.right){
                    speedX=-speedX;
                }
                ball_x+=speedX;
                boardMove();
                invalidate();
            }
        });
        downAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(ball_x<boardRect.left+board_offset||ball_x>boardRect.right+board_offset){
                    isEnd=true;
                    //调用游戏结束动画
                    gameStop();
                    if(gameOverInterface!=null){
                        gameOverInterface.gameOver();
                    }
                }else {
                    updataSpeed();
                    upAnimator.start();
                    if(scoreInterface!=null){
                        scoreInterface.setScore();
                    }
                }

            }
        });
    }

    private void ballUp(){
        upAnimator.setDuration(timeY);
        upAnimator.setInterpolator(new LinearInterpolator());
        upAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ball_y=(float)animation.getAnimatedValue();
                if(ball_x<=ball_range.left||ball_x>=ball_range.right){
                    speedX=-speedX;
                }
                ball_x+=speedX;
                boardMove();
                invalidate();
            }
        });
        upAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                score++;//得分加一
                if(timeY<100){
                    //满分记录
                    return;
                }
                downAnimator.start();
            }
        });
    }

    private void boardMove(){
        if(toLift){
            board_offset-=BOARD_SPEED;
            if(board_offset<-boardRect.left){
                board_offset=-boardRect.left;
            }
        }else if(toRight){
            board_offset+=BOARD_SPEED;
            if(board_offset>boardRect.left){
                board_offset=boardRect.left;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(Color.WHITE);//绘制背景颜色
        canvas.drawRect(boardRect.left+board_offset,boardRect.top,boardRect.right+board_offset,boardRect.bottom,boardPaint);
        canvas.drawCircle(ball_x,ball_y,RADIUS,ballPaint);
    }

    public void gameStart(){
        downAnimator.start();
    }

    public void gameStop(){
        if(downAnimator.isRunning()){
            downAnimator.cancel();
        }
        if(upAnimator.isRunning()){
            upAnimator.cancel();
        }
        score=0;
        ball_x=widthP/2;
        ball_y=RADIUS;
        BOARD_SPEED=25;
        timeY=3800;
        speedX=0;
        isEnd = false;
        score=1;
        currentPlayTime=0;
        toLift=toRight=false;
    }

    public void setScoreInterface(scoreInterface scoreInterface){
        this.scoreInterface=scoreInterface;
    }

    public void setGameOverInterface(GameOverInterface gameOverInterface) {
        this.gameOverInterface = gameOverInterface;
    }

    public int getScore() {
        return score;
    }

    private void updataSpeed(){
        if(Math.abs(speedX)<3){
            speedX= (float) (Math.random()*speedXMAX);
        }else if(toLift){
            speedX-=BOARD_SPEED;
        }else if(toRight){
            speedX+=BOARD_SPEED;
        }else {
            speedX+=(float) (Math.random()*10);
        }
        timeY-=10;
        upAnimator.setDuration(timeY);
        downAnimator.setDuration(timeY);
        if(score%200==0){
            BOARD_SPEED++;
        }
        speedX%=25;
    }

    public void toLiftBegin(){
        toLift=true;
    }

    public void toLiftEnd(){
        toLift=false;
    }

    public void toRightBegin(){
        toRight=true;
    }

    public void toRightEnd(){
        toRight=false;
    }

}

