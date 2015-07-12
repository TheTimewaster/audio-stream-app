package hoang.server.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.io.IOUtils;

public class DefaultStreamingOutput implements AbstractStreamingOutput
{		
	private InputStream in;
	
	public DefaultStreamingOutput(InputStream _in) 
	{
		this.in = _in;
	}

	@Override
	public void write(OutputStream _out) throws IOException,
			WebApplicationException 
	{
		try
		{
			IOUtils.copy(in, _out);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch(IOException | NullPointerException e)
			{
				//do nothing
			}
			
			_out.close();
		}
	}
}