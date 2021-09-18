package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.Container
import com.dimafeng.testcontainers.specs2.TestContainerForAllSpec.{MultipleTestsSpec, TestSpec}
import org.mockito.Mockito.{times, verify}
import org.scalatestplus.mockito.MockitoSugar
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import specs2.run

class TestContainerForAllSpec extends Specification with MockitoSugar {

  "TestContainerForAll" should {
    "call all appropriate methods of the container" in {
      val container = mock[SampleContainer]

      val spec = new TestSpec(true must beTrue, container)
      run(spec)

      verify(container).start()
      verify(container).stop()
      ok
    }

    "call all appropriate methods of the container if assertion fails" in {
      val container = mock[SampleContainer]

      val spec = new TestSpec(false must beTrue, container)
      run(spec)

      verify(container).start()
      verify(container).stop()
      ok
    }

    "start and stop container only once" in {
      val container = mock[SampleContainer]

      val spec = new MultipleTestsSpec(true must beTrue, container)
      run(spec)

      verify(container, times(1)).start()
      verify(container, times(1)).stop()
      ok
    }
  }
}

object TestContainerForAllSpec {

  trait ContainerSpec extends Specification with TestContainerForAll

  protected class TestSpec(result: => MatchResult[Any], cont: Container) extends ContainerSpec {
    override val container: Container = cont

    "test" in {
      result
    }
  }

  protected class MultipleTestsSpec(result: => MatchResult[Any], cont: Container) extends ContainerSpec {
    override val container: Container = cont

    "test 1" in {
      result
    }

    "test 2" in {
      result
    }
  }

}
