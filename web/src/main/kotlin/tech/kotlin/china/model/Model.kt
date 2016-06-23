package tech.kotlin.china.model

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
 * comment: 评论数量
 * forbidden: 封禁状态
 */
data class Article(var aid: Long = 0, var author: Long = 0, var title: String = "", var description: String = "",
                   var content: String = "", var category: Int = 0, var create_time: Date = Date(), var view: Long = 0,
                   var flower: Long = 0, var comment: Long = 0, var forbidden: Boolean = false)

/***
 * 评论
 * cid: 评论id
 * aid: 文章id
 * commenter: 评论人id
 * reply: 回复用户ID
 * create_time: 创建时间
 * content: 评论内容
 * flower: 点赞数量
 * delete: 删除状态
 * forbidden: 封禁状态
 */
data class Comment(var cid: Long = 0, var aid: Long = 0, var commenter: Long = 0, var reply: Long? = null,
                   var create_time: Date = Date(), var content: String = "", var flower: Long = 0,
                   var delete: Boolean = false, var forbidden: Boolean = false)


/***
 * 点赞
 * id: 点赞id
 * mode: 点赞类型 0-文章 1-评论
 * oid: 被点赞客体 id
 * actor: 点赞人
 * praised: 被点赞人
 * create_time: 点赞时间
 */
data class Flower(var id: Long = 0, var mode: Int = 0, var oid: Long, var actor: Long, val author: Long,
                  var create_time: Date = Date())


