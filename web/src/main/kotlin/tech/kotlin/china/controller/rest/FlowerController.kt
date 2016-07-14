package tech.kotlin.china.controller.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tech.kotlin.china.database.ArticleMapper
import tech.kotlin.china.database.CommentMapper
import tech.kotlin.china.database.FlowerMapper
import tech.kotlin.china.framework._Rest
import tech.kotlin.china.framework.of
import utils.dataflow.forbid
import utils.dataflow.require
import utils.map.p

@RestController class FlowerController : _Rest() {

    @RequestMapping("/flower", method = arrayOf(POST))
    fun flower(@RequestParam("oid") oid: Long, @RequestParam("mode") mode: String) = response {
        mode.require("未知的评论类型") { mode.equals("article") || mode.equals("comment") }
        val account = auth.loginRequire()
        db write {
            val args = p("oid", oid).p("actor", account.id)
            when (mode) {
                "article" -> {
                    val article = it.of<ArticleMapper>().queryByAID(oid).forbid("该文章不存在") { it == null }!!
                    args.p("mode", 0).p("praised", article["author"])
                }
                "comment" -> {
                    val comment = it.of<CommentMapper>().queryByCID(oid).forbid("该评论不存在") { it == null }!!;
                    args.p("mode", 1).p("praised", comment["commenter"])
                }
            }
            val flowerMapper = it.of<FlowerMapper>()
            flowerMapper.queryAction(args).forbid("你已经点过赞了") { it != null }
            flowerMapper.flower(args)
        }
    }

    @RequestMapping("/flower/cancel", method = arrayOf(POST))
    fun cancelFlower(@RequestParam("oid") oid: Long, @RequestParam("mode") mode: String) = response {
        mode.require("位置的评论类型") { mode.equals("article") || mode.equals("comment") }
        val account = auth.loginRequire()
        db write {
            val args = p("oid", oid).p("actor", account.id).p("mode", if (mode.equals("article")) 0 else 1)
            val flowerMapper = it.of<FlowerMapper>()
            flowerMapper.queryAction(args).forbid("你还没点过赞呢") { it == null }
            flowerMapper.cancelFlower(args)
        }
    }

}
