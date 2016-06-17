package tech.kotlin.china.restful.model

import java.util.*

/**
 * 用户
 * uid: 用户id
 * name: 用户名
 * password: 用户密码
 * rank: 用户等级 normal - 0, admin - 1
 * forbidden: 封禁状态
 */
data class Account(var uid: Long = 0, var name: String = "", var password: String = "",
                   var rank: Int = 0, var forbidden: Boolean = false)

/***
 * 文章
 * aid: 文章id
 * author: 作者用户id
 * title: 文章标题
 * description: 文章描述
 * content: 文章内容
 * category: 文章类别 0-默认
 * create_time: 文章创建时间
 * view: 阅读数量
 * flower: 点赞数量
 * forbidden: 封禁状态
 */
data class Article(var aid: Long = 0, var author: Long = 0, var title: String = "", var description: String = "",
                   var content: String = "", var category: Int = 0, var create_time: Date = Date(), var view: Long = 0,
                   val flower: Long = 0, var forbidden: Boolean = false)

/***
 * 评论
 * cid: 评论id
 * aid: 文章id
 * uid: 评论人id
 * forbidden: 封禁状态
 * targetUID: 回复用户ID
 * create_time: 创建时间
 * content: 评论内容
 */
data class Comment(var cid: Long = 0, var aid: Long = 0, var uid: Long = 0, var forbidden: Boolean = false,
                   var targetUID: Long? = null, var create_time: Long = 0, var content: String = "")