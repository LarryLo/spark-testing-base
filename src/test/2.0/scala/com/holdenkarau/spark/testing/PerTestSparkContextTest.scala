/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.holdenkarau.spark.testing

import java.nio.file.Files

import org.apache.spark._
import org.scalatest.FunSuite

/**
 * Illustrate using per-test sample test. This is the one to use
 * when your tests may be destructive to the Spark context
 * (e.g. stopping it)
 */
class PerTestSparkContextTest extends FunSuite with PerTestSparkContext {
  val tempPath = Files.createTempDirectory(null).toString()

  //tag::samplePerfTest[]
  test("wordcount perf") {
    val listener = new PerfListener()
    sc.addSparkListener(listener)
    doWork(sc)
    println(listener)
    assert(listener.totalExecutorRunTime > 0)
    assert(listener.totalExecutorRunTime < 10000)
    sc.stop()
  }
  //end::samplePerfTest[]

  test("spark context still exists") {
    assert(sc.parallelize(List(1,2)).reduce(_ + _) === 3)
  }

  def doWork(sc: SparkContext): Unit = {
    val data = sc.textFile("README.md")
    val words = data.flatMap(_.split(" "))
    words.map((_, 1)).reduceByKey(_ + _).saveAsTextFile(tempPath + "/magic")
  }
}
