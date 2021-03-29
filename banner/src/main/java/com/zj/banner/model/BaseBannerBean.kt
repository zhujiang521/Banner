package com.zj.banner.model

/**
 * Banner Model 的基类
 */
abstract class BaseBannerBean {
    // 图片资源 可以是：url、文件路径或者是 drawable id
    abstract val data: Any?
}
