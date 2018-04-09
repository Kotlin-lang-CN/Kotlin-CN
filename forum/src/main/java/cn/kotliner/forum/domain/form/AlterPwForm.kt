package cn.kotliner.forum.domain.form

import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotBlank

data class AlterPwForm(

        @NotBlank(message = "无效的密码")
        @Length(min = 8, message = "密码长度过短")
        var password: String = ""

)