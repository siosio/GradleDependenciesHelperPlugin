package siosio

class SearchParam(text: String) {
  val group: String
  val name: String
  val text: String

  init {
    val list = split(text)
    if (list.size in (2..3)) {
      this.group = list[0]
      this.name = list[1]
      this.text = ""
    } else {
      this.group = ""
      this.name = ""
      this.text = text
    }
  }

  fun isFindVersion() = text.isEmpty()
}

