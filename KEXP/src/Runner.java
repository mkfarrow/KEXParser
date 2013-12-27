import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Runner {
    public static final String BASE_URL = "http://kexp.org/playlist/";
    public static final String VISITED_LOG = "visitedPages";
    public static final String PLAY_LOG = "playLog";
    public static final String ERROR_LOG = "errorLog";
    public static final int START_YEAR = 2001; // first year that KEXP has data for

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> urls = getUrls();
        Set<String> visitedUrls = readVisitedUrls();
        int interval = urls.size() / 4;
        Thread[] threads = {new Thread(new Crawler(0, interval, urls, visitedUrls)),
                            new Thread(new Crawler(interval, interval * 2, urls, visitedUrls)),
                            new Thread(new Crawler(interval * 2, interval * 3, urls, visitedUrls)),
                            new Thread(new Crawler(interval * 3, urls.size(), urls, visitedUrls))};

        for (Thread thread : threads)
            thread.start();
        for (Thread thread : threads)
            thread.join();
    }

    /**
     * The urls that have been visited persist in a text file. Read in those
     * urls and return them as a Set.
     * 
     * @return the Set of URLs for which data has already been recorded
     */
    public static Set<String> readVisitedUrls() {
        Set<String> result = new HashSet<String>();
        Scanner s;
        try {
            s = new Scanner(new File(VISITED_LOG));
        } catch (FileNotFoundException e) {
            return result;
        }
        while (s.hasNextLine()) {
            String line = s.nextLine();
            result.add(line);
        }
        s.close();
        return result;
    }

    /**
     * Returns a list of all the URLs that have playlist data on
     * kexp.org/playlist
     * 
     * @return urls for all playlists as a List
     */
    public static List<String> getUrls() {
        Calendar today = Calendar.getInstance();
        List<String> result = new ArrayList<String>();
        for (int year = START_YEAR; year <= today.get(Calendar.YEAR); year++)
            for (int month = 1; month <= 12; month++)
                for (int day = 1; day <= daysInMonth(year, month); day++)
                    for (String amPm : new String[] { "am", "pm" })
                        for (int hour = 1; hour <= 12; hour++)
                            if (checkDate(today, year, month, day)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(BASE_URL).
                                     append(year).append("/").
                                     append(month).append("/").
                                     append(day).append("/").
                                     append(hour).append(amPm);
                                result.add(sb.toString());
                            }
        return result;
    }

    public static boolean checkDate(Calendar today, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return today.compareTo(cal) > 0;
    }

    public static int daysInMonth(int year, int month) {
        if (month == 4 || month == 6 || month == 9 || month == 11)
            return 30;
        else if (month == 2)
            return year % 4 == 0 ? 29 : 28;
        else
            return 31;
    }

    // prevent this class from being instantiated
    private Runner() {
        throw new UnsupportedOperationException();
    }
}