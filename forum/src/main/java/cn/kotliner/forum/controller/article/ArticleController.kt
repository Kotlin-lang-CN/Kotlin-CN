package cn.kotliner.forum.controller.article

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.domain.*
import cn.kotliner.forum.exceptions.abort
import cn.kotliner.forum.exceptions.check
import cn.kotliner.forum.exceptions.tryExec
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.service.account.api.UserApi
import cn.kotliner.forum.service.article.api.ArticleApi
import cn.kotliner.forum.service.article.api.TextApi
import cn.kotliner.forum.utils.*
import cn.kotliner.forum.service.account.req.QueryUserReq
import cn.kotliner.forum.service.article.req.*
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.ok
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Validated
@RestController
@RequestMapping("/api/article")
class ArticleController {

    @Resource private lateinit var req: Request
    @Resource private lateinit var sessionApi: SessionApi
    @Resource private lateinit var articleApi: ArticleApi
    @Resource private lateinit var userApi: UserApi
    @Resource private lateinit var textApi: TextApi

    //发布文章
    @PostMapping("/post")
    fun createArticle(
            @RequestParam("title") title: String,
            @RequestParam("author") author: Long,
            @RequestParam("category") category: Int,
            @RequestParam("tags") tags: String,
            @NotBlank(message = "无法发送空白内容")
            @Length(min = 30, message = "文章内容过短")
            @RequestParam("content") content: String
    ): Resp {
        sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED, "无权限发布文章内容") {
                    it.id == author
                            || it.role == Account.Role.ADMIN
                }
                .check(Err.UNAUTHORIZED, "只有管理员才能发布【站务】话题") {
                    category != Category.STATION.ordinal + 1
                            || it.role == Account.Role.ADMIN
                }

        val id = articleApi.create(CreateArticleReq().apply {
            this.title = title
            this.author = author
            this.category = category
            this.tags = tags
            this.content = content
        }).articleId

        val article: Article = articleApi.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.SYSTEM)

        return ok { it["id"] = article.id }
    }

    //修改已发布的文章
    @PostMapping("/post/{id}/update")
    fun updateArticle(
            @PathVariable("id") id: Long,
            @NotBlank
            @RequestParam("title") title: String,
            @RequestParam("category") category: Int,
            @RequestParam("tags", defaultValue = "") tags: String,
            @NotBlank(message = "无法发送空白内容")
            @Length(min = 30, message = "文章内容过短")
            @RequestParam("content") content: String
    ): Resp {
        //只有管理员才能发布站务话题
        val me = sessionApi.checkToken(req.token).account
                .check(Err.UNAUTHORIZED, "只有管理员才能发布【站务】话题") {
                    category != Category.STATION.ordinal + 1
                            || it.role == Account.Role.ADMIN
                }

        //查询文章
        val article: Article = articleApi.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        //只有作者和管理员才能修改文章内容
        if (me.role != Account.Role.ADMIN && article.author != me.id)
            abort(Err.UNAUTHORIZED, "无权限修改文章内容")

        //生成文本对象
        var contentId = 0L
        if (!content.isBlank()) {
            contentId = articleApi.updateContent(UpdateArticleContentReq().apply {
                this.content = content
                this.editorUid = me.id
                this.articleId = article.id
            }).contentId
        }

        //更新文章元数据
        articleApi.updateMeta(UpdateArticleReq().apply {
            this.id = id
            this.args = strDict {
                if (!title.isBlank()) this += "title" to title
                if (category > 0) this += "category" to "$category"
                if (!tags.isBlank()) this += "tags" to tags
                this["last_edit_time"] = "${System.currentTimeMillis()}"
                this["last_edit_uid"] = "${me.id}"
                if (contentId != 0L) this += "content_id" to "$contentId"
            }
        })

        return ok()
    }

    //删除文章
    @PostMapping("/post/{id}/delete")
    fun deleteArticle(@PathVariable("id") id: Long): Resp {

        val me = sessionApi.checkToken(req.token).account
        val article: Article = articleApi.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        //只有作者和管理员才能删除
        if (me.role != Account.Role.ADMIN && article.author != me.id)
            abort(Err.UNAUTHORIZED, "无权限删除文章内容")

        //更新文章元数据
        articleApi.updateMeta(UpdateArticleReq().apply {
            this.id = id
            this.args = hashMapOf("state" to "${Article.State.DELETE}")
        })

        return ok()
    }

    //获取某篇已发布的文章
    @GetMapping("/post/{id}")
    fun getArticleById(@PathVariable("id") id: Long): Resp {

        val article: Article = articleApi.queryById(QueryArticleByIdReq().apply {
            this.ids = arrayListOf(id)
        }).articles[id] ?: abort(Err.ARTICLE_NOT_EXISTS)

        //只有管理员才能看到封禁和删除的文章内容
        if (article.state == Article.State.BAN || article.state == Article.State.DELETE) {
            tryExec(Err.ARTICLE_NOT_EXISTS) {
                val account = sessionApi.checkToken(req.token).account
                assert(account.role == Account.Role.ADMIN)
            }
        }

        val author = userApi.queryById(QueryUserReq().apply {
            this.id = arrayListOf(article.author)
        }).info[article.author] ?: UserInfo()

        val lastEditor = userApi.queryById(QueryUserReq().apply {
            this.id = arrayListOf(article.lastEditUID)
        }).info[article.author] ?: UserInfo()

        val content = textApi.getById(QueryTextReq().apply {
            this.id = arrayListOf(article.contentId)
        }).result[article.contentId] ?: TextContent()

        return ok {
            it["author"] = author
            it["article"] = article
            it["content"] = content
            it["last_editor"] = lastEditor
        }
    }

}