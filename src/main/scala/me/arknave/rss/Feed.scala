package me.arknave.rss

import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Future}

import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http._

import scala.util.Try
import scala.xml.{Elem, XML}

class Feed(url: String) {
  val response: Future[ChannelBuffer] = Http.fetchUrl(url).map(_.getContent)

  def xml(): Future[Elem] = {
    def channelBufferToString(channelBuffer: ChannelBuffer): String = {
      val length = channelBuffer.readableBytes
      val bytes = new Array[Byte](length)
      channelBuffer.getBytes(channelBuffer.readerIndex(), bytes, 0, length)
      new String(bytes)
    }

    response.map(XML.loadString _ compose channelBufferToString _)
  }

  def html(): Future[String] = {
    def toHTML(root: Elem): String = {
      val title = (root \ "channel" \ "title").text
      val link = (root \ "channel" \ "link").text

      val header = "<h1><a href=\"" + link + "\">" + title + "</a></h1>"
      val items = (root \ "channel" \ "item").map { item =>
        val itemTitle = (item \ "title").text
        val itemLink = (item \ "link").text
        val description = (item \ "description").text

        "<h2><a href=\"" + itemLink + "\">" + itemTitle + "</a></h2>\n" + "<p>" + description + "</p>"
      }

      header + "\n" + items.mkString("\n")
    }

    xml.map(toHTML)
  }
}
