import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

public class Crawler implements Runnable {

    // determines whether the output is written as a series of JSON objects
    // or as a tab-separated line
    private static final boolean AS_JSON = false;

    private int iStart;
    private int iEnd;
    private List<String> urls;
    private Set<String> visitedUrls;

    public Crawler(int iStart, int iEnd, List<String> urls, Set<String> visitedUrls) {
        this.iStart = iStart;
        this.iEnd = iEnd;
        this.urls = urls;
        this.visitedUrls = visitedUrls;
    }

    @Override
    public void run() {
        PrintStream errorLog;
        PrintStream playLog;
        PrintStream visitedLog;
        try {
            String postFix = iStart + "_" + iEnd + ".txt";
            errorLog = new PrintStream(new FileOutputStream(Runner.ERROR_LOG + postFix, true));
            playLog = new PrintStream(new FileOutputStream(Runner.PLAY_LOG + postFix, true));
            visitedLog = new PrintStream(new FileOutputStream(Runner.VISITED_LOG + postFix, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        KEXParser kexp = new KEXParser(errorLog);
        for (int i = iStart; i < iEnd; i++) {
            if (visitedUrls.contains(urls.get(i)))
                continue;
            StringBuilder sb = new StringBuilder();
            String playPage = sb.append(urls.get(i)).toString();
            List<Play> currPlays = kexp.parsePlays(playPage);
            printPlays(playLog, currPlays);
            visitedLog.println(playPage);
        }
    }

    private static void printPlays(PrintStream log, List<Play> plays) {
        for (Play play : plays) {
            log.println(AS_JSON ? play.toJSON() : play.toTabSeparated());
            log.println();
        }
    }
}