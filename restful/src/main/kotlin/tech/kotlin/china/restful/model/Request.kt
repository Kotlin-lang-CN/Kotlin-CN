package tech.kotlin.china.restful.model

data class AccountForm(val name: String, val password: String)

data class ArticleForm(val title: String, val description: String, val content: String)

data class CommentForm(val content: String, val aid: Long, val reply: Long?)