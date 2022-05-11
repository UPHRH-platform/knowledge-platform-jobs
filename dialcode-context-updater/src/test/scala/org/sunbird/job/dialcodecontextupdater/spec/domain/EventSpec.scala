package org.sunbird.job.dialcodecontextupdater.spec.domain

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.job.dialcodecontextupdater.domain.Event
import org.sunbird.job.dialcodecontextupdater.task.DialcodeContextUpdaterConfig
import org.sunbird.job.util.JSONUtil

class EventSpec extends FlatSpec with Matchers with MockitoSugar {

  val config: Config = ConfigFactory.load("test.conf").withFallback(ConfigFactory.systemEnvironment())
  val jobConfig: DialcodeContextUpdaterConfig = new DialcodeContextUpdaterConfig(config)

  "isValid" should "return true for a valid event" in {
    val sunbirdEvent = "{\"eid\":\"BE_JOB_REQUEST\",\"ets\":1641391738147,\"mid\":\"LP.1641391738147.eb3b7a96-259f-4b46-b386-d0bec1873a57\",\"actor\":{\"id\":\"Auto Creator\",\"type\":\"System\"},\"context\":{\"pdata\":{\"id\":\"org.sunbird.platform\",\"ver\":\"1.0\",\"env\":\"staging\"},\"channel\":\"01329314824202649627\"},\"object\":{\"id\":\"do_2134462034258575361402\",\"ver\":\"1641384302775\"},\"edata\":{\"action\":\"auto-create\",\"originData\":{},\"iteration\":1,\"metadata\":{\"ownershipType\":[\"createdBy\"],\"unitIdentifiers\":[\"do_2134460300692275201128\"],\"copyright\":\"Test axis,2076\",\"organisationId\":\"da0d83d6-0692-4d94-95ae-7499d5e0a5bd\",\"keywords\":[\"Nadiya\"],\"subject\":[\"Hindi\"],\"targetMediumIds\":[\"ekstep_ncert_k-12_medium_english\"],\"channel\":\"01329314824202649627\",\"language\":[\"English\"],\"source\":\"https://dockstaging.sunbirded.org/api/content/v1/read/do_2134462034258575361402\",\"mimeType\":\"video/mp4\",\"targetGradeLevelIds\":[\"ekstep_ncert_k-12_gradelevel_class2\"],\"objectType\":\"Content\",\"appIcon\":\"https://stagingdock.blob.core.windows.net/sunbird-content-dock/content/do_2134462034258575361402/artifact/rhinocerous.thumb.jpg\",\"primaryCategory\":\"Teacher Resource\",\"appId\":\"staging.dock.portal\",\"contentEncoding\":\"identity\",\"artifactUrl\":\"https://stagingdock.blob.core.windows.net/sunbird-content-dock/content/do_2134462034258575361402/artifact/mp4_219.mp4\",\"contentType\":\"MarkingSchemeRubric\",\"trackable\":{\"enabled\":\"No\",\"autoBatch\":\"No\"},\"identifier\":\"do_2134462034258575361402\",\"audience\":[\"Student\"],\"subjectIds\":[\"ekstep_ncert_k-12_subject_hindi\"],\"visibility\":\"Default\",\"author\":\"classmate5\",\"mediaType\":\"content\",\"osId\":\"org.ekstep.quiz.app\",\"languageCode\":[\"en\"],\"lastPublishedBy\":\"1cf88ea3-083d-4fdf-84be-3628e63ce7f0\",\"version\":2,\"se_subjects\":[\"Hindi\"],\"license\":\"CC BY 4.0\",\"prevState\":\"Review\",\"size\":13992641,\"lastPublishedOn\":\"2022-01-05T12:05:03.920+0000\",\"name\":\"content_262\",\"topic\":[\"मेरे बचपन के दिन\"],\"attributions\":[\"kayal\"],\"targetBoardIds\":[\"ekstep_ncert_k-12_board_cbse\"],\"status\":\"Live\",\"topicsIds\":[\"ekstep_ncert_k-12_topic_8696da1edbd3ce327d2a0822f75bb44c7e4fecf8\"],\"code\":\"837d2b38-dd5b-10ff-f210-c93bd19adcb3\",\"interceptionPoints\":{},\"credentials\":{\"enabled\":\"No\"},\"prevStatus\":\"Draft\",\"description\":\"MP4\",\"posterImage\":\"https://stagingdock.blob.core.windows.net/sunbird-content-dock/content/do_2134462034258575361402/artifact/rhinocerous.jpg\",\"idealScreenSize\":\"normal\",\"createdOn\":\"2022-01-05T12:04:57.124+0000\",\"targetSubjectIds\":[\"ekstep_ncert_k-12_subject_hindi\"],\"processId\":\"be4d7bf6-364c-42e7-bc0d-adea66117458\",\"contentDisposition\":\"inline\",\"lastUpdatedOn\":\"2022-01-05T12:05:05.269+0000\",\"collectionId\":\"do_2134460300682690561125\",\"dialcodeRequired\":\"No\",\"lastStatusChangedOn\":\"2022-01-05T12:05:05.269+0000\",\"creator\":\"cbsestaging26\",\"os\":[\"All\"],\"se_FWIds\":[\"ekstep_ncert_k-12\"],\"targetFWIds\":[\"ekstep_ncert_k-12\"],\"pkgVersion\":1,\"versionKey\":\"1641384302775\",\"idealScreenDensity\":\"hdpi\",\"framework\":\"ekstep_ncert_k-12\",\"lastSubmittedOn\":\"2022-01-05T12:05:02.768+0000\",\"createdBy\":\"530b19ea-dc8d-4cc7-a4b5-0c0214c8113a\",\"se_topics\":[\"मेरे बचपन के दिन\"],\"compatibilityLevel\":1,\"programId\":\"8f514bc0-6de9-11ec-9e9f-9f0c75510617\",\"createdFor\":[\"01329314824202649627\"]},\"repository\":\"https://dockstaging.sunbirded.org/api/content/v1/read/do_2134462034258575361402\",\"collection\":[{\"identifier\":\"do_21344602761084928012178\",\"unitId\":\"do_21344602911420416012179\"}],\"objectType\":\"Content\",\"stage\":\"publish\"}}"
    val event = new Event(JSONUtil.deserialize[java.util.Map[String, Any]](sunbirdEvent),0,1)

    assert(event.isValid(jobConfig))
  }
}