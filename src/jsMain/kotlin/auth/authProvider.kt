package auth

import UserInfo
import react.*
import react.dom.html.ReactHTML.h1
import userInfoContext

fun ChildrenBuilder.authProvider(block: ChildrenBuilder.() -> Unit) =
    child(
        FC<PropsWithChildren>("AuthProvider") {
            var userInfo by useState<UserInfo>(null)
            CAuthContainer {
                user = userInfo?.first
                signOff = { userInfo = null }
                signIn = { userInfo = it }
            }
            if (userInfo == null)
                h1 { +"Authentication is required" }
            else
                userInfoContext.Provider(userInfo) {
                    block()
                }
        }.create()
    )
