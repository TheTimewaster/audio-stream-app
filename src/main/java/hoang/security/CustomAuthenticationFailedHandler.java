package hoang.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomAuthenticationFailedHandler implements
        AuthenticationFailureHandler
{

	@Override
	public void onAuthenticationFailure(HttpServletRequest _request,
	        HttpServletResponse _response, AuthenticationException _exception)
	        throws IOException, ServletException
	{
		JSONObject successObject = new JSONObject();
		successObject.put("success", false);
		
		_response.setContentType("application/json");
		_response.setStatus(HttpServletResponse.SC_OK);
		_response.getWriter().write(successObject.toString());
	}

}
