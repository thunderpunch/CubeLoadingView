package com.thunderpunch.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by thunderpunch on 2017/3/3
 * Description:
 */

public class CubeLoadingView extends View {
    private static final int DEGREE = 60;//立方体与Y轴夹角
    private static final float CEIL = 0.8f;//顶部小矩形边长相对于立方体边长的比例
    private static final float SHADOW_DISTANCE = 3.7f;//阴影顶部距离立方体顶部的距离相对于立方体边长的倍数
    private int MAIN_COLOR = 0xffd77900;//主体色
    private int CEIL_COLOR = 0xffffe33c;//顶部小矩形颜色
    private int SHADOW_COLOR = 0xffdbdbdb;//阴影色
    private int T = 3200;//单个方块行动一个周期所需时长
    private Paint mPaint;
    private int mCubeLength;//立方体边长
    private Point mOrigin;//开始绘制前的偏移量，用来使绘制内容居中
    private Path mPath, mCubePathCollection, mShadowPathCollection, mCeilPathCollection;
    private ArrayList<Path> mCubePaths, mShadowPaths, mCeilPaths;
    private ArrayList<Cube> mCubes;
    private long mAnimationStartTime;
    private int mProcessTime;
    private boolean isRunAnimation = true;
    private boolean forceStop;
    private boolean mShadowEnable;//是否开启阴影,默认开启
    private final static int SDK_INT = Build.VERSION.SDK_INT;

    public CubeLoadingView(Context context) {
        this(context, null);
    }

