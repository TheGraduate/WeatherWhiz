package com.example.weatherwhiz.util

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherwhiz.R
import com.example.weatherwhiz.adapter.CityAdapter

class StickyHeaderDecoration(private val adapter: CityAdapter) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }

        val headerTextView = createHeaderView(parent, topChildPosition)
        fixLayoutSize(headerTextView, parent)
        val contactPoint = headerTextView.bottom
        drawHeader(c, headerTextView, topChild, contactPoint)
    }

    private fun createHeaderView(parent: RecyclerView, position: Int): View {
        val headerView = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        val textView = headerView.findViewById<TextView>(R.id.headerTextView)
        textView.visibility = View.VISIBLE
        textView.text = adapter.currentList[position].city.firstOrNull()?.toString() ?: ""
        return headerView
    }

    private fun drawHeader(c: Canvas, headerView: View, child: View, contactPoint: Int) {
        c.save()
        c.translate(0f, Math.max(0, child.top - headerView.height).toFloat())
        headerView.draw(c)
        c.restore()
    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
            parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
            parent.paddingTop + parent.paddingBottom, view.layoutParams.height)

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

}