package arm_assignment

import java.io.File
import net.fornwall.jelf._

object Main {
  def main(args: Array[String]): Unit = {
    val elfFile = ElfFile.fromFile(new File(args(0)))
  }
}
