<?php

include ('getid3/getid3.php');
date_default_timezone_set ("UTC");


class Podcast
{
    const PATH = '/mnt/media/inbox/';
    private $podcast = null;
    private $files = array();
    private $server = "http://lenovo-server.local:8082";
    private static $data = array(
        'balaz_hubinak' => array('title' => 'Baláž a Hubinák', 'pic' => 'http://static.etrend.sk/uploads/tx_media/2011/12/12/balaz_a_hubinak_radio_fm.jpg', 'desc' => 'Program s autorskou dvojicou Daniel Baláž a Pavol Hubinák. Zábava, recesia, glosovanie aktuálnych udalostí a atypická hudobná dramaturgia. Pravidelné rubriky a interakcia s poslucháčmi. Čokoľvek, čo nečakáte.'),
            'od_veci' => array('title' => 'Od Veci_FM', 'pic' => 'http://static.hudba.zoznam.sk/media/obrazky/magazin/galeria/58972/od-veci_fm-nova-humoristicka-relacia.jpg', 'desc' => 'Tomáš Hudák a Ludwig Bagin rozoberajú aktuálne témy a nadhadzujú tak tematickú korisť trojici Jurajovi „Šokovi“ Tabačkovi, Stanovi Staškovi a Lukášovi „Puchovi" Puchovskému (známi z 3T). Nalaďte sa každý nepárny štvrtok od 20:00 do 22:00.')
                                      );

    public function setPodcastName($podcast)
    {
        $this->podcast = $podcast;
    }

    public function toString()
    {
        $this->readFiles();

        $xml = $this->generateHeader();
        foreach ($this->files as $fileInfo) {
            $xml .= $this->generateFileInfo($fileInfo);
        }

        $xml .= $this->generateFooter();

        return $xml;
    }

    private function readFiles()
    {
        // 1 == sorting: descending
        foreach (scandir(self::PATH, 1) as $entry) {
            if (is_file(self::PATH . $entry) && substr($entry, 0, 1) != '.' && strpos($entry, $this->podcast) !== false) {
                $this->files[] = $entry;
            }
        }
    }

    private function getField($field)
    {
        return self::$data[$this->podcast][$field];
    }

    private function getDate($date = null)
    {
        // Sat, 25 Mar 2006 11:30:00 -0500

	if (is_null($date)) return date('D, d M Y H:i:s O');
        return date('D, d M Y H:i:s O', $date);
    }

    private function generateHeader()
    {
        $xml ="<?xml version=\"1.0\" encoding=\"utf-8\" ?>

<rss version=\"2.0\"
 xmlns:atom=\"http://www.w3.org/2005/Atom\"
 xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\"
 xmlns:dc=\"http://purl.org/dc/elements/1.1/\"
 xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\">

<channel>
<atom:link href=\"{$this->server}/podcasts/{$this->podcast}.xml\" rel=\"self\" type=\"application/rss+xml\" />
<title>{$this->getField('title')}</title>
<link>http://www.radiofm.sk/</link>
<description>{$this->getField('desc')}</description>
<pubDate>{$this->getDate()}</pubDate>
<lastBuildDate>{$this->getDate()}</lastBuildDate>

<image>
    <url>{$this->getField('pic')}</url>
    <title>Radio_FM</title>
    <link>http://www.radiofm.sk/</link>
</image>

<dc:creator>http://www.radiofm.sk/</dc:creator>
<sy:updatePeriod>hourly</sy:updatePeriod>
<sy:updateFrequency>1</sy:updateFrequency>

<language>sk-SK</language>
<itunes:subtitle>{$this->getField('title')}</itunes:subtitle>
<itunes:category text=\"Music\"></itunes:category>
<itunes:summary></itunes:summary>
<itunes:owner>
   <itunes:name>Radio_FM</itunes:name>
   <itunes:email>podcast@radiofm.sk</itunes:email>
</itunes:owner>
<itunes:explicit>no</itunes:explicit>
<itunes:image href=\"{$this->getField('pic')}\"/>\n\n";

        return $xml;
    }

    private function generateFooter()
    {
        $xml = "\n</channel></rss>\n";

        return $xml;
    }

    private function generateFileInfo($entry)
    {
        $dateString = "20" . substr($entry, 0, 2) . "-" . substr($entry, 2, 2) . "-" . substr($entry, 4, 2);
        $date = strtotime($dateString);
        $week = date('W', $date);

        $length = filesize(self::PATH . $entry);

        $id3 = new getid3;
        $info = $id3->analyze(self::PATH . $entry);

        $seconds = $info['playtime_seconds'];

        $hours = intval($seconds / 3600);
        $seconds -= ($hours * 3600);
        $minutes = intval($seconds / 60);
        $seconds -= ($minutes * 60);
        $duration = sprintf("%02d:%02d:%02d", $hours, $minutes, $seconds); // 00:24:30
        $guid = md5(implode(':', array($entry, $length, $duration)));

        $url = "{$this->server}/podcasts/" . $entry;

        $xml ="\n\n<item>
        <title>{$week}:{$this->getField('title')}</title>
        <link>http://www.radiofm.sk</link>
        <guid>$guid</guid>
        <category>Podcasts</category>
        <description>{$this->getField('desc')}</description>
        <enclosure url=\"$url\"  type=\"audio/mp3\" length=\"$length\" />
        <pubDate>{$this->getDate($date)}</pubDate>
        <itunes:duration>{$duration}</itunes:duration>
        <itunes:explicit>No</itunes:explicit>
        <itunes:author>Radio_FM</itunes:author>
</item>";

        return $xml;
    }

    static public function index()
    {
        echo "<!DOCTYPE html>
<html>
<head>
    <title>Zemiak Podcasts</title>
</head>
<body>
<ul>\n";

        foreach (self::$data as $key => $row) {
            $url = "/podcasts/" . $key;
            echo "    <li><a href=\"{$url}\">{$row['title']}</a>\n";
        }

        echo "\n</ul>\n</body>\n</html>\n";
    }
}

function main()
{
    global $argv;

    $podcastName = $argv[1];

    header('Content-Type: application/rss+xml; charset=UTF-8');

    $podcast = new Podcast();
    $podcast->setPodcastName($podcastName);
    echo $podcast->toString();
}

main();
