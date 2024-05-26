package component.group

import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.useState
import ru.altmanea.webapp.common.Item
import ru.altmanea.webapp.common.ItemId

external interface GroupListProps : Props {
    var groups: Array<Item<String>>
    var changeGroup: (Item<String>) -> Unit
    var deleteGroup: (ItemId) -> Unit
}

val CGroupList = FC<GroupListProps>("GroupList") { props ->
    var editedId by useState("")
    ReactHTML.ol {
        props.groups.filter { it.elem != "" }.forEach { group ->
            ReactHTML.li {
                if (group.id == editedId) {
                    CChangeGroup {
                        oldGroup = group.elem
                        saveGroup = {
                            props.changeGroup(Item(it, editedId, group.version))
                            editedId = ""
                        }
                    }
                }
                CGroupItem {
                    this.group = group.elem
                }
                ReactHTML.button {
                    +"✕"
                    onClick = {
                        props.deleteGroup(group.id)
                    }
                }
                ReactHTML.button {
                    +"✎"
                    onClick = {
                        editedId = group.id
                    }
                }
            }
        }
    }
}