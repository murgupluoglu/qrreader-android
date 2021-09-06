package com.murgupluoglu.qrreader

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class QROverlayView : View {

    var framingRect: Rect? = null

    private val mDefaultLaserColor = Color.parseColor("#ffcc0000")
    private val mDefaultMaskColor = Color.parseColor("#60000000")
    private val mDefaultBorderColor = Color.parseColor("#ffafed44")
    private val mDefaultBorderStrokeWidth = 4
    private val mDefaultBorderLineLength = 60
    private lateinit var mLaserPaint: Paint
    private lateinit var mFinderMaskPaint: Paint
    private lateinit var mBorderPaint: Paint
    private var mBorderLineLength = 0
    private var mSquareViewFinder = false
    private var mViewFinderOffset = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
    }

    private fun init() {
        //set up laser paint
        mLaserPaint = Paint().apply {
            color = mDefaultLaserColor
            style = Paint.Style.FILL
        }

        //finder mask paint
        mFinderMaskPaint = Paint().apply {
            color = mDefaultMaskColor
        }

        //border paint
        mBorderPaint = Paint().apply {
            color = mDefaultBorderColor
            style = Paint.Style.STROKE
            strokeWidth = mDefaultBorderStrokeWidth.toFloat()
            isAntiAlias = true
        }
        mBorderLineLength = mDefaultBorderLineLength
    }

    public override fun onDraw(canvas: Canvas) {
        if (framingRect == null) {
            return
        }
        drawViewFinderMask(canvas)
        drawViewFinderBorder(canvas)
    }

    private fun drawViewFinderMask(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        val framingRect = framingRect
        canvas.drawRect(0f, 0f, width.toFloat(), framingRect!!.top.toFloat(), mFinderMaskPaint)
        canvas.drawRect(
            0f,
            framingRect.top.toFloat(),
            framingRect.left.toFloat(),
            (framingRect.bottom + 1).toFloat(),
            mFinderMaskPaint
        )
        canvas.drawRect(
            (framingRect.right + 1).toFloat(),
            framingRect.top.toFloat(),
            width.toFloat(),
            (framingRect.bottom + 1).toFloat(),
            mFinderMaskPaint
        )
        canvas.drawRect(
            0f, (framingRect.bottom + 1).toFloat(), width.toFloat(), height.toFloat(),
            mFinderMaskPaint
        )
    }

    private fun drawViewFinderBorder(canvas: Canvas) {
        val framingRect = framingRect

        // Top-left corner
        val path = Path()
        path.moveTo(framingRect!!.left.toFloat(), (framingRect.top + mBorderLineLength).toFloat())
        path.lineTo(framingRect.left.toFloat(), framingRect.top.toFloat())
        path.lineTo((framingRect.left + mBorderLineLength).toFloat(), framingRect.top.toFloat())
        canvas.drawPath(path, mBorderPaint)

        // Top-right corner
        path.moveTo(framingRect.right.toFloat(), (framingRect.top + mBorderLineLength).toFloat())
        path.lineTo(framingRect.right.toFloat(), framingRect.top.toFloat())
        path.lineTo((framingRect.right - mBorderLineLength).toFloat(), framingRect.top.toFloat())
        canvas.drawPath(path, mBorderPaint)

        // Bottom-right corner
        path.moveTo(framingRect.right.toFloat(), (framingRect.bottom - mBorderLineLength).toFloat())
        path.lineTo(framingRect.right.toFloat(), framingRect.bottom.toFloat())
        path.lineTo((framingRect.right - mBorderLineLength).toFloat(), framingRect.bottom.toFloat())
        canvas.drawPath(path, mBorderPaint)

        // Bottom-left corner
        path.moveTo(framingRect.left.toFloat(), (framingRect.bottom - mBorderLineLength).toFloat())
        path.lineTo(framingRect.left.toFloat(), framingRect.bottom.toFloat())
        path.lineTo((framingRect.left + mBorderLineLength).toFloat(), framingRect.bottom.toFloat())
        canvas.drawPath(path, mBorderPaint)
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        updateFramingRect()
    }

    @Synchronized
    fun updateFramingRect() {
        val viewResolution = Point(width, height)
        var width: Int
        var height: Int
        val orientation: Int = Configuration.ORIENTATION_PORTRAIT
        if (mSquareViewFinder) {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (getHeight() * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                width = height
            } else {
                width = (getWidth() * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                height = width
            }
        } else {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (getHeight() * LANDSCAPE_HEIGHT_RATIO).toInt()
                width = (LANDSCAPE_WIDTH_HEIGHT_RATIO * height).toInt()
            } else {
                width = (getWidth() * PORTRAIT_WIDTH_RATIO).toInt()
                height = (PORTRAIT_WIDTH_HEIGHT_RATIO * width).toInt()
            }
        }
        if (width > getWidth()) {
            width = getWidth() - MIN_DIMENSION_DIFF
        }
        if (height > getHeight()) {
            height = getHeight() - MIN_DIMENSION_DIFF
        }
        val leftOffset = (viewResolution.x - width) / 2
        val topOffset = (viewResolution.y - height) / 2
        framingRect = Rect(
            leftOffset + mViewFinderOffset,
            topOffset + mViewFinderOffset,
            leftOffset + width - mViewFinderOffset,
            topOffset + height - mViewFinderOffset
        )
    }

    companion object {
        private const val PORTRAIT_WIDTH_RATIO = 6f / 8
        private const val PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f
        private const val LANDSCAPE_HEIGHT_RATIO = 5f / 8
        private const val LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f
        private const val MIN_DIMENSION_DIFF = 50
        private const val DEFAULT_SQUARE_DIMENSION_RATIO = 5f / 8
    }
}