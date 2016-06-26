package tech.kotlin.china.model

data class AccountForm(val name: String, val password: String)

data class ArticleForm(val title: String, val description: String, val content: String)

data class CommentForm(val content: String, val aid: Long, val reply: Long?)

data class MessageForm(val title: String, val content: String, val from: Long?, val to: Long)