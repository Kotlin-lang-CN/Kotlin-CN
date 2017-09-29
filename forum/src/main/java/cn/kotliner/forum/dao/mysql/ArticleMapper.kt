package cn.kotliner.forum.dao.mysql

import cn.kotliner.forum.domain.Article
import org.apache.ibatis.annotations.*

@Mapper
interface ArticleMapper {

    @Select("""
    SELECT * FROM article
    WHERE id = #{id}
    LIMIT 1
    """)
    @Results(Result(property = "createTime", column = "create_time"),
            Result(property = "lastEdit", column = "last_edit_time"),
            Result(property = "lastEditUID", column = "last_edit_uid"),
            Result(property = "contentId", column = "content_id"))
    fun queryById(id: Long): Article?

    @SelectProvider(type = SqlGenerator::class, method = "articleQueryLatestArticle")
    @Results(Result(property = "createTime", column = "create_time"),
            Result(property = "lastEdit", column = "last_edit_time"),
            Result(property = "lastEditUID", column = "last_edit_uid"),
            Result(property = "contentId", column = "content_id"))
    fun queryLatest(args: HashMap<String, String>): List<Article>

    @Select("""
    SELECT * FROM article
    WHERE author = #{id}
    AND (state = ${Article.State.NORMAL} OR state = ${Article.State.FINE})
    ORDER BY create_time DESC
    """)
    @Results(Result(property = "createTime", column = "create_time"),
            Result(property = "lastEdit", column = "last_edit_time"),
            Result(property = "lastEditUID", column = "last_edit_uid"),
            Result(property = "contentId", column = "content_id"))
    fun queryByAuthor(id: Long): List<Article>


    @Select("""
    SELECT COUNT(*) FROM article
    WHERE author = #{id}
    AND (state = ${Article.State.NORMAL} OR state = ${Article.State.FINE})
    """)
    fun queryCountByAuthor(id: Long): Int

    @Select("""
    SELECT COUNT(*) FROM article
    WHERE (state = ${Article.State.NORMAL}
    OR state = ${Article.State.FINE})
    """)
    fun queryAll(): Int

    @Insert("""
    INSERT INTO article
    VALUES
    (#{id}, #{title}, #{author}, #{createTime}, #{category}, #{tags},
    #{lastEdit}, #{lastEditUID}, #{state}, #{contentId})
    """)
    fun insert(article: Article)

    @Update("""
    UPDATE article set
    title = #{title},
    author = #{author},
    create_time = #{createTime},
    category = #{category},
    tags = #{tags},
    last_edit_time = #{lastEdit},
    last_edit_uid = #{lastEditUID},
    state = #{state},
    content_id = #{contentId}
    WHERE
    id = #{id}
    """)
    fun update(article: Article)

    @UpdateProvider(type = SqlGenerator::class, method = "articleQueryLatestArticle")
    fun updateByArgs(args: HashMap<String, Any>)

    class SqlGenerator {

        //更新文章内容
        fun articleUpdateArticleByArgs(args: HashMap<String, Any>): String {
            return """
            UPDATE article SET
             ${StringBuilder().apply {
                args.forEach { k, _ -> append("$k = #{$k}, ") }
                setLength(length - ", ".length)
            }}
            WHERE id = #{id}
            """
        }

        //查询文章内容
        fun articleQueryLatestArticle(args: HashMap<String, String>): String {
            val whereCause = when {
                args.isEmpty() -> ""
                else -> {
                    val sb = StringBuilder("WHERE")
                    args.forEach { k, v ->
                        if (k == "state" || k == "category") {
                            sb.append(" (")
                            v.split(",").forEach {
                                sb.append("$k = $it OR ")
                            }
                            sb.setLength(sb.length - "OR ".length)
                            sb.append(") AND ")
                        } else {
                            sb.append("$k = #{k} AND ")
                        }
                    }
                    sb.setLength(sb.length - "AND ".length)
                    sb.toString()
                }
            }
            return """
            SELECT * from article
            $whereCause
            ORDER BY create_time DESC
            """
        }
    }
}