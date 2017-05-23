package tech.kotlin.model.response

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf
import tech.kotlin.model.domain.Article

class ArticleResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.STRING, description = "model")
    var article: Article = Article()

}