package cn.kotliner.forum.domain.form

import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotBlank

data class RegisterForm(

        @NotBlank(message = "无效的用户名")
        @Length(min = 2, message = "用户名过短")
        var username: String = "",

        @NotBlank(message = "无效的密码")
        @Length(min = 8, message = "密码长度过短")
        var password: String = "",

        @Email
        var email: String = "",

        var github_token: String = "",

        var logo: String = ""

)