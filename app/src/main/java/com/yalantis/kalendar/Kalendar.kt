package com.yalantis.kalendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView

const val DRAG_HEIGHT = 45

class Kalendar(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet), ViewProvider {

    private var totalWidth: Int = 0

    private var totalHeight: Int = 0

    private var dayContainerHeight = 0

    private var weeksMarginTop = 0

    private var previousSelectedDay: View? = null

    private var isCreated = false

    private val dragView by lazy { createDragView() }

    private val moveManager by lazy { MoveManager(this) }


//    private fun createTouchContainer(touchListener: OnTouchListener) {
//        addView(LinearLayout(context).apply {
//            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
//            setOnTouchListener(touchListener)
//        })
//    }

    private fun createWeek(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL

            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(0, weeksMarginTop, 0, 0)
            }
            for (j in 0 until 7) {
                this.addView(createDay(j.toString()))
            }
        }
    }

    private fun createDay(label: String = "8"): TextView {
        return TextView(context).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                performDayClick(it)
            }
            text = label
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
            setTextColor(resources.getColor(android.R.color.background_dark))
            layoutParams = LinearLayout.LayoutParams(totalWidth / 7, dayContainerHeight)
        }
    }

    private fun performDayClick(day: View) {
        if (moveManager.isBusy.not()) {
            changeColors(day)
            selectWeek(day)
        }
    }

    private fun selectWeek(selectedDay: View?) {
        selectedDay?.let {
            val parent = it.parent as View
            val selectedWeek = indexOfChild(parent)
            moveManager.setSelectedWeek(selectedWeek)
        }
    }

    private fun changeColors(newSelectedDay: View?) {
        newSelectedDay?.setBackgroundResource(R.drawable.day_background)
        previousSelectedDay?.background = null
        previousSelectedDay = newSelectedDay
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (isCreated.not()) {
            calculateBounds(left, top, right, bottom)
            setBackgroundResource(android.R.color.holo_purple)
            createContent()
            isCreated = true
        }
    }

    override fun onTouchEvent(event: MotionEvent) = moveManager.onTouch(event)

    private fun createContent() {
        orientation = VERTICAL
        addView(createMonthSwitch())
        addView(createWeekDays())
        for (i in 0 until 5) addView(createWeek())
        addView(dragView)
    }

    private fun createDragView(): View {
        return LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, DRAG_HEIGHT).apply {
                setGravity(Gravity.BOTTOM)
            }
            setBackgroundColor(Color.GRAY)
        }
    }

    private fun createMonthSwitch(): LinearLayout {
        return LinearLayout(context).apply {
            isClickable = true
            isFocusable = true
            addView(createMonthDay("March"))
            addView(createMonthDay("April"))
            addView(createMonthDay("May"))
        }
    }


    private fun createMonthDay(label: String): View? {
        return TextView(context).apply {
            text = label
            textSize = 25f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                weight = 1f
            }
        }
    }

    private fun calculateBounds(left: Int, top: Int, right: Int, bottom: Int) {
        totalWidth = right - left
        totalHeight = bottom - top
        dayContainerHeight = totalHeight / 10
        weeksMarginTop = totalHeight / 40
    }

    private fun createWeekDays(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            isClickable = true
            isFocusable = true
            addView(createWeekDay("Mon"))
            addView(createWeekDay("Tue"))
            addView(createWeekDay("Wed"))
            addView(createWeekDay("Thu"))
            addView(createWeekDay("Fri"))
            addView(createWeekDay("Sat"))
            addView(createWeekDay("Sun"))
        }
    }

    private fun createWeekDay(label: String) =
        TextView(context).apply {
            text = label
            gravity = Gravity.CENTER
            textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
            setTextColor(resources.getColor(android.R.color.background_dark))
            layoutParams = LinearLayout.LayoutParams(totalWidth / 7, dayContainerHeight)
        }

    override fun setViewBottom(newBottom: Int) {
        bottom = newBottom
    }

    override fun getBottomLimit() = bottom

    override fun getTopLimit() = getChildAt(2).bottom

    override fun setDragTop(newDragTop: Float) {
        dragView.y = newDragTop
    }

    override fun getDragTop() = dragView.y

    override fun getWeekHeight(position: Int) = getChildAt(position).height

    override fun getWeekBottom(position: Int) = getChildAt(position).y + getChildAt(position).height

    override fun getWeekTop(position: Int) = getChildAt(position).y

    override fun setWeekTop(position: Int, newTop: Float) {
        getChildAt(position).y = newTop
    }

}