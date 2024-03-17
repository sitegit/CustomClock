package com.example.customclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.util.Calendar
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask
import kotlin.math.min
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
    private var timeLineColor by Delegates.notNull<Int>()

    private var clockRingWidth by Delegates.notNull<Float>()
    private var minuteLineWidth by Delegates.notNull<Float>()
    private var minuteLineLength by Delegates.notNull<Float>()
    private var hourLineWidth by Delegates.notNull<Float>()
    private var hourLineLength by Delegates.notNull<Float>()
    private var hourHandWidth by Delegates.notNull<Float>()
    private var minuteHandWidth by Delegates.notNull<Float>()
    private var secondHandWidth by Delegates.notNull<Float>()
    private var numberSize by Delegates.notNull<Float>()

    private var clockWidth by Delegates.notNull<Int>()
    private var clockHeight by Delegates.notNull<Int>()
    private var centerX by Delegates.notNull<Float>()
    private var centerY by Delegates.notNull<Float>()
    private var clockRadius by Delegates.notNull<Float>()

    private lateinit var circlePaint: Paint
    private lateinit var backgroundPaint: Paint
    private lateinit var pointerPaint: Paint
    private lateinit var numPaint: Paint

    private val textBound = Rect()

    private val calendar = Calendar.getInstance()
    private var timer: Timer? = null

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        R.style.DefaultClockViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.ClockViewStyle
    )

    constructor(context: Context) : this(context, null)

    init {
        if (attrs != null) {
            initAttributes(attrs, defStyleAttr, defStyleRes)
        } else {
            initDefaultAttributes()
        }
        initPaint()
    }

    private fun initPaint() {
        circlePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
        backgroundPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = backgroundClockColor
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
        numberColor = typedArray.getColor(R.styleable.ClockView_numberColor, NUMBER_COLOR)
        timeLineColor = typedArray.getColor(R.styleable.ClockView_timeLineColor, BORDER_COLOR)

        clockRingWidth = typedArray.getDimension(R.styleable.ClockView_clockRingWidth, getSizeInPixels(CLOCK_RING_WIDTH))
        minuteLineWidth = typedArray.getDimension(R.styleable.ClockView_minuteLineWidth, getSizeInPixels(MINUTE_LINE_WIDTH))
        minuteLineLength = typedArray.getDimension(R.styleable.ClockView_minuteLineLength, getSizeInPixels(MINUTE_LINE_LENGTH))
        hourLineWidth = typedArray.getDimension(R.styleable.ClockView_hourLineWidth, getSizeInPixels(HOUR_LINE_WIDTH))
        hourLineLength = typedArray.getDimension(R.styleable.ClockView_hourLineLength, getSizeInPixels(HOUR_LINE_LENGTH))
        hourHandWidth = typedArray.getDimension(R.styleable.ClockView_hourHandWidth, getSizeInPixels(HOUR_HAND_WIDTH))
        minuteHandWidth = typedArray.getDimension(R.styleable.ClockView_minuteHandWidth, getSizeInPixels(MINUTE_HAND_WIDTH))
        secondHandWidth = typedArray.getDimension(R.styleable.ClockView_secondHandWidth, getSizeInPixels(SECOND_HAND_WIDTH))
        numberSize = typedArray.getDimension(R.styleable.ClockView_numberSize, getSizeInPixels(NUMBER_SIZE))
        typedArray.recycle()
    }

    private fun initDefaultAttributes() {
        backgroundClockColor = BACKGROUND_COLOR
        borderColor = BORDER_COLOR
        hourHandColor = HOUR_HAND_COLOR
        minuteHandColor = MINUTE_HAND_COLOR
        secondHandColor = SECOND_HAND_COLOR
        numberColor = NUMBER_COLOR
        timeLineColor = BORDER_COLOR

        clockRingWidth = CLOCK_RING_WIDTH
        minuteLineWidth = MINUTE_LINE_WIDTH
        minuteLineLength = MINUTE_LINE_LENGTH
        hourLineWidth = HOUR_LINE_WIDTH
        hourLineLength = HOUR_LINE_LENGTH
        hourHandWidth = HOUR_HAND_WIDTH
        minuteHandWidth = MINUTE_HAND_WIDTH
        secondHandWidth = SECOND_HAND_WIDTH
        numberSize = NUMBER_SIZE
    }

    fun setTimeZone(region: String, city: String) {
        calendar.timeZone = TimeZone.getTimeZone("$region/$city")
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    postInvalidate()
                }
            }, 0, 1000)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startTimer()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val radius = (min(width, height) / 2f) - clockRingWidth
        val desireRadius = (2 * radius + clockRingWidth * 2).toInt()

        setMeasuredDimension(
            resolveSize(desireRadius, widthMeasureSpec),
            resolveSize(desireRadius, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clockWidth = w - paddingLeft - paddingRight
        clockHeight = h - paddingTop - paddingBottom
        centerX = paddingLeft + paddingRight + clockWidth / 2f
        centerY = paddingTop + paddingBottom + clockHeight / 2f
        clockRadius = (min(clockWidth, clockHeight) - clockRingWidth) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(centerX, centerY)
        drawCircle(canvas)
        drawNums(canvas)
        getCurrentTime(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(0f, 0f, clockRadius, backgroundPaint)

        circlePaint.strokeWidth = clockRingWidth
        circlePaint.color = borderColor

        canvas.drawCircle(0f, 0f, clockRadius, circlePaint)

        for (i in 0 until 60) {
            if (i % 5 == 0) {
                circlePaint.strokeWidth = hourLineWidth
                circlePaint.color = timeLineColor
                canvas.drawLine(
                    0f,
                    -clockRadius + clockRingWidth / 2,
                    0f,
                    -clockRadius + clockRingWidth / 2 + hourLineLength,
                    circlePaint
                )
            } else {
                circlePaint.strokeWidth = minuteLineWidth
                canvas.drawLine(
                    0f,
                    -clockRadius + clockRingWidth / 2,
                    0f,
                    -clockRadius + clockRingWidth / 2 + minuteLineLength,
                    circlePaint
                )
            }
            canvas.rotate(6f)
        }
    }

    private fun drawNums(canvas: Canvas) {
        for (i in 0 until 12) {
            canvas.save()
            canvas.translate(0f, -clockRadius + hourLineLength + minuteLineLength + clockRingWidth)
            val text = if (i == 0) "12" else i.toString()
            numPaint.textSize = numberSize
            numPaint.color = numberColor
            numPaint.getTextBounds(text, 0, text.length, textBound)

            if (i != 0) {
                canvas.rotate(-i * 30f)
            }

            canvas.drawText(text, -textBound.width() / 2f, textBound.height() / 2f, numPaint)
            canvas.restore()
            canvas.rotate(30f)
        }
    }

    private fun getCurrentTime(canvas: Canvas) {
        calendar.timeInMillis = System.currentTimeMillis()

        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)

        val hourDegrees = (hours + minutes / 60f) * 30f
        val minuteDegrees = minutes * 6f
        val secondDegrees = seconds * 6f

        drawPointer(canvas, hourDegrees, minuteDegrees, secondDegrees)
    }

    private fun drawPointer(
        canvas: Canvas,
        hourDegrees: Float,
        minuteDegrees: Float,
        secondDegrees: Float
    ) {
        canvas.save()
        pointerPaint.color = hourHandColor
        pointerPaint.strokeWidth = hourHandWidth
        canvas.rotate(hourDegrees, 0f, 0f)
        canvas.drawLine(0f, 20f, 0f, -clockRadius * 0.45f, pointerPaint)
        canvas.restore()

        canvas.save()
        pointerPaint.color = minuteHandColor
        pointerPaint.strokeWidth = minuteHandWidth
        canvas.rotate(minuteDegrees, 0f, 0f)
        canvas.drawLine(0f, 20f, 0f, -clockRadius * 0.6f, pointerPaint)
        canvas.restore()

        canvas.save()
        pointerPaint.color = secondHandColor
        pointerPaint.strokeWidth = secondHandWidth
        canvas.rotate(secondDegrees, 0f, 0f)
        canvas.drawLine(0f, 40f, 0f, -clockRadius * 0.75f, pointerPaint)
        canvas.restore()

        pointerPaint.color = secondHandColor
        canvas.drawCircle(0f, 0f, hourHandWidth / 2, pointerPaint)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopTimer()
    }

    private fun getSizeInPixels(value: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
    }

    companion object {
        const val BACKGROUND_COLOR = Color.WHITE
        const val BORDER_COLOR = Color.BLACK
        const val HOUR_HAND_COLOR = Color.BLACK
        const val MINUTE_HAND_COLOR = Color.BLACK
        const val SECOND_HAND_COLOR = Color.RED
        const val NUMBER_COLOR = Color.BLACK

        const val CLOCK_RING_WIDTH = 16f
        const val MINUTE_LINE_WIDTH = 3f
        const val HOUR_LINE_WIDTH = 5f
        const val MINUTE_LINE_LENGTH = 8f
        const val HOUR_LINE_LENGTH = 15f
        const val HOUR_HAND_WIDTH = 8f
        const val MINUTE_HAND_WIDTH = 5f
        const val SECOND_HAND_WIDTH = 2f
        const val NUMBER_SIZE = 24f
    }
}