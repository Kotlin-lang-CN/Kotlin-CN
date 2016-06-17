package tech.kotlin.china.restful.controller

import com.github.pagehelper.PageHelper
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController
import tech.kotlin.china.restful.database.ArticleMapper
import tech.kotlin.china.restful.database.use
import tech.kotlin.china.restful.model.*
import tech.kotlin.china.restful.utils.*

@RestController
class ArticleController : _Base() {

    val ARTICLE_PAGE_SIZE = 10 //账号列表的分页大小

    @Doc("文章内容")
    @RequestMapping("/article/{aid}", method = arrayOf(GET))
    fun article(@PathVariable("aid") @Doc("文章id") aid: Long) = session {
        it.use(ArticleMapper::class.java) {
            defaultMode(Fail(message = "文章不存在", status = 404)) {
                val article = it.queryByAID(aid)!!
                Data(Maps.p("aid", article.aid).p("title", article.title)
                        .p("description", article.description)
                        .p("content", article.content)
                        .p("author", article.author)
                        .p("create_time", article.create_time.format()))
            }
        }
    }

    @Doc("查看当前文章总数")
    @RequestMapping("/article/count", method = arrayOf(GET))
    fun getArticleCount() = session { it.use(ArticleMapper::class.java) { Data(it.getArticleCount()) } }

    @Doc("查看文章列表")
    @RequestMapping("/article/list/{page}", method = arrayOf(GET))
    fun articleList(@PathVariable("page") @Doc("分页") page: Int) = session {
        it.use(ArticleMapper::class.java) {
            PageHelper.startPage<Article>((page - 1) * ARTICLE_PAGE_SIZE + 1, page * ARTICLE_PAGE_SIZE)
            Data(it.queryArticleList().map {
                Maps.p("aid", it.aid).p("title", it.title)
                        .p("description", it.description)
                        .p("create_time", it.create_time.format())
                        .p("author", it.author)
            })
        }
    }

    @Doc("查看我的文章列表")
    @RequestMapping("/article/mine/{page}", method = arrayOf(GET))
    fun myArticles(@PathVariable("page") @Doc("文章分页") page: Int) = authorized().session {
        it.use(ArticleMapper::class.java) {
            PageHelper.startPage<Article>((page - 1) * ARTICLE_PAGE_SIZE + 1, page * ARTICLE_PAGE_SIZE)
            val uid = getUID()!!
            val articles = it.queryByUID(uid)
            Data(articles.map {
                Maps.p("aid", it.aid).p("title", it.title)
                        .p("description", it.description)
                        .p("create_time", it.create_time.format())
                        .p("author", it.author)
                        .p("status", if (it.forbidden) "管理员锁定" else "正常")
            })
        }
    }

    @Doc("发布一篇文章")
    @RequestMapping("/article/publish", method = arrayOf(POST))
    fun publishArticle(@RequestBody @Doc("发布文章请求") form: ArticleForm) = check(form) {
        it.title.forbid("标题长度不合法") { it.length > 128 || it.length == 0 }
        it.content.forbid("请输入文章内容") { it.length == 0 }
        it.description.forbid("请输入文章描述") { it.length == 0 }
    }.authorized().session(transaction = true) {
        it.use(ArticleMapper::class.java) {
            defaultMode(Fail("发布失败")) {
                it.publishArticle(Maps.p("title", form.title)
                        .p("uid", getUID()!!)
                        .p("content", form.content)
                        .p("description", form.description))
                Success()
            }
        }
    }

    @Doc("修改我的文章")
    @RequestMapping("/article/{aid}/update", method = arrayOf(POST))
    fun updateArticle(@PathVariable("aid") @Doc("文章id") aid: Long,
                      @RequestBody @Doc("文章内容") form: ArticleForm) = check(form) {
        it.title.forbid("标题长度不合法") { it.length > 100 || it.length == 0 }
        it.content.forbid("请输入文章内容") { it.length == 0 }
        it.description.forbid("请输入文章描述") { it.length == 0 }
    }.authorized {
        val uid = getUID()!!
        it.use(ArticleMapper::class.java) { it.queryByAID(aid)!!.author == uid }
    }.session(transaction = true) {
        it.use(ArticleMapper::class.java) {
            defaultMode(Fail("修改失败, 请刷新后重试")) {
                it.updateArticle(Maps.p("aid", aid)
                        .p("title", form.title)
                        .p("content", form.content)
                        .p("description", form.description))
                Success()
            }
        }
    }

    @Doc("锁定文章")
    @RequestMapping("/article/{aid}/disable", method = arrayOf(POST))
    fun disableArticle(@PathVariable("aid") @Doc("文章id") aid: Long) = authorized(admin = true)
            .session(transaction = true) {
                it.use(ArticleMapper::class.java) {
                    defaultMode(Fail("操作失败, 请重试")) {
                        it.enableArticle(Maps.p("aid", aid).p("disable", true))
                        Success()
                    }
                }
            }

    @Doc("解锁文章")
    @RequestMapping("/article/{aid}/enable", method = arrayOf(POST))
    fun enableArticle(@PathVariable("aid") @Doc("文章id") aid: Long) = authorized(admin = true)
            .session(transaction = true) {
                it.use(ArticleMapper::class.java) {
                    defaultMode(Fail("操作失败, 请重试")) {
                        it.enableArticle(Maps.p("aid", aid).p("disable", false))
                        Success()
                    }
                }
            }
}