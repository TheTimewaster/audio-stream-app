package hoang.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{

	@Override
    public void onAuthenticationSuccess(HttpServletRequest _request,
            HttpServletResponse _response, Authentication _auth) throws IOException,
            ServletException
    {
		JSONObject successObject = new JSONObject();
		successObject.put("success", true);
		
		_response.setContentType("application/json");
		_response.setStatus(HttpServletResponse.SC_OK);
		_response.getWriter().write(successObject.toString());
    }

}
