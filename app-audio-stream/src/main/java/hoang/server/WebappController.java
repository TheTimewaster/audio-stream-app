package hoang.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class WebappController 
{
	private static final String PATH_TO_FILE = "F://User/Eigene Musik/Muse/Black Holes and Revelations/MP3/03 - Supermassive Black Hole.mp3";
	
	private static final int FILE_BUFFER = 2048;
	
	@RequestMapping(value = "/stream_now", method = RequestMethod.GET)
	public void getFile(HttpServletResponse response)
	{
		byte[] bytes = new byte[FILE_BUFFER];
		int bytesRead;
		File file = new File(PATH_TO_FILE);
		
		System.out.println(file.length());
		
		ServletOutputStream out = null;
		FileInputStream fInput = null;
		
		response.setContentType("audio/mpeg");
		try 
		{
			out = response.getOutputStream();
			
			fInput = new FileInputStream(file);
			
			while((bytesRead = fInput.read(bytes)) != -1)
			{
				out.write(bytes, 0, bytesRead);
			}
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				fInput.close();
			} 
			catch (IOException | NullPointerException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try 
			{
				out.close();
			} 
			catch (IOException | NullPointerException e)
			{
				// TODO: handle exception
			}
		}
	}
}

