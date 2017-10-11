package nl.biopet.tools.correctrefalleles

import nl.biopet.test.BiopetTest
import org.testng.annotations.Test

class CorrectRefallelesTest extends BiopetTest {
  @Test
  def testNoArgs(): Unit = {
    intercept[IllegalArgumentException] {
      CorrectRefalleles.main(Array())
    }
  }
}
