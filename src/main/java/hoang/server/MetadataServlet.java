package hoang.server;

import hoang.db.DatabaseConnectionWrapper;
import hoang.db.MongoDatabaseConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/metadata")
public class MetadataServlet
{
	@Context
	HttpServletRequest request;
	
	@Context
	HttpServletResponse response;
	
	@GET
	@Path("/getDocument")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocument()
	{
		MongoDatabaseConnection conn = DatabaseConnectionWrapper.getInstance().getMongoDatabaseConnection("localhost", 27017, "music");
		
		String json = conn.getAllDocumentsInCollection();
		
		return Response.ok(json).type("application/json").build();
	}
	
	@GET
	@Path("/albums/{albid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbum(@PathParam("albid") String _albumID)
	{
		MongoDatabaseConnection conn = DatabaseConnectionWrapper.getInstance().getMongoDatabaseConnection("localhost", 27017, "music");
		
		String json = null;
		
		if(_albumID == null)
		{
			json = conn.getAllAlbumsFromCollection();
		}
		else
		{
			json = conn.getAlbumFromCollection(_albumID);
		}
		
		return Response.ok(json).type("application/json").build();
	}
	
	@GET
	@Path("/albums")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAlbum()
	{
		MongoDatabaseConnection conn = DatabaseConnectionWrapper.getInstance().getMongoDatabaseConnection("localhost", 27017, "music");
		
		String json = null;
		
		json = conn.getAllAlbumsFromCollection();
		
		return Response.ok(json).type("application/json").build();
	}

}
