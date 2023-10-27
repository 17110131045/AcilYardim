package com.nexis.acilyardim.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearVerticalDecoration(val vSize: Int) : RecyclerView.ItemDecoration() {
    private var aPos: Int = 0

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        aPos = parent.getChildAdapterPosition(view)

        if (aPos == 0)
            outRect.set(0, vSize, 0, vSize)
        else
            outRect.set(0, 0, 0, vSize)
    }
}