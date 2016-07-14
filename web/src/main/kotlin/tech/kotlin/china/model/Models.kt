package tech.kotlin.china.model

import org.springframework.context.annotation.Description

@Description("发布文章所用表单")
data class ArticleForm(val title: String, val content: String, val category: String)

@Description("评论表单")
data class CommentForm(val content: String, val aid: Long, val reply: Long?)

@Description("发送消息表单")
data class MessageForm(val title: String, val content: String, val from: Long?, val to: Long)

@Description("github access token wrapper")
data class GithubTokenWrapper(var access_token: String = "")

@Description("github账号信息")
data class GithubAccount(var id: Long = 0L, var login: String = "", var avatar_url: String = "",
                         var html_url: String = "", var email: String? = "")