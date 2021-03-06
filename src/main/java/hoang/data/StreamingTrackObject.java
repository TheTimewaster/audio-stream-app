package hoang.data;

import java.io.InputStream;

public class StreamingTrackObject
{
	private int length;
	
	private InputStream in;
	
	public StreamingTrackObject(int _length, InputStream _in)
	{
		length = _length;
		in = _in;
	}
	
	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public InputStream getIn()
	{
		return in;
	}

	public void setIn(InputStream in)
	{
		this.in = in;
	}
}
