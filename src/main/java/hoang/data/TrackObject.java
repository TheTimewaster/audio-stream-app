package hoang.data;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;


public class TrackObject
{
	private static final SimpleDateFormat	SDF	= new SimpleDateFormat("mm:ss");

	private int	                          id;

	private String	                      name;

	private int	                          trackNo;

	private int	                          length;

	public TrackObject(int id, String _name, int _trackNo, int _length)
	{
		this.id = id;
		this.name = _name;
		this.trackNo = _trackNo;
		this.length = _length;
	}

	public JSONObject convertToJSON()
	{
		
		JSONObject trackObj = new JSONObject();
		trackObj.put("trackID", this.id);
		trackObj.put("track", this.trackNo);
		trackObj.put("name", this.name);
		trackObj.put("length",
		        SDF.format(new Date(this.length)));
		
		return trackObj;
	}
}
