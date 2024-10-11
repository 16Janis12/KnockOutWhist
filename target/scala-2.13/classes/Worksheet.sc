val eol = sys.props("line.seperator") // String =
def bar(cellwidth: Int = 3, cellNum: Int = 3) = (("+" + "-" * cellwidth)*cellNum) + "+" + eol
def cells(cellwidth: Int = 3, cellNum: Int = 3) = ("|" + " " * cellwidth) *cellNum + "|" + eol
def mesh(cellwidth: Int = 3, cellNum: Int = 3) = (bar(cellwidth, cellNum) + cells(cellwidth, cellNum)) * cellNum + eol

bar(8,6)
cells()
mesh()