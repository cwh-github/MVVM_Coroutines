package com.cwh.mvvm_coroutines.model

/**
 * Description:
 * Date：2020/3/2-13:36
 * Author: cwh
 */
data class LatestNews(
    val date: String,
    val stories: List<Story>,
    val top_stories: List<TopStory>
)

/**
 * "image_hue": "0xb3a87d",
"title": "三个人成立一个公司，怎么才能相互制约？",
"url": "https://daily.zhihu.com/story/9720954",
"hint": "萧溪 · 3 分钟阅读",
"ga_prefix": "022911",
"images": [
"https://pic2.zhimg.com/v2-36de0401ed53cbaee5249c72bc29f44d.jpg"
],
"type": 0,
"id": 9720954
 */
data class Story(
    val ga_prefix: String,
    /**
     * 作者信息
     */
    val hint: String,
    /**
     * 文章详情对应的id
     */
    val id: Int,
    /**
     * 渐变色对应的启始颜色  0xb3a87d
     */
    val image_hue: String,
    /**
     * 对应的主题图片
     */
    val images: List<String>,
    val title: String,
    val type: Int,
    /**
     * 文章详情对应的url
     */
    val url: String
)

/**
 *
"image_hue": "0xb3947d",
"hint": "作者 / 我是一只小萌刀",
"url": "https://daily.zhihu.com/story/9720746",
"image": "https://pic3.zhimg.com/v2-a2e68715865ee4717110c684ff34072e.jpg",
"title": "历史上有哪些英雄人物在晚年对自己一生的评价？",
"ga_prefix": "022411",
"type": 0,
"id": 9720746
 */
data class TopStory(
    val ga_prefix: String,
    /**
        *作者信息
     */
    val hint: String?,
    /**
    `对应的文章id
     */
    val id: Int,
    val image: String,
    /**
       * 图片下面渐变色
     */
    val image_hue: String?,
    val title: String,
    val type: Int,
    /**
     * 文章详情对应的url
     */
    val url: String
)