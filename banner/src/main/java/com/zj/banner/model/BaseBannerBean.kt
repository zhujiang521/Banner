package com.zj.banner.model

/**
 * Banner Model 的基类
 */
abstract class BaseBannerBean {
    // 图片 url
    abstract val url: String
    // 图片文件路径
    abstract val filePath: String
    // 图片 drawable id
    abstract val imgRes: Int
}
