<?php

include ('getid3/getid3.php');

class Podcast
{
    const PATH = '/mnt/media/inbox/';
    private $podcast = null;
    private $files = array();

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

    private static $data = array(
        'signall' => array('title' => 'Signall_FM', 'pic' => 'http://profile.ak.fbcdn.net/hprofile-ak-snc7/373418_157034178699_424661517_n.jpg', 'desc' => 'Drum and bass, hudba založená na zlomenej rytmike bicích a silnej basovej linke. U nás čoraz populárnejší hudobný žáner v dvojhodinovom autorskom programe DJa Lixxa. Exkluzívni hostia, hudobné novinky a promá, domáci DJi a promotéri, ale aj hostia zo zahraničia.')
        , 'headbanger' => array('title' => 'Headbanger_FM', 'pic' => 'http://profile.ak.fbcdn.net/hprofile-ak-prn1/162061_88994698360_581590913_n.jpg', 'desc' => 'Jediná metalová relácia v celoslovenskom éteri. Headbanger_FM je program pre všetkých priaznivcov extrémnych foriem gitarovej hudby. Zaujímaví hostia, horúce novinky i vykopávky, aktuality, pozvánky na koncerty, súťaže o voľné vstupy, profily, rozhovory a veľa iných prekvapení.')
        , 'nuSpirit' => array('title' => 'Nu Spirit_FM', 'pic' => 'http://www.radiofm.sk/modules/mod_news_pro_gk4/cache/articles.rel%C3%A1cie.nuspirit.nuspirit_fm_imidz_modranewspro_rel_nuspirit.jpg', 'desc' => 'The new Spirit of Jazz. Nu jazz, nu soul a nu funk. Nové spojenia a netradičné hudobné fúzie. Bootlegy, remixy. Prepojenie starého sveta a nových hudobných postupov v hudbe s DJmi Kinetom a Magálovou.')
        , 'wilsonic' => array('title' => 'Wilsonic_FM', 'pic' => 'http://a0.twimg.com/profile_images/1133360373/wilsonic_icon_reasonably_small.png', 'desc' => 'Súčasná elektronická hudba do ucha i do skoku. Nové trendy, klubová hudba, i hudba na počúvanie. V čase rovnomenného festivalu Wilsonic predstavuje účinkujúcich v exkluzívnych hudobných setoch. Autorsky pripravujú DJi Tibor Holoda a Daniel Baláž.')
        , 'exclusive' => array('title' => 'Exclusive_FM', 'pic' => 'http://www.radiofm.sk/cache/plg_readlesstext/71a109945f9b9104481579f7d5e87b88.jpg', 'desc' => 'Exkluzívne koncerty zo siete EBU. Každý týždeň vyberá, zostavuje a moderuje Hana Brozová.')
        , '4elements' => array('title' => '4 Elements_FM', 'pic' => 'http://www.radiofm.sk/cache/plg_readlesstext/5202c18fa3d57e00a5522812748fd038.jpg', 'desc' => 'Tanečná elektronika zo sveta, ale aj domáca klubová scéna. House, tech house, progressive a iné tanečné žánre. Djské osobnosti zo sveta, exkluzívne rozhovory, mixované sety a hľadanie dídžejských talentov z celého Slovenska s Michalom Breethom.')
        , 'selector' => array('title' => 'The Selector', 'pic' => 'http://www.britishcouncil.org/greece-arts-330x220-the-selector.jpg', 'desc' => 'Program podporovaný a financovaný britskou radou na Slovensku. Nová britská hudba, kapely a producenti, ktorí ešte nemajú vydavateľa, exkluzívne sety ale aj fíčre a rozhovory. DJ Daniel Baláž.')
        , 'experimental' => array('title' => 'Experimental_FM', 'pic' => 'http://www.radiofm.sk/modules/mod_news_pro_gk4/cache/articles.rel%C3%A1cie.experimental.experimental_fm_imidznewspro_rel_experimental.jpg', 'desc' => 'Experimentálne a novátorské žánre. Wonky, dubstep, glitch, elektronika i vážna hudba nevážne. DJ Daniel Baláž.')
        , 'balaz_hubinak' => array('title' => 'Baláž a Hubinák', 'pic' => 'http://static.etrend.sk/uploads/tx_media/2011/12/12/balaz_a_hubinak_radio_fm.jpg', 'desc' => 'Program s autorskou dvojicou Daniel Baláž a Pavol Hubinák. Zábava, recesia, glosovanie aktuálnych udalostí a atypická hudobná dramaturgia. Pravidelné rubriky a interakcia s poslucháčmi. Čokoľvek, čo nečakáte.')
        , 'pohoda' => array('title' => 'Pohoda_FM', 'pic' => 'http://profile.ak.fbcdn.net/hprofile-ak-prn1/41589_60485392917_3227364_n.jpg', 'desc' => 'Alternatíva s hitovým potenciálom, set alebo liveact domáceho dídžeja. 1x mesačne živý koncert domácej kapely z Komorného štúdia SRo za účasti publika s voľným vstupom. DJ Biba_FM.')
        , 'hudba_sveta' => array('title' => 'Hudba Sveta_FM', 'pic' => 'http://www.radiofm.sk/modules/mod_news_pro_gk4/cache/articles.rel%C3%A1cie.hudba_sveta.BuskersBBCnewspro_rel_hudbasveta.jpg', 'desc' => 'World music a etno s Potkanom. Autorský program niekdajšieho world music redaktora Rádia Ragtime. Pohľad na netradičné hudobné nástroje, rebríček World Music Chart, hudobní hostia a hudba sveta.')
                                  );

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
<atom:link href=\"http://mekbuk-server.local/podcasts/podcast.php?podcast={$this->podcast}\" rel=\"self\" type=\"application/rss+xml\" />
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
        $date = strtotime(substr($entry, 0, 10));
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

