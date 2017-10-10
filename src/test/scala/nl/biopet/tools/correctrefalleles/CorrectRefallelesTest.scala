package nl.biopet.tools.correctrefalleles

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

object CorrectRefallelesTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      ToolTemplate.main(Array())
    }
  }
}
