package arm_assignment

import java.io._
import net.fornwall.jelf._
import spray.json._
import arm_assignment.ElfFileJsonProtocol._

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      println("Usage: sbt \"run <path_to_elf>\"")
      System.exit(1)
    }

    try {
      val elfFile = ElfFile.fromFile(new File(args(0)))
      println(elfFile.toJson.prettyPrint)
    } catch {
      case elfe: ElfException =>
        println("ERROR: \"" + args(0) + "\" isn't ELF file.")
      case fnfe: FileNotFoundException =>
        println("ERROR: \"" + args(0) + "\" file not found. Check path and permissions.")
      case _: Throwable => println("ERROR: unknown.")
    }
  }
}
