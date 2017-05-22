package tech.kotlin.service.article

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import com.fasterxml.jackson.annotation.JsonProperty
import tech.kotlin.model.Article
import tech.kotlin.common.exceptions.Abort
import tech.kotlin.common.rpc.RpcInterface
import tech.kotlin.model.EmptyResp
import tech.kotlin.service.Service.ARTICLE_CHANGE_STATE
import tech.kotlin.service.Service.ARTICLE_CREATE
import tech.kotlin.service.Service.ARTICLE_QUERY_BY_ID
import tech.kotlin.service.Service.ARTICLE_QUERY_IN_ORDER
import tech.kotlin.service.Service.ARTICLE_UPDATE

/*********************************************************************
 * Created by chpengzh@foxmail.com
 * Copyright (c) http://chpengzh.com - All Rights Reserved
 *********************************************************************/
interface ArticleService {

    @RpcInterface(ARTICLE_CREATE)
    @Throws(Abort::class)
    fun create(req: CreateArticleReq): ArticleResp

    @RpcInterface(ARTICLE_UPDATE)
    @Throws(Abort::class)
    fun update(req: UpdateArticleReq): ArticleResp

    @RpcInterface(ARTICLE_CHANGE_STATE)
    @Throws(Abort::class)
    fun changeState(req: ChangeArticleStateReq): ArticleResp

    @RpcInterface(ARTICLE_QUERY_BY_ID)
    @Throws(Abort::class)
    fun queryById(req: QueryArticleByIdReq): QueryArticleByIdResp

    @RpcInterface(ARTICLE_QUERY_IN_ORDER)
    @Throws(Abort::class)
    fun queryInOrder(req: QueryArticleInOrderReq): QueryArticleInOrderResp

}

class ArticleResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "model")
    var article: Article = Article()

}

class CreateArticleReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "标题")
    @JsonProperty("title")
    var title: String = ""

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT64, description = "作者uid")
    @JsonProperty("author")
    var author: Long = 0

    @Protobuf(order = 3, required = true, fieldType = FieldType.UINT32, description = "文章分类id")
    @JsonProperty("category")
    var category: Int = 0

    @Protobuf(order = 4, required = false, fieldType = FieldType.STRING, description = "标签")
    @JsonProperty("tags")
    var tags: String = ""

}

class UpdateArticleReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "文章id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.MAP, description = "修改内容")
    @JsonProperty("args")
    var args: Map<String, String> = HashMap()
}

class ChangeArticleStateReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "文章id")
    @JsonProperty("id")
    var id: Long = 0

    @Protobuf(order = 2, required = true, fieldType = FieldType.UINT32, description = "修改封禁状态")
    @JsonProperty("state")
    var state: Int = Article.State.NORMAL

}

class QueryArticleByIdReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64, description = "查询id")
    @JsonProperty("ids")
    var ids = ArrayList<Long>()

}

class QueryArticleByIdResp {

    @Protobuf(order = 1, required = false, fieldType = FieldType.MAP, description = "文章查询结果")
    @JsonProperty("articles")
    var articles: Map<Long, Article> = HashMap()

}

class QueryArticleInOrderReq {

    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT32, description = "排序方式")
    @JsonProperty("order")
    var order: Int = Order.DATE_DESC

    @Protobuf(order = 2, required = false, fieldType = FieldType.UINT64, description = "查询uid")
    @JsonProperty("uid")
    var uid: Long = 0

    @Protobuf(order = 3, required = false, fieldType = FieldType.STRING, description = "查询tags")
    @JsonProperty("tags")
    var tags: String = ""

    @Protobuf(order = 4, required = true, fieldType = FieldType.UINT32, description = "查询偏移值")
    @JsonProperty("offset")
    var offset: Int = 0

    object Order {
        const val DATE_DESC = 0
    }
}

class QueryArticleInOrderResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.OBJECT, description = "查询文章")
    @JsonProperty("articles")
    var articles: List<Article> = ArrayList()

}