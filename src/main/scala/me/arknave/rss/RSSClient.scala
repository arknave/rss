package me.arknave.rss

import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}

import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.nio.file.{Files,FileSystems,Path}

import org.jboss.netty.handler.codec.http._

import scala.collection.JavaConverters._
import scala.util.Try
import scala.xml.{Elem, XML}

object RSSClient {
  val RSS_VERSION = "2.0"
  val DEFAULT_CONFIG_FILE = "feeds.txt"

  def errorAndQuit(errorMessage: String) = {
    System.err.println("ERROR: " + errorMessage)
    System.exit(1)
  }

  def main(args: Array[String]) = {
    val configFileLocation = if (args.length > 0) args(0) else DEFAULT_CONFIG_FILE
    val configFilePath = FileSystems.getDefault.getPath(configFileLocation)
    if (!Files.exists(configFilePath)) {
      errorAndQuit(s"""Could not find configuration file: $configFilePath.
        Either pass the configuration file as the first command line parameter,
        or create $DEFAULT_CONFIG_FILE in the current directory.""")
    }

    // list of feeds to scrape
    val feeds = Files.readAllLines(configFilePath, Charset.forName("US-ASCII"))
      .asScala
      .map { new Feed(_) }

    val futures = Future.collect(feeds.map(_.html))
    futures onSuccess { htmls =>
      println(htmls.mkString("\n"))
    }

    Await.ready(futures)
    ()
  }
}
