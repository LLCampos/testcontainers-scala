package com.dimafeng.testcontainers.specs2
import com.dimafeng.testcontainers.Container
import org.specs2.specification.BeforeAfterAll

trait TestContainerForAll extends BeforeAfterAll { self =>
  val container: Container

  override def beforeAll(): Unit = {
    container.start()
  }

  override def afterAll(): Unit = {
    container.stop()
  }
}