        $url = "http://mekbuk-server.local/podcasts/" . $entry;

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

    static public function downloadFile($fileName, $fullDownload)
    {
        $dangerous = array('./', '..', '/', '\\');
        foreach ($dangerous as $substr) {
            if (strpos($fileName, $substr) !== false) return;
        }

        $full = self::PATH . $fileName;

        header("Content-Type: audio/mpeg");
        header("Content-Length: " . filesize($full));
        header('Content-Disposition: attachment; filename="' . $fileName . '"');
        header("Content-Transfer-Encoding: binary\n");

        if (! $fullDownload) return;

        self::rangeDownload($full);
    }

    /**
     * Returns the specified portion of the file. Required by iPhone
     * (not only) streaming client.
     *
     * @param string $file
     *
     */
    static private function rangeDownload($file)
    {
        $fp = @fopen($file, 'rb');

        $size   = filesize($file); // File size
        $length = $size;           // Content length
        $start  = 0;               // Start byte
        $end    = $size - 1;       // End byte

        // Now that we've gotten so far without errors we send the accept range header
        /* At the moment we only support single ranges.
         * Multiple ranges requires some more work to ensure it works correctly
         * and comply with the spesifications: http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html#sec19.2
         *
         * Multirange support annouces itself with:
         * header('Accept-Ranges: bytes');
         *
         * Multirange content must be sent with multipart/byteranges mediatype,
         * (mediatype = mimetype)
         * as well as a boundry header to indicate the various chunks of data.
         */
        header("Accept-Ranges: 0-$length");

        // header('Accept-Ranges: bytes');
        // multipart/byteranges
        // http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html#sec19.2
        if (isset($_SERVER['HTTP_RANGE'])) {

            $c_start = $start;
            $c_end   = $end;

                // Extract the range string
            list(, $range) = explode('=', $_SERVER['HTTP_RANGE'], 2);

                // Make sure the client hasn't sent us a multibyte range
            if (strpos($range, ',') !== false) {

                        // (?) Shoud this be issued here, or should the first
                        // range be used? Or should the header be ignored and
                        // we output the whole content?
                header('HTTP/1.1 416 Requested Range Not Satisfiable');
                header("Content-Range: bytes $start-$end/$size");
                        // (?) Echo some info to the client?
                exit;
            }

                // If the range starts with an '-' we start from the beginning
                // If not, we forward the file pointer
                // And make sure to get the end byte if spesified
            if (substr($range, 0, 1) == '-') {

                        // The n-number of the last bytes is requested
                $c_start = $size - substr($range, 1);
            } else {
                $range  = explode('-', $range);
                $c_start = $range[0];
                $c_end   = (isset($range[1]) && is_numeric($range[1])) ? $range[1] : $size;
            }

            /* Check the range and make sure it's treated according to the specs.
             * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
             */

            // End bytes can not be larger than $end.
            $c_end = ($c_end > $end) ? $end : $c_end;

            // Validate the requested range and return an error if it's not correct.
            if ($c_start > $c_end || $c_start > $size - 1 || $c_end >= $size) {
                header('HTTP/1.1 416 Requested Range Not Satisfiable');
                header("Content-Range: bytes $start-$end/$size");
                    // (?) Echo some info to the client?
                exit;
            }

            $start  = $c_start;
            $end    = $c_end;
            $length = $end - $start + 1; // Calculate new content length
            fseek($fp, $start);
            header('HTTP/1.1 206 Partial Content');
        }

        // Notify the client the byte range we'll be outputting
        header("Content-Range: bytes $start-$end/$size");
        header("Content-Length: $length");

        // Start buffered download
        $buffer = 1024 * 8;
        while(!feof($fp) && ($p = ftell($fp)) <= $end) {
            if ($p + $buffer > $end) {
                    // In case we're only outputtin a chunk, make sure we don't
                    // read past the length
                $buffer = $end - $p + 1;
            }

            set_time_limit(0); // Reset time limit for big files
            echo fread($fp, $buffer);
            flush(); // Free up memory. Otherwise large files will trigger PHP's memory limit.
        }

        fclose($fp);
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
            $url = "http://mekbuk-server.local/podcasts/podcast.php?podcast=" . $key;
            echo "    <li><a href=\"{$url}\">{$row['title']}</a>\n";
        }

        echo "\n</ul>\n</body>\n</html>\n";
    }
}

function main()
{
    global $argv;

    if (isset($_GET['podcast'])) {
        $podcastName = $_GET['podcast'];
    } elseif (isset($argv[1])) {
        $podcastName = $argv[1];
    } else {
        if (isset($_GET['file'])) {
            Podcast::downloadFile($_GET['file'], ($_SERVER['REQUEST_METHOD'] == 'GET'));
            return;
        } else {
            Podcast::index();
            return;
        }
        
    }

    header('Content-Type: application/rss+xml; charset=UTF-8');

    $podcast = new Podcast();
    $podcast->setPodcastName($podcastName);
    echo $podcast->toString();
}

main();
