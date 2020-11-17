package org.sunbird.job.functions

import java.lang.reflect.Type
import java.security.MessageDigest
import java.util

import com.google.gson.reflect.TypeToken
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.job.cache.RedisConnect
import org.sunbird.job.dedup.DeDupEngine
import org.sunbird.job.{BaseProcessFunction, Metrics}
import org.sunbird.job.task.ActivityAggregateUpdaterConfig

import scala.collection.JavaConverters._

class ContentConsumptionDeDupFunction(config: ActivityAggregateUpdaterConfig)(implicit val stringTypeInfo: TypeInformation[String]) extends BaseProcessFunction[util.Map[String, AnyRef], String](config) {

  val mapType: Type = new TypeToken[Map[String, AnyRef]]() {}.getType
  private[this] val logger = LoggerFactory.getLogger(classOf[ContentConsumptionDeDupFunction])
  private var deDupEngine: DeDupEngine = _

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    deDupEngine = new DeDupEngine(config, new RedisConnect(config, Option(config.deDupRedisHost), Option(config.deDupRedisPort)), config.deDupStore, config.deDupExpirySec)
    deDupEngine.init()
  }

  override def close(): Unit = {
    deDupEngine.close()
    super.close()
  }


  override def processElement(event: util.Map[String, AnyRef], context: ProcessFunction[util.Map[String, AnyRef], String]#Context, metrics: Metrics): Unit = {
    val eData = event.get(config.eData).asInstanceOf[util.Map[String, AnyRef]].asScala
    val isBatchEnrollmentEvent: Boolean = StringUtils.equalsIgnoreCase(eData.getOrElse(config.action, "").asInstanceOf[String], config.batchEnrolmentUpdateCode)
    if (isBatchEnrollmentEvent) {
      val contents = eData.getOrElse(config.contents, new util.ArrayList[java.util.Map[String, AnyRef]]()).asInstanceOf[util.List[java.util.Map[String, AnyRef]]].asScala
      val filteredContents = contents.filter(x => x.get("status") == 2).toList
      if (filteredContents.size == 0)
        metrics.incCounter(config.skipEventsCount)
      else
        metrics.incCounter(config.batchEnrolmentUpdateEventCount)
      filteredContents.map(c => {
        (eData + ("contents" -> List(Map("contentId" -> c.get("contentId"), "status" -> c.get("status"))))).toMap
      }).filter(e => discardDuplicates(e)).foreach(d => context.output(config.uniqueConsumptionOutput, d))
    } else metrics.incCounter(config.skipEventsCount)
  }

  override def metricsList(): List[String] = {
    List(config.skipEventsCount, config.batchEnrolmentUpdateEventCount)
  }

  def discardDuplicates(event: Map[String, AnyRef]): Boolean = {
    val userId = event.getOrElse(config.userId, "").asInstanceOf[String]
    val courseId = event.getOrElse(config.courseId, "").asInstanceOf[String]
    val batchId = event.getOrElse(config.batchId, "").asInstanceOf[String]
    val contents = event.getOrElse(config.contents, List[Map[String,AnyRef]]()).asInstanceOf[List[Map[String, AnyRef]]]
    if (contents.nonEmpty) {
      val content = contents.head
      val contentId = content.getOrElse("contentId", "").asInstanceOf[String]
      val status = content.getOrElse("status", 0.asInstanceOf[AnyRef]).asInstanceOf[Number].intValue()
      val checksum = getMessageId(courseId, batchId, userId, contentId, status)
      val isUnique = deDupEngine.isUniqueEvent(checksum)
      if (isUnique) deDupEngine.storeChecksum(checksum)
      isUnique
    } else false
  }

  def getMessageId(collectionId: String, batchId: String, userId: String, contentId: String, status: Int): String = {
    val key = Array(collectionId, batchId, userId, contentId, status).mkString("|")
    MessageDigest.getInstance("MD5").digest(key.getBytes).map("%02X".format(_)).mkString;
  }

}
