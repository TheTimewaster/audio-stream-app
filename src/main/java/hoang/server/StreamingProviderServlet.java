package hoang.server;


import hoang.data.StreamingTrackObject;
import hoang.db.DBConnectionException;
import hoang.db.DatabaseConnectionWrapper;
import hoang.db.SQLDatabaseConnection;
import hoang.server.stream.IStreamingOutput;
import hoang.server.stream.DefaultStreamingOutput;
import hoang.server.stream.MediaStreamingOutput;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/stream")
public class StreamingProviderServlet
{
	private int chunkSize = 1024*1024;
	
	@Context
	HttpServletRequest	     request;

	@Context
	HttpServletResponse	     response;
	
	@GET
	@Path("/{trackID}")
	@Produces("audio/mpeg")
	public Response getStream(@PathParam(value = "trackID") String _trackID, 
			@HeaderParam(value = "Range") String _range, @HeaderParam(value = "User-Agent") String _userAgent)
	{
		
		int contentLength = 0;
		IStreamingOutput streamer = null;
		StreamingTrackObject track = null;
		SQLDatabaseConnection conn;
		
        try
        {
	        conn = DatabaseConnectionWrapper.getInstance().getSQLDatabaseConnection();
	        track = conn.getTrack(Integer.valueOf(_trackID));
	        contentLength = track.getLength();
	        
	        //if User-Agent contains Gecko -> fallback with no 206 response
	        if(_range == null || _userAgent.contains("Firefox"))
			{
				streamer = new DefaultStreamingOutput(track.getIn());
				
				return Response.ok(streamer).header(HttpHeaders.CONTENT_LENGTH, contentLength).build();
			}
			else
			{
				//split down range request
				String[] ranges = _range.split("=")[1].split("-");
				final int from = Integer.parseInt(ranges[0]);
				
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
				final int len = to - from +1;
				streamer = new MediaStreamingOutput(len, from, track.getIn());
				
				return Response.ok(streamer)
						.status(206)
						.header("Accept-Ranges", "bytes")
						.header("Content-Range", responseRange)
						.header(HttpHeaders.CONTENT_LENGTH, len)
						.build();
			}
        } 
        catch (DBConnectionException e)
        {
        	return Response.serverError().build();
        }
	}

}
