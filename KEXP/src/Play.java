public class Play {
	private String artist;
	private String track;
	private String album;
	private String year;
	private String label;
	private String showName;
	private String tagline;
	private String dj;
	private String dateTime; // String representation of SQL DateTime
	
	public String getArtist()      { return artist;   }
	public String getTrack()       { return track;    }
	public String getAlbum()       { return album;    }
	public String getYear()        { return year;     }
	public String getLabel()       { return label;    }
	public String getShowName()    { return showName; }
	public String getShowTagline() { return tagline;  }
	public String getDj()          { return dj;       }
	public String getDateTime()    { return dateTime; }

	public Play setArtist(String artist) {
		this.artist = artist;
		return this;
	}

	public Play setTrack(String track) {
		this.track = track;
		return this;
	}

	public Play setAlbum(String album) {
		this.album = album;
		return this;
	}

	public Play setDate(String year) {
		this.year = year;
		return this;
	}

	public Play setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public Play setShowName(String showName) {
		this.showName = showName;
		return this;
	}
	
	public Play setTagline(String tagline) {
		this.tagline = tagline;
		return this;
	}
	
	public Play setDj(String dj) {
		this.dj = dj;
		return this;
	}
	
	public Play setDateTime(String dateTime) {
		this.dateTime = dateTime;
		return this;
	}
	
	/**
	 * Stupid printable version of the Play for debugging only.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(artist).append(" - ").
		   append(track).append(" - ").
		   append(album).append("\n").
		   append(year).append(", ").
		   append(label).append("\n").
		   append(showName).append("\n").
		   append(tagline).append("\n").
		   append(dj).append("\n").
		   append("played at: " + dateTime).append("\n");
		return sb.toString();
	}
	
	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n").
			   append("  \"artist\": ").  append(jsonify(artist)).  append(",\n").
			   append("  \"track\": ").   append(jsonify(track)).   append(",\n").
			   append("  \"album\": ").   append(jsonify(album)).   append(",\n").
			   append("  \"year\": ").    append(jsonify(year)).    append(",\n").
			   append("  \"label\": ").   append(jsonify(label)).   append(",\n").
			   append("  \"showName\": ").append(jsonify(showName)).append(",\n").
			   append("  \"tagline\": "). append(jsonify(tagline)). append(",\n").
			   append("  \"dj\": ").      append(jsonify(dj)).      append(",\n").
			   append("  \"playTime\": ").append(jsonify(dateTime)).append("\n").
		   append("}");
		return sb.toString();
	}
	
	/**
	 * Format that can be imported by MySQL or SQLite.
	 * 
	 * @return a tab separated version of the data
	 */
	public String toTabSeparated() {
		StringBuilder sb = new StringBuilder();
		sb.append(tabify(artist)).  append("\t").
		   append(tabify(track)).   append("\t").
		   append(tabify(album)).   append("\t").
           append(tabify(year)).    append("\t").
	       append(tabify(label)).   append("\t").
	       append(tabify(showName)).append("\t").
   	       append(tabify(tagline)). append("\t").
		   append(tabify(dj)).      append("\t").
		   append(tabify(dateTime)).append("\t");
		return sb.toString();
	}
	
	/**
	 * Turns nulls into appropriate character for MySQL to import
	 * 
	 * @param s the string to convert
	 * @return the MySQL null-string if null, s otherwise
	 */
	public static String tabify(String s) {
		return s == null || s.equals("null") ? "\\N" : s;
	}
	
	/**
	 * Escapes quotes and turns NULL_VALUE into the string "null" to make it a
	 * real JSON string
	 * 
	 * @param s the string to escape - this is the VALUE of a field in a JSON object,
	 * NOT a JSON object itself
	 * @return a String that can be used in a JSON object
	 */
	private static String jsonify(String s) {
		s = s.replace("\"", "\\\"");
		return s.equals(KEXParser.NULL_VALUE) ? "null" : "\"" + s + "\"";
	}
}