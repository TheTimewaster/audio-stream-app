package hoang.server.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;

public class MediaStreamingOutput implements IStreamingOutput 
{
	private int contentLength;
	
	private InputStream in;
	
	private int start;
	
	public MediaStreamingOutput(int _contentLength, int _start, InputStream _in) 
	{
		contentLength = _contentLength;
		in = _in;
		start = _start;
	}
	
	@Override
	public void write(OutputStream _out) throws IOException,
			WebApplicationException 
	{
		final byte[] buffer = new byte[4096];
		in.skip(start);
		try
		{
			while(contentLength != 0)
			{
				int read = in.read(buffer, 0, buffer.length > contentLength ? contentLength : buffer.length);
				_out.write(buffer, 0, read);
				contentLength -= read;
			}
		}
		finally
		{
			in.close();
		}
	}

}
