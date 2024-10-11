
object Main {

  class KnockOutWhist {

  }

  val eol = sys.props("line.separator")

  def bar(cellWidth: Int = 9, cellNum: Int = 1) =
    ("-" * cellWidth) * cellNum

  def cells(cellWidth: Int = 7, cellNum: Int = 1) =
    ("|" + " " * cellWidth) * cellNum + "|"

  def meshk =
    bar() + cells() + eol + "|Karten | " + eol + (cells()+ eol) * 2 + bar() + eol

  def mesha =
    bar() + cells() + eol + "|Ablage | " + eol + (cells()+ eol) * 2 + bar() + eol

  def mesht1 =
    bar() + eol + cells() + eol + "|Trumpfe| " + eol + "|s1     |\n" + cells() + eol + bar() + eol

  def mesht2 =
    bar() + eol + cells() + eol + "|Trumpfe|" + eol + "|s2     |\n" + cells() + eol + bar() + eol
  def meshTable = {
    bar() + "\t" + bar() + eol + cells() + "\t" + cells() + eol +
      "|Karten |" + "\t" + "|Ablage |" + eol + (cells() + "\t" + cells() + eol) * 2 +
      bar() + "\t" + bar() + eol
  }

  def main(args: Array[String]): Unit = {
    println("Welcome to Knock out Whist")

    print(meshTable)
    print(mesht1)
    print(mesht2)
  }
}