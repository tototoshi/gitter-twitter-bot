package gittertwitterbot


case class GitterUser(username: String)
case class GitterMessage(fromUser: GitterUser, text: String)
