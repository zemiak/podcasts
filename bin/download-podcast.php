<?php

class DownloadPodcast
{
    const BASE_DESTINATION = '/mnt/media/inbox/';
    protected $podcast;
    protected $destination;
    protected $url;
    protected $date;
    protected $genre = 'Other';
    protected $local = false;

    /**
     * Finds out the last Monday, Tuesday, ... date
     *
     * @param int $dow Day of the week; 0 == Sunday
     * @param $fromLastWeek If this script runs one day after the podcast was created
     * @return date
     */
    public function findLastDow($dow, $fromLastWeek = true)
    {
        $current_date = new DateTime(date('Y-m-d'));

        $found = false;
        while (! $found) {
            if ($fromLastWeek) {
                $current_date = $current_date->sub(date_interval_create_from_date_string("1 day"));
            } else {
                $fromLastWeek = true;
            }
            
            $current_dow = (int) $current_date->format('w');

            $found = ($current_dow == $dow);
        }

        return $current_date;
    }

    /**
     * The file name of the provided podcast (podcast name with a timestamp)
     */
    protected function getDestination()
    {
        return self::BASE_DESTINATION . $this->date->format('Y-m-d') . "_{$this->podcast}.mp3";
    }

    /**
     * Download (optional) and tag the podcast
     */
    public function download()
    {
        $ret = 0;
        $destination = $this->getDestination();

        if (! $this->local) {
            $command = "wget -q '{$this->url}' -O '{$destination}' 2>&1";
            $text = system($command, $ret);
        } else {
            // the file is already here (Radio_FM stream recording)
            $ret = 0;
        }

        if ($ret !== 0) {
            echo "{$this->podcast} Download error {$ret}:\n";
            echo "$command\n$text\n";
        } else {
            $week = $this->date->format('W');
            $title = "{$this->podcast} #{$week}";
            $year = $this->date->format('Y');
            $comment = "{$this->podcast} " . $this->date->format('Y-m-d');

            $command = "id3 -t \"{$title}\" -y {$year} -A Radio_FM -a Radio_FM -T {$week} -g \"{$this->genre}\" -c \"{$comment}\" \"{$destination}\" 2>&1";
            $text = system($command, $ret);

            if ($ret !== 0) {
                echo "{$this->podcast} Tag error {$ret}:\n";
                echo "$command\n$text\n";
            }
        }
    }

    /**
     * Support for Signall_FM
     */
    protected $niekdePodcasts = array('signall' => true);
    protected function niekde()
    {
        $this->date = $this->findLastDow(0); // Sunday
        $this->url = 'http://signallfm.niekde.sk/streams/[signall_FM] '
            . $this->date->format('Y-m-d')
            . " 215501.mp3";
        $this->genre = 'Drum & Bass';
    }

    /**
     * Support for Kyberia
     */
    protected $kyberiaPodcasts = array(
        'nuSpirit' => array(0, '2000', 'Jazz'),            // Sunday 20:00
        'wilsonic' => array(6, '2200', 'Electronic'),      // Saturday 22:00
        'exclusive' => array(6, '2000', 'Other'),          // Saturday 20:00
        '4elements' => array(5, '2100', 'Techno'),         // Friday 21:00
        'balaz_hubinak' => array(5, '1800', 'Speech'),     // Friday 18:00
        'selector' => array(4, '2000', 'BritPop'),         // Thursday 20:00
        'experimental' => array(2, '2200', 'Alternative'), // Tuesday 22:00
        'pohoda' => array(2, '2000', 'Alternative'),       // Tuesday 20:00
        'hudba_sveta' => array(1, '2000', 'Ethnic')        // Monday 20:00
        );
    protected function kyberia()
    {
        $day = $this->kyberiaPodcasts[$this->podcast][0];
        $time = $this->kyberiaPodcasts[$this->podcast][1];
        if (isset($this->kyberiaPodcasts[$this->podcast][2])) {
            $this->genre = $this->kyberiaPodcasts[$this->podcast][2];
        }

        $this->date = $this->findLastDow($day);
        $this->url = "http://radio-fm.kyberia.net/mp3/fm_{$this->podcast}_"
            . $this->date->format('dmY') . "{$time}.mp3";
    }

    /**
     * Support for direct recording from the Radio_FM stream
     */
    protected $recordPodcasts = array(
        'headbanger' => array(1, '2h', 'Metal'),  // Monday, duration 2 hours
        'test-record' => array(1, '1s', 'Metal')
        );
    protected function record()
    {
        $this->local = true;
        
        $data = $this->recordPodcasts[$this->podcast];
        $this->date = $this->findLastDow($data[0], false); // false == the same day recording
        $destination = $this->getDestination();

        $this->genre = $data[2];
        
        $time = $data[1];
        $letter = substr($time, strlen($time) - 1);
        $seconds = substr($time, 0, strlen($time) - 1);

        if ($letter == 'm') {
            $seconds = $seconds * 60;
        } elseif ($letter == 'h') {
            $seconds = $seconds * 3600;
        } elseif ($letter != 's') {
            throw new Exception('Unknown time modifier: ' . $letter);
        }

        $seconds += 600; // plus 10 minutes

        $command = "./record.bash '{$destination}' '{$seconds}'";
        $text = system($command, $ret);

        if ($ret !== 0) {
            echo "{$this->podcast} Record error {$ret}:\n";
            echo "$command\n$text\n";
        }        
    }

    function __construct($podcast)
    {
        $this->podcast = $podcast;

        if (isset($this->niekdePodcasts[$podcast])) {
            $this->niekde();
        } elseif (isset($this->kyberiaPodcasts[$podcast])) {
            $this->kyberia();
        } elseif (isset($this->recordPodcasts[$podcast])) {
	        $this->record();
	    } else {
            echo "Unknown podcast {$this->podcast}\n";
            die();
        }
    }
}

function main()
{
    global $argv;

    if (count($argv) != 2) {
        echo "{$argv[0]} <podcast-name>\n";
        return 1;
    }

    $app = new DownloadPodcast($argv[1]);
    $app->download();
}

main();
