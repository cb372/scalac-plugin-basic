package basic

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent
import java.nio.file._
import java.nio.charset.StandardCharsets

class BasicPlugin(val global: Global) extends Plugin {
  import global._

  val name = "basic"
  val description = "generates BASIC code"
  val components = List[PluginComponent](Component)

  private object Component extends PluginComponent {
    val global: BasicPlugin.this.global.type = BasicPlugin.this.global
    val runsAfter = List("typer")
    val phaseName = BasicPlugin.this.name
    override val description = BasicPlugin.this.description
    def newPhase(prev: Phase) = new BasicPhase(prev)

    class BasicPhase(prev: Phase) extends StdPhase(prev) {
      override def name = BasicPlugin.this.name
      def apply(unit: CompilationUnit): Unit = {
        for {
          DefDef(
            mod,
            methodName,
            typeArgs,
            paramLists,
            tpe,
            body
          ) <- unit.body if methodName.toString.startsWith("BASIC_")
        } {
          val programName = methodName.toString.substring(6)
          println(s"Found a BASIC program: $programName")
          val lines = parseProgram(body)
          val path = writeToFile(lines, programName)
          println(s"Wrote BASIC program to file: ${path.toAbsolutePath}")
        }
      }

      private def parseProgram(tree: Tree): List[String] = {
        var lines: List[(Int, String)] = Nil
        var lineNum = 0
        def nextLineNum() = { lineNum = lineNum + 10; lineNum }
        def appendLine(cmd: String): Int = {
          val lineNum = nextLineNum()
          lines = (lineNum, cmd) :: lines
          lineNum
        }

        val traverser = new Traverser {
          override def traverse(tree: Tree): Unit = tree match {
            case LabelDef(name, _, 
              If(
                Literal(Constant(true)),
                ifBody,
                _
              )
            ) =>
              // this is a while(true) loop
              val gotoLine = appendLine("REM")
              super.traverse(ifBody)
              appendLine(s"GOTO $gotoLine")
            case Apply(
              Select(Select(This(TypeName("scala")), TermName("Predef")), TermName("println")), 
              List(Literal(Constant(message)))
            ) => appendLine(s"""PRINT "$message"""")
            case _ => super.traverse(tree)
          }
        }
        traverser.traverse(tree)

        lines.reverse.map { case (lineNum, cmd) => s"$lineNum $cmd" }
      }

      private def writeToFile(lines: List[String], programName: String): Path = {
        val bytes = lines.mkString("\n").getBytes("UTF-8")
        val path = Paths.get(s"${programName}.bas")
        Files.write(path, bytes)
        path
      }

    }
  }
}
