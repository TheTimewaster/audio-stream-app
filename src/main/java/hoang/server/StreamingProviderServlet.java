package hoang.server;


import hoang.db.DatabaseConnectionWrapper;
import hoang.db.MongoDatabaseConnection;
import hoang.server.stream.AbstractStreamingOutput;
import hoang.server.stream.DefaultStreamingOutput;
import hoang.server.stream.MediaStreamingOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.interfaces.RSAKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

@Path("/stream")
public class StreamingProviderServlet
{
	private int chunkSize = 1024*1024;
	
	@Context
	HttpServletRequest	     request;

	@Context
	HttpServletResponse	     response;
	
	@GET
	@Path("/{obj_id}")
	@Produces("audio/mpeg")
	public Response getStream(@PathParam(value = "obj_id") String _objID, 
			@HeaderParam(value = "Range") String _range)
	{
		MongoDatabaseConnection conn = DatabaseConnectionWrapper.getInstance()
		        .getMongoDatabaseConnection(
		                MongoDatabaseConnection.DEFAULT_MONGO_HOST,
		                MongoDatabaseConnection.DEFAULT_MONGO_PORT,
		                MongoDatabaseConnection.DEFAULT_DATABASE);
		
		int contentLength = conn.getContentLength(_objID);
		
		AbstractStreamingOutput streamer = null;
		
		if(_range == null)
		{
			streamer = new DefaultStreamingOutput(conn.getFileFromMongoDB(_objID));
			
			return Response.ok(streamer).header(HttpHeaders.CONTENT_LENGTH, contentLength).build();
		}
		else
		{
			//split down range request
			String[] ranges = _range.split("=")[1].split("-");
			final int from = Integer.parseInt(ranges[0]);
			System.out.println(from);
			
			//chunk down media if the range header upper bound is undefined, e.g. Chrome
			int to = chunkSize + from;
			if(to >= contentLength)
			{
				to = (int) (contentLength -1);
			}
			
			if(ranges.length == 2)
			{
				to = Integer.parseInt(ranges[1]);
			}
			
			final String responseRange = String.format("bytes %d-%d/%d", from, to, contentLength);
			
			streamer = new MediaStreamingOutput(contentLength, from, conn.getFileFromMongoDB(_objID));
			
			return Response.ok(streamer)
					.status(206)
					.header("Accept-Ranges", "bytes")
					.header("Content-Range", responseRange)
					.header(HttpHeaders.CONTENT_LENGTH, contentLength)
					.build();
		}
	}

}
