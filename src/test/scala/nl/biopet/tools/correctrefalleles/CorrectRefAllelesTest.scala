package nl.biopet.tools.correctrefalleles

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

class CorrectRefAllelesTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      CorrectRefAlleles.main(Array())
    }
  }
}
