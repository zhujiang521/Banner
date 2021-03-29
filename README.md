# Banner

## 打造 Compose 版本的 Banner

### 没有 ViewPager ？
之前写安卓的时候这都不是事，不管是自己使用 `ViewPager` 实现又或者是直接使用三方库，都不是什么难事；特别是使用三方库，添加一行依赖基本就没啥事了，但。。。现在使用 `Compose` 该怎么搞？

本来想着 `Compose` 中怎么着也应该有类似于 `ViewPager` 的东西，但找遍了愣是没找见，什么情况？没有？那怎么搞？自己定义？不是个事啊！

### 柳暗花明！

正当一筹莫展的时候，又想起来再看看 `Google` 官方给的 `Demo` 吧，没准能获得一些有用的信息呢！

果然！在官方 `Jetcaster` 这个 `Demo` 中竟然自定义有类似 `ViewPager` 的控件：`Pager` ，别的不说，先来贴下地址：

[https://github.com/android/compose-samples/tree/1630f6b35ac9e25fb3cd3a64208d7c9afaaaedc5/Jetcaster](https://github.com/android/compose-samples/tree/1630f6b35ac9e25fb3cd3a64208d7c9afaaaedc5/Jetcaster)

上面是 `Jetcaster` 项目的地址，下面再贴下 `Pager` 的地址吧，免得大家还得找：

[https://github.com/android/compose-samples/blob/1630f6b35ac9e25fb3cd3a64208d7c9afaaaedc5/Jetcaster/app/src/main/java/com/example/jetcaster/util/Pager.kt#L130](https://github.com/android/compose-samples/blob/1630f6b35ac9e25fb3cd3a64208d7c9afaaaedc5/Jetcaster/app/src/main/java/com/example/jetcaster/util/Pager.kt#L130)

上面既然已经贴了官方的代码了，本着有现成就不重复制造轮子的原则，就不自定义了（主要是懒）。直接把官方的拿过来借鉴下，然后再自己加工一下，只是个简单实现和封装，下面是成果样式，大家凑合看：

<img src="/screenshots/banner.gif" width="360">

还不错吧，哈哈哈！来看看使用这个 `Banner` 需要怎么做吧：

首先需要定义下 `Banner` 的 `Model` ：

```kotlin
data class BannerBean(
    override val data: Any? = null
) : BaseBannerBean()
```

`Model` 很简单，只有一个参数，继承自 `BaseBannerBean` ，可能有人就说了，`BaseBannerBean` 是个啥玩意？别着急，后面会说的，现在先这样用！

```kotlin
val items = arrayListOf(
            BannerBean(
                "https://wanandroid.com/blogimgs/8a0131ac-05b7-4b6c-a8d0-f438678834ba.png",
            ),
            BannerBean(
                "https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png",
            ),
            BannerBean(
                "https://www.wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png",
            ),
            BannerBean(
                "https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png",
            ),
        )

BannerPager(
    items = items
) { item ->
    Toast.makeText(context, "item:$item", Toast.LENGTH_SHORT).show()
    Log.e(TAG, "Notes: 哈哈哈:$item")
}
```

怎么样，是不是 **so easy** ？只需要传入定义好的 `items` ，没错，只需要传入你的数据集合就可以了，函数对象中的参数是回调回来的数据，直接做你想做的事就可以了！是不是心动了？？？上面图中的 `indicator` 颜色也太丑了，直男审美，大家别介意！哈哈哈，放心吧，如果想换颜色什么的都可以直接换！快来详细看看还有什么功能吧！

```kotlin
@Composable
fun <T : BaseBannerBean> BannerPager(
    items: List<T> = arrayListOf(),
    repeat: Boolean = true,
    config: BannerConfig = BannerConfig(),
    indicator: Indicator = CircleIndicator(),
    onBannerClick: (T) -> Unit
)
```

来一个一个参数看看啥意思吧：

- **items**：数据源，可以看到数据源使用到了泛型，这里为什么使用泛型下面再说，这里只要知道必须是继承自 `BaseBannerBean` 就可以了
- **repeat**：是否允许自动轮播
- **config**：Banner 的一些配置参数
- **indicator**：Banner 指示器，你可以使用默认的 `CircleIndicator` 或 `NumberIndicator` ，也可以自定义，仅需要继承 `Indicator` 即可。
- **onBannerClick**：`Banner` 的点击事件，会回调回一个泛型参数供大家使用

通过上面的这些参数可以修改很多你想要的样式！

### 图片动起来

这里我选择使用 `Timer` 来实现让图片动起来！来看看代码吧：

```kotlin
val timer = Timer()
val mTimerTask: TimerTask = object : TimerTask() {
    override fun run() {
        viewModelScope.launch {
            if (pagerState.currentPage == pagerState.maxPage) {
                pagerState.currentPage = 0
            } else {
                pagerState.currentPage++
            }
            pagerState.fling(-1f)
        }
    }
}

timer?.schedule(mTimerTask, 5000, 3000)
```

就是当当前页码和最大页码相同的时候手动置为 0 就可以了，嗯，就这！是不是太简单了，哈哈哈，别想太多，其实就这么简单。然后通过 `pagerState` 中的 `fling` 方法让图片动起来！ `pagerState` 是什么在下面会有介绍的。

### BaseBannerBean

本来想一块一块给大家说的，但想了想还是直接根据写好的一步步深入比较好理解。

咱们先来看看 `items` ，可以看到它继承自 `BaseBannerBean` ，那这个 `BaseBannerBean` 又是个什么东西呢？它不是个东西，它只是为了抽象，为了能统一对图片做处理，废话不多说，直接看代码：

```kotlin
/**
 * Banner Model 的基类
 */
abstract class BaseBannerBean {
    // 图片资源 可以是：url、文件路径或者是 drawable id
    abstract val data: Any?
}
```

对，没了，就这么点内容！只有一个 `data` ，有什么用注释也写了，就是图片资源，为了方便统一做处理所以才抽出来的！

最开始的时候这块我其实写了三个变量：一个 `url` ，一个文件路径，一个 `drawable id` ，然后再判断哪个不为空进行处理，但是后来想了想弄成一个就可以了啊，一个图片而已嘛！

下面来一步一步看看怎么使用吧：

```kotlin
Pager(
    state = pagerState,
    modifier = Modifier.fillMaxWidth().height(config.bannerHeight)
) {
    val item = items[page]
    BannerCard(
        bean = item,
        modifier = Modifier.fillMaxSize().padding(config.bannerImagePadding),
        shape = config.shape
    ) {
        Log.d(TAG, "item is :${item.javaClass}")
        onBannerClick(item)
    }
}
```

上面代码中的 `Pager` 就是上面提到的官方的 `Compose` 版本的 `ViewPager` ，使用很简单，但是需要传入一个 `pagerState` ，这个 `pagerState` 里面保存着最大页码、最小页码和当前页码，随时更新状态，创建方法很简单：

```kotlin
val pagerState: PagerState = remember { PagerState() }
```

然后把 `pagerState` 直接像上面那样穿进去就行。

#### BannerCard

跑题了跑题了，这一小块说的是图片。。。。继续看，上面代码中的 `BannerCard` 就是显示图片的控件，来看看怎么写的吧：

```kotlin
/**
 * Banner 图片展示卡片
 *
 * @param bean banner Model
 * @param modifier
 */
@Composable
fun <T : BaseBannerBean> BannerCard(
    bean: T,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    onBannerClick: () -> Unit,
) {
    if (bean.data == null) {
        throw NullPointerException("Url or imgRes or filePath must have a not for empty.")
    }

    Card(
        shape = shape,
        modifier = modifier
    ) {
        val imgModifier = Modifier.clickable(onClick = onBannerClick)
        when (bean.data) {
            is String -> {
                val img = bean.data as String
                if (img.contains("https://") || img.contains("http://")) {
                    Log.d(TAG, "PostCardPopular: 加载网络图片")
                    CoilImage(
                        data = img,
                        contentDescription = null,
                        modifier = imgModifier
                    )
                } else {
                    Log.d(TAG, "PostCardPopular: 加载本地图片")
                    val bitmap = BitmapFactory.decodeFile(img)
                    Image(
                        modifier = imgModifier,
                        painter = BitmapPainter(bitmap.asImageBitmap()),
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                }
            }
            is Int -> {
                Log.d(TAG, "PostCardPopular: 加载本地资源图片")
                Image(
                    modifier = imgModifier,
                    painter = painterResource(bean.data as Int),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                throw IllegalArgumentException("参数类型不符合要求，只能是：url、文件路径或者是 drawable id")
            }
        }
    }
}
```

虽然代码看着不少，但是逻辑是不是很清晰，传进来的 `BaseBannerBean` 中的 `data` 如果为空的话直接抛异常，因为图片资源都为空了还搞什么飞机啊！对不？下面就根据不同资源直接加载图片就行了。

这里需要特别说的有两点：

1. 当图片资源为图片路径的时候，需要通过 `BitmapFactory` 先转成 `Bitmap` ，然后还需要把 `Bitmap` 通过 `Bitmap` 的扩展方法 `asImageBitmap` 转成 `ImageBitmap` ，这样才能在 `Compose` 的 `Image` 中使用。
2. 网络图片的加载这里用到了一个三方加载库 `coil` ，具体是什么就不过多介绍了，大家可以去百度下，在 `Kotlin` 中还是推荐使用 `coil` ，当然 `Glide` 也支持了 `Compose` ，大家可以随便使用。

#### 本地图片

在最开始的时候已经试了网络图片，这里咱们刚说过还可以使用本地图片，来试试吧：

```kotlin
val items2 = arrayListOf(
    BannerBean(R.drawable.banner1),
    BannerBean(R.drawable.banner2),
    BannerBean(R.drawable.banner3),
    BannerBean(R.drawable.banner4),
)

BannerPager(
    modifier = Modifier.padding(top = 10.dp),
    items = items2,
    indicator = CircleIndicator(gravity = BannerGravity.BottomLeft)
) { item ->
    Toast.makeText(context, "item:$item", Toast.LENGTH_SHORT).show()
}
```

嗯，感觉比网络图片还简单啊，哈哈哈！运行下看看吧：

<img src="/screenshots/banner2.gif" width="360">

嘿嘿嘿！大家有没有发现什么不一样？指示器是不是跑到左边了！哈哈哈，因为上面 `CircleIndicator` 中加了 `BannerGravity.BottomLeft` ，所以就在左边，这个下面会详细给大家介绍。而且加载本地图片完全没有问题，而且由于是本地的，比加载网络图片快得多！

### Banner配置类

上面 `BannerPager` 中参数的一个类型，咱们来看看这个类可以修改什么东西吧：

```kotlin
data class BannerConfig(
    // banner 高度
    var bannerHeight: Dp = 210.dp,
    // banner 图片距离四周的 padding 值
    var bannerImagePadding: Dp = 8.dp,
    // banner 图片的 shape
    var shape: Shape = RoundedCornerShape(10.dp),
    // banner 切换间隔时间
    var intervalTime: Long = 3000
)
```

注释中基本都写了，`banner` 高度 、距离四周的 `padding` 值、图片的 `shape` 以及切换间隔时间都可以在这里进行设置，当然不设置也可以，不设置的话就是默认值，也够用了！

这块咱们也可以修改下里面的值看看效果！

```kotlin
BannerPager(
    items = items2,
    indicator = CircleIndicator(gravity = BannerGravity.BottomLeft),
    config = BannerConfig(
        bannerHeight = 250.dp,
        bannerImagePadding = 0.dp,
        shape = RoundedCornerShape(0),
        intervalTime = 1000
    )
) { item ->
    Toast.makeText(context, "item:$item", Toast.LENGTH_SHORT).show()
}
```

可以看到上面咱们修改了 `BannerConfig` 的默认值，将高度改为了 250dp，图片的 `Padding` 值改为了 0dp，圆角也设置为了 0，切换时间改为了 1 秒，来看看效果吧：

<img src="/screenshots/banner3.gif" width="360">

是不是基本符合咱们心中的样子，而且加载速度变快了许多，圆角也没了，成了长方形！

### Banner 指示器

这一块需要好好说说，其实 `Banner` 并没有多少东西，也就一个图片区域，剩下的就是指示器了，一般都是指示器各种样式花里胡哨的！

我在这里一共默认了两种指示器，一种是常见的圆形的指示器，另一种是数字的指示器。出来的效果大家应该在上面都看见了。

来看看指示器是怎么写的，如果你想自定义的话该怎么做！

```kotlin
/**
 * 指示器基类，如果需要自定义指示器，需要继承此类，并实现 [DrawIndicator] 方法
 * 别忘了在重写方法上添加 @Composable 注解
 */
abstract class Indicator {

    abstract var gravity: Int

    @Composable
    abstract fun DrawIndicator(pagerState: PagerState)

}
```

上面代码看注释就能明白，这是一个抽象类，是所有 `Banner` 指示器的基类，里面有一个 `gravity` ，我定义了三个：

```kotlin
/**
 * BannerGravity 设置指示器位置
 */
object BannerGravity {

    // 底部居中
    const val BottomCenter = 0
    // 底部左边
    const val BottomLeft = 2
    // 底部右边
    const val BottomRight = 3

}
```

为什么只定义三个呢？因为我觉得也就这几种情况了。。。如果还有别的的话，之后我再加。

#### 圆形指示器

接下来先来看看圆形指示器的写法吧：

```kotlin
/**
 * 圆形指示器 eg：。。. 。。
 *
 * @param indicatorColor 指示器默认颜色
 * @param selectIndicatorColor 指示器选中颜色
 * @param indicatorDistance 指示器之间的距离
 * @param indicatorSize 指示器默认圆大小
 * @param selectIndicatorSize 指示器选中圆大小
 * @param gravity 指示器位置
 */
class CircleIndicator(
    var indicatorColor: Color = Color(30, 30, 33, 90),
    var selectIndicatorColor: Color = Color.Green,
    var indicatorDistance: Int = 50,
    var indicatorSize: Float = 10f,
    var selectIndicatorSize: Float = 13f,
    override var gravity: Int = BottomCenter,
) : Indicator() {

    @Composable
    override fun DrawIndicator(pagerState: PagerState) {
        for (pageIndex in 0..pagerState.maxPage) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val color: Color
                val inSize: Float
                if (pageIndex == pagerState.currentPage) {
                    color = selectIndicatorColor
                    inSize = selectIndicatorSize
                } else {
                    color = indicatorColor
                    inSize = indicatorSize
                }
                val start = when (gravity) {
                    BottomCenter -> {
                        val width = canvasWidth - pagerState.maxPage * indicatorDistance
                        width / 2
                    }
                    BottomLeft -> {
                        100f
                    }
                    BottomRight -> {
                        canvasWidth - pagerState.maxPage * indicatorDistance - 100f
                    }
                    else -> 100f
                }
                drawCircle(
                    color,
                    inSize,
                    center = Offset(start + pageIndex * indicatorDistance, canvasHeight)
                )
            }
        }
    }
}
```

上面代码虽然有点多，但是逻辑其实很简单，循环画圆，通过 `PagrState` 来判断是否是当前页面，然后画不同的颜色。

可以看到，圆形指示器中也可以修改一些配置，如果不想用默认的完全可以通过参数进行修改。

代码很简单，直接通过 `Canvas` 画了几个圆，上面也说了，颜色大家可以自己进行定义！

还是说一下怎么画的吧。。。最外面包裹着一个 `for` 循环，通过 `PageState` 可以获取到页面的数量及当前页码，然后添加一个 `Canvas` ，获取下 `Canvas` 的宽和高，然后获取当前的颜色，再根据设置的 `gravity` 获取下放置的坐标点，最后直接画上去就可以了！

圆形指示器上面图中的都是，也展示了在左边的样式，这里展示下在右边的样子吧：

```kotlin
BannerPager(
    modifier = Modifier.padding(top = 10.dp),
    items = items2,
    indicator = CircleIndicator(gravity = BannerGravity.BottomRight)
) { item ->
    Toast.makeText(context, "item:$item", Toast.LENGTH_SHORT).show()
}
```

<img src="/screenshots/banner4.gif" width="360">

怎么样，不丑吧！哈哈哈。

#### 数字指示器

数字指示器和圆形指示器一样，都继承自 `Indicator`：

```kotlin
/**
 * 数字指示器，显示数字的指示器  eg: 1/5
 *
 * @param backgroundColor 数字指示器背景颜色
 * @param numberColor 数字的颜色
 * @param circleSize 背景圆的半径
 * @param fontSize 页码文字大小
 * @param gravity 指示器位置
 */
class NumberIndicator(
    var backgroundColor: Color = Color(30, 30, 33, 90),
    var numberColor: Color = Color.White,
    var circleSize: Dp = 35.dp,
    var fontSize: TextUnit = 15.sp,
    override var gravity: Int = BottomRight,
) : Indicator() {

    @Composable
    override fun DrawIndicator(pagerState: PagerState) {
        val alignment: Alignment = when (gravity) {
            BannerGravity.BottomCenter -> {
                Alignment.BottomCenter
            }
            BannerGravity.BottomLeft -> {
                Alignment.BottomStart
            }
            BottomRight -> {
                Alignment.BottomEnd
            }
            else -> Alignment.BottomEnd
        }
        Box(modifier = Modifier.fillMaxSize().padding(10.dp), contentAlignment = alignment) {
            Box(
                modifier = Modifier.size(circleSize).clip(CircleShape)
                    .background(color = backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${pagerState.currentPage + 1}/${pagerState.maxPage + 1}",
                    color = numberColor,
                    fontSize = fontSize
                )
            }
        }
    }

}
```

这里原本也想使用 `Canvas` 画一下的，但想了想有的朋友可能不喜欢画，所以就使用 `Box` 进行布局了，看大家需要了，哪种写法都是可以的，大家也可以在这里也通过 `Canvas` 画一下。同样的，数字指示器中也可以修改一些配置，如果不想用默认的完全可以通过参数进行修改。

从上面代码不难看出，数字指示器也可以设置左中右三个地方，就不一一设置了，给大家看一下最常用的右边的样式吧：

<img src="/screenshots/banner5.gif" width="360">

### 小结尾

到这里为止一个 `Banner` 已经完成了，功能都差不多完成了，但是。。。

凡事就怕但是！但并不是特别好看，不过也无所谓了，已经将接口给大家放开了，直接继承自己画就行了，而且 `Banner` 中的属性都可以进行设置，其实上面有些还没有展示，大家可以每个参数都修改试试，会有不一样的小惊喜的！

其实还有很多没有写到的内容，比如图片切换的动画。。。一个优秀的库不是一两天就能写完的，希望大家能多给我提点建议。

我会尽快把这个库上传到 `JitPack` 中，到时候大家就可以加一行依赖直接使用了。

最后放一下 `Github` 库的地址吧：[https://github.com/zhujiang521/Banner](https://github.com/zhujiang521/Banner)

对大家如果有帮助的话别忘了点 `Star` 啊！感激不尽！！！