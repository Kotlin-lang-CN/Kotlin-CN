package cn.kotliner.forum.service.article.req

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf

class QueryReplyCountByAuthorReq {
    @Protobuf(order = 1, required = true, fieldType = FieldType.UINT64)
    var author: List<Long> = ArrayList()
}