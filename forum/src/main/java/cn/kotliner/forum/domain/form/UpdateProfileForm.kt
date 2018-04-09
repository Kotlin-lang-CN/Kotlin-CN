package cn.kotliner.forum.domain.form

import cn.kotliner.forum.domain.model.Profile
import cn.kotliner.forum.utils.validate.Checker
import org.springframework.util.Assert

data class UpdateProfileForm(

        var uid: Long = 0L,
        var gender: Int = Profile.Gender.FEMALE,
        var github: String = "",
        var blog: String = "",
        var company: String = "",
        var location: String = "",
        var description: String = "",
        var education: String = ""

) : Checker {
    override fun check() {
        Assert.isTrue(uid != 0L, "无效的uid")
    }
}