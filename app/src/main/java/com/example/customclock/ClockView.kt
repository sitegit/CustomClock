package com.example.customclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class ClockView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var borderColor by Delegates.notNull<Int>()
    private var backgroundClockColor by Delegates.notNull<Int>()
    private var hourHandColor by Delegates.notNull<Int>()
    private var minuteHandColor by Delegates.notNull<Int>()
    private var secondHandColor by Delegates.notNull<Int>()
    private var numberColor by Delegates.notNull<Int>()

    private var clockRingWidth by Delegates.notNull<Float>()
    private var defaultWidth by Delegates.notNull<Float>()
    private var defaultLength by Delegates.notNull<Float>()
    private var specialWidth by Delegates.notNull<Float>()
    private var specialLength by Delegates.notNull<Float>()
    private var hourHandWidth by Delegates.notNull<Float>()
    private var minuteHandWidth by Delegates.notNull<Float>()
    private var secondHandWidth by Delegates.notNull<Float>()

    private var clockWidth by Delegates.notNull<Int>()
    private var clockHeight by Delegates.notNull<Int>()
    private var centerX by Delegates.notNull<Float>()
    private var centerY by Delegates.notNull<Float>()
    private var clockRadius by Delegates.notNull<Float>()

    private lateinit var circlePaint: Paint
    private lateinit var pointerPaint: Paint
    private lateinit var numPaint: Paint

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, R.style.DefaultClockViewStyle)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.ClockViewStyle)
    constructor(context: Context) : this(context, null)

    init {
        if (attrs != null) {
            initAttributes(attrs, defStyleAttr, defStyleRes)
        } else {
            initDefaultColors()
        }
        initPaint()
    }

    private fun initPaint() {
        circlePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
        pointerPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            strokeCap = Paint.Cap.ROUND
        }
        numPaint = Paint().apply {
            style = Paint.Style.FILL
            textSize = 60f
            color = numberColor
        }
    }

    private fun initAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockView, defStyleAttr, defStyleRes)
        backgroundClockColor = typedArray.getColor(R.styleable.ClockView_backgroundClockColor, BACKGROUND_COLOR)
        borderColor = typedArray.getColor(R.styleable.ClockView_borderColor, BORDER_COLOR)
        hourHandColor = typedArray.getColor(R.styleable.ClockView_hourHandColor, HOUR_HAND_COLOR)
        minuteHandColor = typedArray.getColor(R.styleable.ClockView_minuteHandColor, MINUTE_HAND_COLOR)
        secondHandColor = typedArray.getColor(R.styleable.ClockView_secondHandColor, SECOND_HAND_COLOR)
        numberColor = typedArray.getColor(R.styleable.ClockView_secondHandColor, NUMBER_COLOR)
        clockRingWidth = typedArray.getDimension(R.styleable.ClockView_clockRingWidth, CLOCK_RING_WIDTH)
        defaultWidth = typedArray.getDimension(R.styleable.ClockView_defaultWidth, DEFAULT_WIDTH)
        defaultLength = typedArray.getDimension(R.styleable.ClockView_defaultLength, DEFAULT_LENGTH)
        specialWidth = typedArray.getDimension(R.styleable.ClockView_specialWidth, SPECIAL_WIDTH)
        specialLength = typedArray.getDimension(R.styleable.ClockView_specialLength, SPECIAL_LENGTH)
        hourHandWidth = typedArray.getDimension(R.styleable.ClockView_hourHandWidth, HOUR_HAND_WIDTH)
        minuteHandWidth = typedArray.getDimension(R.styleable.ClockView_minuteHandWidth, MINUTE_HAND_WIDTH)
        secondHandWidth = typedArray.getDimension(R.styleable.ClockView_secondHandWidth, SECOND_HAND_WIDTH)
        typedArray.recycle()
    }

    private fun initDefaultColors() {
        backgroundClockColor = BACKGROUND_COLOR
        borderColor = BORDER_COLOR
        hourHandColor = HOUR_HAND_COLOR
        minuteHandColor = MINUTE_HAND_COLOR
        secondHandColor = SECOND_HAND_COLOR
        numberColor = NUMBER_COLOR
        clockRingWidth = CLOCK_RING_WIDTH
        defaultWidth = DEFAULT_WIDTH
        defaultLength = DEFAULT_LENGTH
        specialWidth = SPECIAL_WIDTH
        specialLength = SPECIAL_LENGTH
        hourHandWidth = HOUR_HAND_WIDTH
        minuteHandWidth = MINUTE_HAND_WIDTH
        secondHandWidth = SECOND_HAND_WIDTH
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getMeasureSize(true, widthMeasureSpec)
        val height = getMeasureSize(false, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clockWidth = w
        clockHeight = h
        centerX = w / 2f
        centerY = h / 2f
        clockRadius = w / 2f * 0.8f
    }

    private fun getMeasureSize(isWidth: Boolean, measureSpec: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED ->
                if (isWidth) suggestedMinimumWidth else suggestedMinimumHeight
            MeasureSpec.AT_MOST ->
                if (isWidth) minOf(specSize, clockWidth) else minOf(specSize, clockHeight)
            MeasureSpec.EXACTLY -> specSize
            else -> specSize
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(centerX, centerY)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        circlePaint.strokeWidth = clockRingWidth
        circlePaint.color = borderColor

        canvas.drawCircle(0f, 0f, clockRadius, circlePaint)
        for (i in 0 until 60) {
            if (i % 5 == 0) {
                circlePaint.strokeWidth = specialWidth
                circlePaint.color = hourHandColor
                canvas.drawLine(0f, -clockRadius + clockRingWidth / 2, 0f, -clockRadius + specialLength, circlePaint)
            } else {
                circlePaint.strokeWidth = defaultWidth
                circlePaint.color = secondHandColor
                canvas.drawLine(0f, -clockRadius + clockRingWidth / 2, 0f, -clockRadius + defaultLength, circlePaint)
            }
            canvas.rotate(6f)
        }
    }

    companion object {
        const val BACKGROUND_COLOR = Color.WHITE
        const val BORDER_COLOR = Color.BLACK
        const val HOUR_HAND_COLOR = Color.BLACK
        const val MINUTE_HAND_COLOR = Color.BLACK
        const val SECOND_HAND_COLOR = Color.RED
        const val NUMBER_COLOR = Color.BLACK

        const val CLOCK_RING_WIDTH = 5f
        const val DEFAULT_WIDTH = 1f
        const val DEFAULT_LENGTH = 1f
        const val SPECIAL_WIDTH = 1f
        const val SPECIAL_LENGTH = 1f
        const val HOUR_HAND_WIDTH = 5f
        const val MINUTE_HAND_WIDTH = 3f
        const val SECOND_HAND_WIDTH = 2f
    }
}