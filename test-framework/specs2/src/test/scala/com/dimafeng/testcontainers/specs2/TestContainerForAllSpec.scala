package com.dimafeng.testcontainers.specs2

import com.dimafeng.testcontainers.ContainerDef
import com.dimafeng.testcontainers.specs2.TestContainerForAllSpec.{EmptySpec, MultipleTestsSpec, TestSpec}
import org.mockito.Mockito.{never, spy, times, verify}
import org.scalatestplus.mockito.MockitoSugar
import org.specs2.main.{Arguments, Report}
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification
import specs2.run

class TestContainerForAllSpec extends Specification with MockitoSugar {

  "TestContainerForAll" should {
    "call all appropriate methods of the container" in {
      val container = mock[SampleJavaContainer]

      val spec = new TestSpec(true must beTrue, SampleContainer.Def(container))
      runSilently(spec)

      verify(container).start()
      verify(container).stop()
      ok
    }

    "call all appropriate methods of the container if assertion fails" in {
      val container = mock[SampleJavaContainer]

      val spec = new TestSpec(false must beTrue, SampleContainer.Def(container))
      runSilently(spec)

      verify(container).start()
      verify(container).stop()
      ok
    }

    "start and stop container only once" in {
      val container = mock[SampleJavaContainer]

      val spec = new MultipleTestsSpec(true must beTrue, SampleContainer.Def(container))
      runSilently(spec)

      verify(container, times(1)).start()
      verify(container, times(1)).stop()
      ok
    }

    "call afterContainersStart() and beforeContainersStop()" in {
      val container = mock[SampleJavaContainer]

      val spec = spy(new MultipleTestsSpec(true must beTrue, SampleContainer.Def(container)))
      runSilently(spec)

      verify(spec).afterContainerStart()
      verify(spec).beforeContainerStop()
      ok
    }

    "call beforeContainersStop() and stop container if error thrown in afterContainersStart()" in {
      val container = mock[SampleJavaContainer]

      @volatile var beforeContainersStopCalled = false

      val spec = new MultipleTestsSpec(true must beTrue, SampleContainer.Def(container)) {
        override def afterContainerStart(): Unit =
          throw new RuntimeException("something wrong in afterContainersStart()")

        override def beforeContainerStop(): Unit =
          beforeContainersStopCalled = true
      }

      runSilently(spec)

      verify(container).start()
      verify(container).stop()
      beforeContainersStopCalled must beTrue
    }

    "not start container if all tests are pending" in pending {
      val container = mock[SampleJavaContainer]

      val spec = new MultipleTestsSpec(true must beTrue, SampleContainer.Def(container))
      runSilently(spec)

      verify(container, never()).start()
      ok
    }

    "not start container for empty suite" in pending {
      val container = mock[SampleJavaContainer]

      val spec = new EmptySpec(SampleContainer.Def(container))
      runSilently(spec)

      verify(container, never()).start()
      ok
    }
  }

  def runSilently(spec: Specification): Unit =
    run(spec)(arguments = Arguments(report = Report(_showOnly = Some(""))))
}

object TestContainerForAllSpec {

  trait ContainerSpec extends Specification with TestContainerForAll

  protected class TestSpec(result: => MatchResult[Any], cont: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = cont

    "test" in {
      result
    }
  }

  protected class MultipleTestsSpec(result: => MatchResult[Any], cont: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = cont

    "test 1" in {
      result
    }

    "test 2" in {
      result
    }
  }

  protected class TestSpecWithPending(result: => MatchResult[Any], cont: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = cont

    "test" in pending {
      result
    }
  }

  protected class EmptySpec(cont: ContainerDef) extends ContainerSpec {
    override val containerDef: ContainerDef = cont
  }

}
