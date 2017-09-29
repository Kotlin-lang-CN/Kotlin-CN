package cn.kotliner.forum.controller.article

import cn.kotliner.forum.utils.gateway.Request
import cn.kotliner.forum.service.Err
import cn.kotliner.forum.service.account.api.SessionApi
import cn.kotliner.forum.service.article.api.FlowerApi
import cn.kotliner.forum.service.article.req.CountStarReq
import cn.kotliner.forum.service.article.req.StarReq
import cn.kotliner.forum.utils.gateway.Resp
import cn.kotliner.forum.utils.gateway.ok
import cn.kotliner.forum.exceptions.tryExec
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Validated
@RestController
@RequestMapping("/api/flower")
class FlowerController {

    @Resource private lateinit var req: Request
    @Resource private lateinit var sessionApi: SessionApi
    @Resource private lateinit var flowerApi: FlowerApi

    //点赞文章
    @PostMapping("/article/{id}/star")
    fun startArticle(@PathVariable("id") articleId: Long): Resp {
        val me = sessionApi.checkToken(req.token).account
        flowerApi.star(StarReq().apply {
            this.owner = me.id
            this.poolId = "article:$articleId"
        })
        return ok()
    }

    //取消点赞文章
    @PostMapping("/article/{id}/unstar")
    fun unStarArticle(@PathVariable("id") articleId: Long): Resp {
        val me = sessionApi.checkToken(req.token).account
        flowerApi.unstar(StarReq().apply {
            this.owner = me.id
            this.poolId = "article:$articleId"
        })
        return ok()
    }

    //获取对文章的点赞状态
    @GetMapping("/article/{id}/star")
    fun queryArticle(@PathVariable("id") articleId: Long): Resp {
        val account = sessionApi.checkToken(req.token).account
        val data = flowerApi.queryStar(StarReq().apply {
            this.owner = account.id
            this.poolId = "article:$articleId"
        })
        return ok {
            it["has_star"] = data.hasStar
            it["flower"] = data.flower
        }
    }

    //获取文章的点赞数量
    @GetMapping("/article/star/count")
    fun queryCountByArticle(@RequestParam("ids") articleIds: String): Resp {
        val ids = articleIds.tryExec(Err.PARAMETER, "非法的id信息") {
            it.split(",")
                    .filter { it.isNotBlank() }
                    .map { it.trim().toLong() }
                    .map { "article:$it" }
        }
        val resp = flowerApi.countStar(CountStarReq().apply { this.poolIds = ids })
        return ok {
            it["data"] = resp.result.entries.map { it.key.split(":")[1] to it.value }.toMap()
        }
    }

    //点赞文章
    @PostMapping("/reply/{id}/star")
    fun starReply(@PathVariable("id") replyId: Long): Resp {
        val me = sessionApi.checkToken(req.token).account
        flowerApi.star(StarReq().apply {
            this.owner = me.id
            this.poolId = "reply:$replyId"
        })
        return ok()
    }

    //取消点赞评论
    @PostMapping("/reply/{id}/unstar")
    fun unstarReply(@PathVariable("id") replyId: Long): Resp {
        val me = sessionApi.checkToken(req.token).account
        flowerApi.unstar(StarReq().apply {
            this.owner = me.id
            this.poolId = "reply:$replyId"
        })
        return ok()
    }

    //获取对评论的坚赞状态
    @GetMapping("/reply/{id}/star")
    fun queryReply(@PathVariable("id") replyId: Long): Resp {
        val account = sessionApi.checkToken(req.token).account

        val data = flowerApi.queryStar(StarReq().apply {
            this.owner = account.id
            this.poolId = "reply:$replyId"
        })
        return ok {
            it["has_star"] = data.hasStar
            it["flower"] = data.flower
        }
    }

    //获取评论的点赞数量
    @GetMapping("/reply/star/count")
    fun queryCountByReply(@RequestParam("ids") articleIds: String): Resp {
        val ids = articleIds.tryExec(Err.PARAMETER, "非法的id信息") {
            it.split(",")
                    .filter { it.isNotBlank() }
                    .map { it.trim().toLong() }
                    .map { "reply:$it" }
        }
        val resp = flowerApi.countStar(CountStarReq().apply { this.poolIds = ids })
        return ok {
            it["data"] = resp.result.entries.map { it.key.split(":")[1] to it.value }.toMap()
        }
    }

}