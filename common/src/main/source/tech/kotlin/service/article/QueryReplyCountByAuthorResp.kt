package tech.kotlin.service.article

import com.baidu.bjf.remoting.protobuf.FieldType
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf

class QueryReplyCountByAuthorResp {

    @Protobuf(order = 1, required = true, fieldType = FieldType.MAP)
    var result: Map<Long, Int> = HashMap()

}