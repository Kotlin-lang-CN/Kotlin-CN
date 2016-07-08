package tech.kotlin.china.controller.rest

import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import tech.kotlin.china.database.ArticleMapper
import tech.kotlin.china.database.page
import tech.kotlin.china.framework._Rest
import tech.kotlin.china.framework.of
import tech.kotlin.china.model.ArticleForm
import utils.dataflow.forbid
import utils.dataflow.require
import utils.date.format
import utils.map.p
import utils.map.row
import java.util.*

@RestController class ArticleController : _Rest() {

    @RequestMapping("/article/{aid}", method = arrayOf(GET))
    fun article(@PathVariable("aid") aid: Long) = response {
        db read {
            val article = it.of<ArticleMapper>().queryByAID(aid)
                    .forbid("文章不存在", 404) { it == null }!!
                    .row()
            article.p("create_time", (article["create_time"] as Date).format())
        }
    }

    @RequestMapping("/article/list", method = arrayOf(GET))
    fun articleList(@RequestParam("category", defaultValue = "all") category: String,
                    @RequestParam("page", defaultValue = "1") page: Int) = response {
        page.require { page > 0 }
        db read  {
            it.page(page)
            when (category) {
                "all" -> it.of<ArticleMapper>().articleList()
                else -> it.of<ArticleMapper>().articleListWithCategory(category)
            }.map {
                it.p("create_time", (it["create_time"] as Date).format())
            }
        }
    }

    @RequestMapping("/article/mine", method = arrayOf(GET))
    fun myArticleList(@RequestParam("category", defaultValue = "all") category: String,
                      @RequestParam("page", defaultValue = "1") page: Int) = response {
        page.require { page > 0 }
        val account = auth.loginRequire()
        db read {
            it.page(page)
            when (category) {
                "all" -> it.of<ArticleMapper>().myArticleList(account.id)
                else -> it.of<ArticleMapper>().myArticleListWithCategory(
                        p("category", category).p("author", account.id))
            }.map {
                it.p("create_time", (it["create_time"] as Date).format())
            }
        }
    }

    @RequestMapping("/article/publish", method = arrayOf(POST))
    fun publishArticle(@RequestBody articleForm: ArticleForm) = response {
        articleForm.title.forbid("请输入标题") { it.length == 0 }
        articleForm.content.forbid("请输入内容") { it.length == 0 }
        val account = auth.loginRequire()
        db write {
            val articleMapper = it.of<ArticleMapper>()
            articleMapper.publishArticle(articleForm.row().p("author", account.id))
        }
    }
}
