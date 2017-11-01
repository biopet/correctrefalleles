package nl.biopet.tools.correctrefalleles

import nl.biopet.utils.test.tools.ToolTest
import org.testng.annotations.Test

class CorrectRefAllelesTest extends ToolTest[Args] {
  def toolCommand: CorrectRefAlleles.type = CorrectRefAlleles
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      CorrectRefAlleles.main(Array())
    }
  }
}
