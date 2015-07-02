package hoang.server;


import hoang.db.DatabaseConnectionWrapper;
import hoang.db.MongoDatabaseConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

@Path("/stream")
public class StreamingProviderServlet
{
	@Context
	HttpServletRequest	     request;

	@Context
	HttpServletResponse	     response;

	@GET
	@Path("/{obj_id}")
	@Produces("audio/mpeg")
	public Response getFileFromDB(@PathParam(value = "obj_id") String _objID)
	{
		//make this method better by responding with a HTTP 206
		
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
					is = conn.getFileFromMongoDB(_objID);
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
