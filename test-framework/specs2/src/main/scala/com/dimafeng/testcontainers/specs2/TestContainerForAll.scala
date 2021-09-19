package com.dimafeng.testcontainers.specs2
import com.dimafeng.testcontainers.ContainerDef
import org.specs2.specification.BeforeAfterAll

trait TestContainerForAll extends BeforeAfterAll { self =>
  val containerDef: ContainerDef

  private var startedContainer: Option[containerDef.Container] = None

  override def beforeAll(): Unit = {
    val container = containerDef.start()
    startedContainer = Some(container)
    afterContainerStart()
  }

  override def afterAll(): Unit = {
    beforeContainerStop()
    startedContainer.foreach(_.stop())
  }

  def afterContainerStart(): Unit = {}
  def beforeContainerStop(): Unit = {}
}
