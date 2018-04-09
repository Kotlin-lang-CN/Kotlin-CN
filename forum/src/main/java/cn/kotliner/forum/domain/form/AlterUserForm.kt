package cn.kotliner.forum.domain.form

import cn.kotliner.forum.utils.validate.Checker
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotBlank
import org.springframework.util.Assert

data class AlterUserForm(

        @NotBlank(message = "无效的用户名")
        @Length(min = 2, message = "用户名过短")
        var username: String = "",

        @Email
        var email: String = "",

        var uid: Long = 0L,

        var logo: String

) : Checker {
    override fun check() {
        Assert.isTrue(uid != 0L, "缺少uid")
    }
}