    public CubeLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CubeLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CubeLoadingView);
        mShadowEnable = a.getBoolean(R.styleable.CubeLoadingView_shadowEnable, true);
        MAIN_COLOR = a.getColor(R.styleable.CubeLoadingView_mainColor, MAIN_COLOR);
        CEIL_COLOR = a.getColor(R.styleable.CubeLoadingView_ceilColor, CEIL_COLOR);
        SHADOW_COLOR = a.getColor(R.styleable.CubeLoadingView_shadowColor, SHADOW_COLOR);
        T = a.getInteger(R.styleable.CubeLoadingView_duration, T);
        a.recycle();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mOrigin = new Point();
        mCubes = new ArrayList<>();

        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mCubePathCollection = new Path();
            mShadowPathCollection = new Path();
            mCeilPathCollection = new Path();
        } else {
            mCubePaths = new ArrayList<>();
            mShadowPaths = new ArrayList<>();
            mCeilPaths = new ArrayList<>();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int horizontalSpace = w - getPaddingLeft() - getPaddingRight();
        final int verticalSpace = h - getPaddingTop() - getPaddingBottom();
        int tempSize[] = calculateSizeByHorizontalSpace(getMinimumHorizontalSpace());
        if (horizontalSpace < tempSize[0] || verticalSpace < tempSize[1]) {
            //给到空间小于所需最小宽高则按最小宽高计算
        } else {
            tempSize = calculateSizeByVerticalSpace(verticalSpace);
            if (tempSize[0] > horizontalSpace) {
                tempSize = calculateSizeByHorizontalSpace(horizontalSpace);
            }
        }

        mCubeLength = tempSize[2];
        mOrigin.x = (w - tempSize[0]) / 2;
        mOrigin.y = (h - tempSize[1]) / 2;
        final int diagonalHorizontal = (int) (2 * mCubeLength * Math.sin(Math.toRadians(DEGREE)));
        final int diagonalVertical = (int) (2 * mCubeLength * Math.cos(Math.toRadians(DEGREE)));

        mPath = new Path();
        mPath.moveTo(diagonalHorizontal, 0);
        mPath.lineTo(2 * diagonalHorizontal, diagonalVertical);
        mPath.lineTo(diagonalHorizontal * 3 / 2, diagonalVertical * 3 / 2);
        mPath.lineTo(diagonalHorizontal / 2, diagonalVertical / 2);
        mPath.close();
        PathMeasure pathMeasure = new PathMeasure(mPath, false);
        mCubes.clear();
        for (int i = 0; i < 4; i++) {
            mCubes.add(new Cube((1.0f / 4) * i, pathMeasure, diagonalHorizontal, diagonalVertical, mCubeLength));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isRunAnimation) {
            super.onDraw(canvas);
            return;
        }

        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mShadowPathCollection.reset();
            mCubePathCollection.reset();
            mCeilPathCollection.reset();
        } else {
            mShadowPaths.clear();
            mCeilPaths.clear();
            mCeilPaths.clear();
        }

        long passedTime = System.currentTimeMillis() - mAnimationStartTime;
        float fraction = passedTime % T * 1.0f / T;
        for (Cube cube : mCubes) {
            cube.update(fraction);
            if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mCubePathCollection.op(cube.cubePath, Path.Op.UNION);
                mCeilPathCollection.op(cube.ceilPath, Path.Op.UNION);
                if (mShadowEnable) {
                    mShadowPathCollection.op(cube.shadowPath, Path.Op.UNION);
                }
            } else {
                mCubePaths.add(cube.cubePath);
                mCeilPaths.add(cube.ceilPath);
                if (mShadowEnable) {
                    mShadowPaths.add(cube.shadowPath);
                }
            }
        }
        canvas.save();
        canvas.translate(mOrigin.x, mOrigin.y);
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mShadowEnable) {
                mPaint.setColor(SHADOW_COLOR);
                mShadowPathCollection.offset(0, mCubeLength * SHADOW_DISTANCE);
                canvas.drawPath(mShadowPathCollection, mPaint);
            }
            mPaint.setColor(MAIN_COLOR);
            canvas.drawPath(mCubePathCollection, mPaint);
            mPaint.setColor(CEIL_COLOR);
            canvas.drawPath(mCeilPathCollection, mPaint);
        } else {
            if (mShadowEnable) {
                mPaint.setColor(SHADOW_COLOR);
                for (Path path : mShadowPaths) {
                    path.offset(0, mCubeLength * SHADOW_DISTANCE);
                    canvas.drawPath(path, mPaint);
                }
            }
            mPaint.setColor(MAIN_COLOR);
            for (Path path : mCubePaths) {
                canvas.drawPath(path, mPaint);
            }
            mPaint.setColor(CEIL_COLOR);
            for (Path path : mCeilPaths) {
                canvas.drawPath(path, mPaint);
            }
        }
        canvas.restore();
        if (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postInvalidateOnAnimation();
        } else {
            postInvalidateDelayed(200);
        }
    }


    /**
     * 根据给到高度计算所需宽度
     */
    private int[] calculateSizeByVerticalSpace(int verticalSpace) {
        int size[] = new int[3];
        size[1] = verticalSpace;
        float ratio = (float) (1.0f / 2 / Math.cos(Math.toRadians(DEGREE)));
        int diagonalVertical = (int) (size[1] / (2.5 + (mShadowEnable ? ratio * SHADOW_DISTANCE : ratio)));
        int unitHeight = (int) (diagonalVertical * ratio);
        size[0] = (int) (unitHeight * Math.sin(Math.toRadians(DEGREE)) * 2 * 2.5);
        size[2] = unitHeight;
        return size;
    }

    /**
     * 根据给到宽度计算所需高度
     */
    private int[] calculateSizeByHorizontalSpace(int horizontalSpace) {
        int size[] = new int[3];
        size[0] = horizontalSpace;
        int floorHeight = (int) (size[0] * 1.0f / Math.tan(Math.toRadians(DEGREE)));
        int unitHeight = (int) (floorHeight * 1.0f / 5 / Math.cos(Math.toRadians(DEGREE)));
        size[1] = (int) (floorHeight + (mShadowEnable ? unitHeight * SHADOW_DISTANCE : unitHeight));
        size[2] = unitHeight;
        return size;
    }

    public int getMinimumHorizontalSpace() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60
                , getContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && !forceStop) {
            start();
        }
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        if (screenState == View.SCREEN_STATE_OFF && !forceStop) {
            stop();
            forceStop = false;
        }
    }

    public void start() {
        mAnimationStartTime = System.currentTimeMillis() - mProcessTime;
        isRunAnimation = true;
        forceStop = false;
        invalidate();
    }

    public void stop() {
        if (mAnimationStartTime != 0)
            mProcessTime = (int) (System.currentTimeMillis() - mAnimationStartTime) % T;
        isRunAnimation = false;
        forceStop = true;
    }

    private class Cube {
        private Path cubePath, ceilPath, shadowPath;
        private PathMeasure pathMeasure;
        private float offset, stageLength;
        private float[] anchorPoint;
        private int diagonalHorzontal, diagonalVertical, sideLength;

        Cube(float offset, PathMeasure pathMeasure, int diagonalHorizontal, int diagonalVertical, int sideLength) {
            this.offset = offset;
            this.pathMeasure = pathMeasure;
            this.diagonalHorzontal = diagonalHorizontal;
            this.diagonalVertical = diagonalVertical;
            this.sideLength = sideLength;
            this.anchorPoint = new float[2];
            this.stageLength = pathMeasure.getLength() / 6;
            this.cubePath = new Path();
            this.ceilPath = new Path();
            this.shadowPath = new Path();
        }

        private void update(float fraction) {
            calculateAnchorPoint(fraction);
            float x = anchorPoint[0];
            float y = anchorPoint[1];
            final int dx = diagonalHorzontal >> 1;
            final int dy = diagonalVertical >> 1;
            updateCubePath(x, y, dx, dy);
            updateCeilPath(x, y, dx, dy);
            if (mShadowEnable) {
                updateShadowPath(x, y, dx, dy);
            }
        }

        private void updateCubePath(float x, float y, int dx, int dy) {
            cubePath.reset();
            cubePath.moveTo(x, y);
            x += dx;
            y += dy;
            cubePath.lineTo(x, y);
            y += sideLength;
            cubePath.lineTo(x, y);
            x -= dx;
            y += dy;
            cubePath.lineTo(x, y);
            x -= dx;
            y -= dy;
            cubePath.lineTo(x, y);
            y -= sideLength;
            cubePath.lineTo(x, y);
            cubePath.close();
        }

        private void updateCeilPath(float x, float y, int dx, int dy) {
            ceilPath.reset();
            y += diagonalVertical * (1 - CEIL);
            dx *= CEIL;
            dy *= CEIL;
            ceilPath.moveTo(x, y);
            x += dx;
            y += dy;
            ceilPath.lineTo(x, y);
            x -= dx;
            y += dy;
            ceilPath.lineTo(x, y);
            x -= dx;
            y -= dy;
            ceilPath.lineTo(x, y);
            ceilPath.close();
        }

        private void updateShadowPath(float x, float y, int dx, int dy) {
            shadowPath.reset();

            dx += 1;//填补间隔
            dy += 1;//填补间隔

            shadowPath.moveTo(x, y);
            x += dx;
            y += dy;
            shadowPath.lineTo(x, y);
            x -= dx;
            y += dy;
            shadowPath.lineTo(x, y);
            x -= dx;
            y -= dy;
            shadowPath.lineTo(x, y);
            shadowPath.close();
        }

        /**
         * @param fraction 在动画周期的位置[0.1)
         *                 Description: 将单个方块的行动周期分成八个时间段，以方块最上点的移动轨迹{@link #pathMeasure}为参照，得出每个时间段内方块的位置
         */
        private void calculateAnchorPoint(float fraction) {
            float fixedFraction = fraction + offset;
            fixedFraction = fixedFraction > 1 ? fixedFraction - 1 : fixedFraction;
            float stage = fixedFraction * 8;
            if (stage < 1) {

            } else if (stage < 2) {
                stage = 1;
            } else if (stage < 5) {
                stage -= 1;
            } else if (stage < 6) {
                stage = 4;
            } else {
                stage -= 2;
            }
            pathMeasure.getPosTan(stageLength * stage, anchorPoint, null);
        }
    }
}
