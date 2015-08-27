package hoang.server;

import hoang.db.DBConnectionException;
import hoang.db.DatabaseConnectionWrapper;
import hoang.db.SQLDatabaseConnection;

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
	@Path("/albums/{albid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAlbum(@PathParam("albid") String _albumID)
	{		
		SQLDatabaseConnection conn;
		
		String json = null;
		
		try
        {
	        conn = DatabaseConnectionWrapper.getInstance().getSQLDatabaseConnection();
	        
	        if(_albumID == null)
			{
				json = conn.getAlbums().toString();
			}
			else
			{
				System.out.println(_albumID);
				json = conn.getAlbum(Integer.valueOf(_albumID)).toString();
			}
        } 
		catch (DBConnectionException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
		return Response.ok(json).type("application/json").build();
	}
	
	@GET
	@Path("/albums")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAlbum()
	{
		SQLDatabaseConnection conn = null;
		
		String json = null;
        try
        {
	        conn = DatabaseConnectionWrapper.getInstance().getSQLDatabaseConnection();
	        
	        json = conn.getAlbums().toString();
        }
        catch (DBConnectionException e)
        {
	        return Response.serverError().build();
        }
		
		return Response.ok(json).type("application/json").build();
	}
	
	@GET
	@Path("/artists")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllArtist()
	{
		SQLDatabaseConnection conn = null;
		
		String json = null;
        try
        {
	        conn = DatabaseConnectionWrapper.getInstance().getSQLDatabaseConnection();
	        
	        json = conn.getArtists().toString();
        }
        catch (DBConnectionException e)
        {
	        return Response.serverError().build();
        }
		
		return Response.ok(json).type("application/json").build();
	}
	
	@GET
	@Path("/artists/{artistID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArtist(@PathParam("artistID") String _artistID)
	{
		SQLDatabaseConnection conn = null;
		
		String json = null;
        try
        {
	        conn = DatabaseConnectionWrapper.getInstance().getSQLDatabaseConnection();
	        
	        json = conn.getArtist(Integer.valueOf(_artistID)).toString();
        }
        catch (DBConnectionException e)
        {
	        return Response.serverError().build();
        }
		
		return Response.ok(json).type("application/json").build();
	}

}
