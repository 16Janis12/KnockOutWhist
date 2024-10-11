
object H {

  class KnockOutWhist {

  }

  def main(args: Array[String]): Unit = {
    println("Welcome to Knock out Whist")


    val eol = sys.props("line.seperator") // String =

    def bar(cellWidth: Int = 9, cellNum: Int = 1) =
      (("-" * cellWidth) * cellNum) + "\n"

    def cells(cellWidth: Int = 7, cellNum: Int = 1) =
      ("|" + " " * cellWidth) * cellNum + "|" + "\n"

    def meshk =
      bar() + cells() + "|Karten | \n" + cells() * 2 + bar()

    def mesha =
      bar() + cells() + "|Ablage |\n" + cells() * 2 + bar()

    def mesht1 =
      bar() + cells() + "|Trumpfe|\n" + "|s1     |\n" + cells() + bar()

    def mesht2 =
      bar() + cells() + "|Trumpfe|\n" + "|s2     |\n" + cells() + bar()

    print(meshk)
    print(mesha)
    print(mesht1)
    print(mesht2)
  }
}