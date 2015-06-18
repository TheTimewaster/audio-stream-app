package hoang.server;


import hoang.db.DatabaseConnectionWrapper;
import hoang.db.MongoDatabaseConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

@Path("/stream")
public class StreamingProviderServlet
{
	private static final int	FILE_BUFFER	= 2048;

	@Context
	HttpServletRequest	     request;

	@Context
	HttpServletResponse	     response;

	@GET
	@Path("/stream_now")
	@Produces("audio/mpeg")
	public Response getFileFromDB()
	{
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements())
		{
			String element = headers.nextElement();
			System.out.println(element + ":\t"
			        + request.getHeader(element).toString());
		}

		// make better by implementing a config wrapper
		MongoDatabaseConnection conn = DatabaseConnectionWrapper.getInstance()
		        .getMongoDatabaseConnection(
		                MongoDatabaseConnection.DEFAULT_MONGO_HOST,
		                MongoDatabaseConnection.DEFAULT_MONGO_PORT,
		                MongoDatabaseConnection.DEFAULT_DATABASE);

		StreamingOutput defaultStreamer = new StreamingOutput()
		{
			InputStream	is	= null;

			@Override
			public void write(OutputStream out) throws IOException,
			        WebApplicationException

			{
				try
				{
					is = conn.getFirstFileFromMongoDB();
					IOUtils.copy(is, out);
				} finally
				{
					is.close();
					out.close();
				}
			}

		};

		return Response.ok(defaultStreamer).type("audio/mpeg").build();
	}

}
