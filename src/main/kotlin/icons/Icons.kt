package icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object Icons {
    val defaultIcon: Icon = IconLoader.getIcon("/icons/love.png", Icons::class.java).apply {  }
    val defaultIconBig: Icon = IconLoader.getIcon("/icons/love_saranghe.png", Icons::class.java).apply {  }
}
