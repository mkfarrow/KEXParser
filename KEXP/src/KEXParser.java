import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class KEXParser {
	
	public static final String NULL_VALUE = "<<<null>>>";
	public static final String ERROR_CLASS = "KEXParserError";
	
	private PrintStream errors;
	
	public KEXParser(PrintStream errors) { this.errors = errors; }
	
	public List<Play> parsePlays(String url) {
		Connection con = Jsoup.connect(url);
		Document doc = null;
		try {
			doc = con.get();
		} catch (Exception e) {
			errors.println(url);
			return new ArrayList<Play>();
		}
		
		if (doc == null || fIs404Page(url, doc))
			return new ArrayList<Play>();
		
		List<Play> plays = new ArrayList<Play>();
		
		Element playlistItems = doc.getElementById("PlaylistItems");
		if (playlistItems == null || playlistItems.childNodes().size() == 0)
			return new ArrayList<Play>();
		
		Element showName = doc.getElementsByClass("ShowName").first();
		Element tagline = doc.getElementsByClass("ShowTagline").first();	
		Element hostNames = doc.getElementsByClass("HostNames").first();
		Element showHost = null;
		if (hostNames != null)
			showHost = hostNames.getElementsByClass("ShowHost").first();
		
		showHost = checkNull(showHost);
		tagline  = checkNull(tagline);
		showName = checkNull(showName);
		
		for (Element item : playlistItems.children()) {
			if (item.hasClass("AirBreak"))
				continue;

			Element artist = item.getElementsByClass("ArtistName").first();
			artist = checkNull(artist);
			
			Element track = item.getElementsByClass("TrackName").first();
			track = checkNull(track);
			
			Element airDate = item.getElementsByClass("AirDate").first();
			airDate = checkNull(airDate);
			
			Element releaseName = null;
			Element releaseDate = null;
			Element label       = null;
			
			Element releaseMetadata = item.getElementsByClass("ReleaseMetadata").first();
			releaseMetadata = checkNull(releaseMetadata);
			
			for (Element releaseChild : releaseMetadata.children()) {
				if (releaseChild.hasClass("ReleaseName")) {
					releaseName = releaseChild.getElementsByClass("ReleaseName").first();
				} else if (releaseChild.hasClass("ReleaseAndLabel")) {
					Element releaseAndLabel = releaseChild.getElementsByClass("ReleaseAndLabel").first();
					releaseDate = releaseAndLabel.getElementsByClass("ReleaseEventDate").first();
					label = releaseAndLabel.getElementsByClass("LabelName").first();
				}
			}
			releaseDate = checkNull(releaseDate);
			label       = checkNull(label);
			releaseName = checkNull(releaseName);
			
			Play play = new Play();
			play.setArtist(artist.text()).
			     setTrack(track.text()).
				 setAlbum(releaseName.text()).
				 setDate(releaseDate.text()).
				 setLabel(label.text()).
				 setShowName(showName.text()).
				 setTagline(tagline.text()).
				 setDj(showHost.text()).
				 setDateTime(parseDateTime(url, airDate));
			plays.add(play);
		}
		return plays;
	}
	
	/**
	 * Turns the URL and the airDate (which is a time of day like 3:47 PM or something) into
	 * a SQL DATETIME String.
	 * 
	 * @param url the url (which has date information e.g. kexp.org/playlist/2012/04/12/4am)
	 * @param airDate the html element containing the time of the play
	 * @return a String representing a SQL DATETIME
	 */
	private String parseDateTime(String url, Element airDate) {
		// year, month, date, hour(am|pm)
		String[] dateParts = url.substring(Runner.BASE_URL.length()).split("/");
		String year  = dateParts[0];
		String month = "00".substring(dateParts[1].length()) + dateParts[1];
		String day   = "00".substring(dateParts[2].length()) + dateParts[2];
		
		String hour = dateParts[3].substring(0, dateParts[3].length() - 2);
		int hourNum = Integer.parseInt(hour);
		String amPm = dateParts[3].substring(dateParts[3].length() - 2);
		if (hourNum == 12 && amPm.equalsIgnoreCase("am"))
			hourNum -= 12;
		else if (hourNum >= 1 && hourNum <= 11 && amPm.equalsIgnoreCase("pm"))
			hourNum += 12;
	
		hour = hourNum + "";
		hour = "00".substring(hour.length()) + hour;
		String time = hour + ":" + "00:00";
		
		if (!airDate.hasClass(ERROR_CLASS)) {
			String airDateText = airDate.text();
			int iColon = airDateText.indexOf(":");
			String mins = airDateText.substring(iColon + 1, iColon + 3);
			time = hour + ":" + mins + ":00";
		}
		
		String result = year + "-" + month + "-" + day + " " + time;
		return result;
	}
	
	private Element checkNull(Element e) {
		if (e == null || e.text().length() == 0) {
			Element result = new Element(Tag.valueOf("p"), "");
			result.addClass(ERROR_CLASS);
			result.text(NULL_VALUE);
			return result;
		} else {
			return e;
		}
	}
	
	private boolean fIs404Page(String url, Document doc) {
		Elements errors = doc.getElementsByClass("ErrorTitle");
		return !errors.isEmpty();
	}
}