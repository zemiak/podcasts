<%@page contentType="application/rss+xml" pageEncoding="UTF-8"%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><?xml version="1.0" encoding="utf-8" ?>
<rss version="2.0"
    xmlns:atom="http://www.w3.org/2005/Atom"
    xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:sy="http://purl.org/rss/1.0/modules/syndication/">
    <jsp:useBean id="podcasts" scope="request" class="com.zemiak.podcasts.service.jsp.PodcastJSPService" /><% podcasts.setPodcastName(request); %>
    <channel>
        <atom:link href="/podcasts/feed.jsp?name=${podcasts.podcast.name}" rel="self" type="application/rss+xml" />
        <title>${podcasts.podcast.title}</title>
        <link>http://www.radiofm.sk/</link>
        <description>${podcasts.podcast.description}</description>
        <pubDate>${podcasts.now}</pubDate>
        <lastBuildDate>${podcasts.now}</lastBuildDate>

        <image>
            <url>${podcasts.podcast.picture}</url>
            <title>${podcasts.podcast.title}</title>
            <link>http://www.radiofm.sk/</link>
        </image>

        <dc:creator>http://www.radiofm.sk/</dc:creator>
        <sy:updatePeriod>hourly</sy:updatePeriod>
        <sy:updateFrequency>1</sy:updateFrequency>

        <language>sk-SK</language>
        <itunes:subtitle>${podcasts.podcast.title}</itunes:subtitle>
        <itunes:category text="Music"></itunes:category>
        <itunes:summary></itunes:summary>
        <itunes:owner>
           <itunes:name>Radio_FM</itunes:name>
           <itunes:email>podcast@radiofm.sk</itunes:email>
        </itunes:owner>
        <itunes:explicit>no</itunes:explicit>
        <itunes:image href="${podcasts.podcast.picture}"/>;

        <c:forEach var="item" items="${podcasts.podcast.episodes}">
            <item>
                <title>${item.createdWeek} ${podcasts.podcast.title}</title>
                <link>http://www.radiofm.sk</link>
                <guid>${item.guid}</guid>
                <category>Podcasts</category>
                <description>${podcasts.podcast.description}</description>
                <enclosure url="/podcasts/stream/${item.baseFileNameWithoutExtension}"  type="audio/mp3" length="${item.fileSize}" />
                <pubDate>${item.createdDate}</pubDate>
                <itunes:duration>${item.durationSeconds}</itunes:duration>
                <itunes:explicit>No</itunes:explicit>
                <itunes:author>Radio_FM</itunes:author>
            </item>
        </c:forEach>
    </channel>
</rss>
