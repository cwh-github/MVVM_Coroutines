package com.cwh.mvvm_coroutines.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.cwh.mvvm_coroutines.db.Convert
import java.io.Serializable

/**
 * Description:
 * Date：2020/3/2-13:36
 * Author: cwh
 */
data class LatestNews(
    val date: Long,
    val stories: List<Story>?,
    val top_stories: List<TopStory>?
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
//Room 中属性必选为var,且必须有构造函数
@Entity
@TypeConverters(Convert::class)
data class Story(
    var ga_prefix: String,
    /**
     * 作者信息
     */
    var hint: String,
    /**
     * 文章详情对应的id
     */
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    /**
     * 渐变色对应的启始颜色  0xb3a87d
     */
    var image_hue: String,
    /**
     * 对应的主题图片
     */
    var images: ArrayList<String>?,
    var title: String,
    var type: Int,
    /**
     * 文章详情对应的url
     */
    var url: String,
    /**
     * 是否收藏
     */
    var isLike:Boolean,
    /**
     * 是否已读
     */
    var isRead:Boolean,
    /**
     * news对应的时间
     */
    var date:Long,

    /**
     * 是否是时间type
     */
    var isTime:Boolean=false,
    //显示顺序
    var orderNum:Int

):Serializable{
    constructor():this("","",0,"",null,"",
        0,"",false,false,0L,false,0)
}

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
@Entity
data class TopStory(
    var ga_prefix: String,
    /**
        *作者信息
     */
    var hint: String?,
    /**
    `对应的文章id
     */
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var image: String,
    /**
       * 图片下面渐变色
     */
    var image_hue: String?,
    var title: String,
    var type: Int,
    /**
     * 文章详情对应的url
     */
    var url: String
){
    constructor():this("","",0,"",null,"",0,"")
}