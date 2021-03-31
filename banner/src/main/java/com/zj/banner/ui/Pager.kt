package com.zj.banner.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import com.zj.banner.ui.pagetransformer.BasePageTransformer
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * This is a modified version of:
 * https://gist.github.com/adamp/07d468f4bcfe632670f305ce3734f511
 */

class PagerState(
    currentPage: Int = 0,
    minPage: Int = 0,
    maxPage: Int = 0
) {

    private var _minPage by mutableStateOf(minPage)
    var minPage: Int
        get() = _minPage
        set(value) {
            _minPage = value.coerceAtMost(_maxPage)
            _currentPage = _currentPage.coerceIn(_minPage, _maxPage)
        }

    private var _maxPage by mutableStateOf(maxPage, structuralEqualityPolicy())
    var maxPage: Int
        get() = _maxPage
        set(value) {
            _maxPage = value.coerceAtLeast(_minPage)
            _currentPage = _currentPage.coerceIn(_minPage, maxPage)
        }

    private var _currentPage by mutableStateOf(currentPage.coerceIn(minPage, maxPage))
    var currentPage: Int
        get() = _currentPage
        set(value) {
            _currentPage = value.coerceIn(minPage, maxPage)
        }

    enum class SelectionState { Selected, Undecided }

    var selectionState by mutableStateOf(SelectionState.Selected)

    /**
     * 切换下一个页面
     */
    suspend fun setNextPage() {
        if (currentPage == maxPage) {
            currentPage = 0
        } else {
            currentPage++
        }
        _currentPageOffset.animateTo(
            currentPageOffset.roundToInt().toFloat(),
            animationSpec = SpringSpec()
        )
        currentPage -= currentPageOffset.roundToInt()
        snapToOffset(0f)
        selectionState = SelectionState.Selected
    }

    private var _currentPageOffset = Animatable(0f).apply {
        updateBounds(-1f, 1f)
    }
    val currentPageOffset: Float
        get() = _currentPageOffset.value

    suspend fun snapToOffset(offset: Float) {
        _currentPageOffset.snapTo(offset.coerceIn(-1f, 1f))
    }

    suspend fun fling(velocity: Float) {
        var currentPager = true
        if (velocity < 0 && currentPage == maxPage) {
            currentPage = minPage
            currentPager = false
        }
        if (velocity > 0 && currentPage == minPage) {
            currentPage = maxPage
            currentPager = false
        }
        _currentPageOffset.animateTo(
            currentPageOffset.roundToInt().toFloat(),
            animationSpec = SpringSpec(stiffness = 2200f)
        )

        if (currentPager) {
            currentPage -= currentPageOffset.roundToInt()
        }
        snapToOffset(0f)
        selectionState = SelectionState.Selected
    }

    override fun toString(): String = "PagerState{minPage=$minPage, maxPage=$maxPage, " +
            "currentPage=$currentPage, currentPageOffset=$currentPageOffset}"

    /**
     * 设置图片切换动画
     */
    fun setTransformer(transformer: BasePageTransformer?) {

    }
}

@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@PageData
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("no PageData for measurable $this")

@Composable
fun Pager(
    state: PagerState,
    modifier: Modifier = Modifier,
    offscreenLimit: Int = 1,
    pageContent: @Composable PagerScope.() -> Unit
) {
    var pageSize by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    Layout(
        content = {

            val minPage = (state.currentPage - offscreenLimit).coerceAtLeast(state.minPage)
            val maxPage = (state.currentPage + offscreenLimit).coerceAtMost(state.maxPage)

            for (page in minPage..maxPage) {
                val pageData = PageData(page)
                val scope = PagerScope(state, page)
                key(pageData) {
                    Box(contentAlignment = Alignment.Center, modifier = pageData) {
                        scope.pageContent()
                    }
                }
            }
        },
        modifier = modifier.draggable(
            orientation = Orientation.Horizontal,
            onDragStarted = {
                state.selectionState = PagerState.SelectionState.Undecided
            },
            onDragStopped = { velocity ->
                coroutineScope.launch {
                    state.fling(velocity / pageSize)
                }
            },
            state = rememberDraggableState { dy ->
                coroutineScope.launch {
                    with(state) {
                        val pos = pageSize * currentPageOffset
                        val max = pageSize * offscreenLimit
                        val min = -pageSize * offscreenLimit
                        val newPos = (pos + dy).coerceIn(min.toFloat(), max.toFloat())
                        snapToOffset(newPos / pageSize)
                    }
                }
            },
        )
    ) { measurable, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentPage = state.currentPage
            val offset = state.currentPageOffset
            val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            measurable
                .map {
                    it.measure(childConstraints) to it.page
                }
                .forEach { (placeable, page) ->
                    val xCenterOffset = (constraints.maxWidth - placeable.width) / 2
                    val yCenterOffset = (constraints.maxHeight - placeable.height) / 2

                    if (currentPage == page) {
                        pageSize = placeable.width
                    }

                    val xItemOffset = ((page + offset - currentPage) * placeable.width).roundToInt()
                    placeable.place(
                        x = xCenterOffset + xItemOffset,
                        y = yCenterOffset
                    )
                }
        }
    }
}

/**
 * Scope for [Pager] content.
 */
class PagerScope(
    private val state: PagerState,
    val page: Int
) {
    /**
     * Returns the current selected page
     */
    val currentPage: Int
        get() = state.currentPage

    /**
     * Returns the current selected page offset
     */
    val currentPageOffset: Float
        get() = state.currentPageOffset

    /**
     * Returns the current selection state
     */
    val selectionState: PagerState.SelectionState
        get() = state.selectionState
}
