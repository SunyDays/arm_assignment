package arm_assignment

import java.io.File
import net.fornwall.jelf._
import spray.json._
import arm_assignment.ElfFileJsonProtocol._

object Main {
  def main(args: Array[String]): Unit = {
    val elfFile = ElfFile.fromFile(new File(args(0)))
    println(elfFile.toJson.prettyPrint)
  }
}
