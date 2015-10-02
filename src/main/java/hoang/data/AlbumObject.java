package hoang.data;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;


public class AlbumObject
{
	private int	              id;

	private String	          name;

	private String	          artist;

	private Date	          year;

	private byte[]	          coverObj;

	private List<TrackObject>	tracks	= new ArrayList<>();

	public AlbumObject(int _id, String _name, String _artist, Date _year,
	        byte[] _cover)
	{
		this.id = _id;
		name = _name;
		artist = _artist;
		year = _year;
		coverObj = _cover;
	}

	public void setTracks(List<TrackObject> _tracks)
	{
		this.tracks = _tracks;
	}

	public JSONObject convertToJSON()
	{
		JSONObject obj = new JSONObject();
		obj.put("id", this.id);
		obj.put("album", this.name);
		obj.put("artist", this.artist);
		obj.put("year", this.year.toString());
		// decode to base64
		String encodedCoverImg = "data:image/png;base64,"
		        + Base64.encodeBase64String(coverObj);
		obj.put("coverImg", new String(encodedCoverImg));

		if (this.tracks != null)
		{
			JSONArray array = new JSONArray();
			for (TrackObject track : tracks)
			{
				array.put(track.convertToJSON());
			}

			obj.put("tracks", array);
		}

		return obj;
	}
}
