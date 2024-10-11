
object H {

  class KnockOutWhist {

  }

  val eol = sys.props("line.separator")

  def bar(cellWidth: Int = 9, cellNum: Int = 1) =
      (("-" * cellWidth) * cellNum) + eol

    def cells(cellWidth: Int = 7, cellNum: Int = 1) =
      ("|" + " " * cellWidth) * cellNum + "|" + eol

    def meshk =
      bar() + cells() + "|Karten | " + eol + cells() * 2 + bar()

    def mesha =
      bar() + cells() + "|Ablage | " + eol + cells() * 2 + bar()

    def mesht1 =
      bar() + cells() + "|Trumpfe| " + eol + "|s1     |\n" + cells() + bar()

    def mesht2 =
      bar() + cells() + "|Trumpfe|" + eol + "|s2     |\n" + cells() + bar()

  def main(args: Array[String]): Unit = {
    println("Welcome to Knock out Whist")

    print(meshk)
    print(mesha)
    print(mesht1)
    print(mesht2)
  }
}