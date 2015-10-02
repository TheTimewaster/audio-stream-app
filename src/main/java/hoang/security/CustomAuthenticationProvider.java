package hoang.security;

import hoang.db.DBConnectionException;
import hoang.db.DatabaseConnectionWrapper;
import hoang.db.SQLDatabaseConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider
{
	@Override
	public Authentication authenticate(Authentication _auth)
	        throws AuthenticationException
	{
		String name = _auth.getPrincipal().toString();
		String pw = _auth.getCredentials().toString();
		
		try
        {
	        SQLDatabaseConnection dbCon = DatabaseConnectionWrapper.getInstance().getSQLDatabaseConnection();
	        byte[] pwBytes = pw.toString().getBytes();
	        String md5Hash = "";
	        
	        try
            {
	        	md5Hash = convertByteArrayToHexString(MessageDigest.getInstance("MD5").digest(pwBytes));
            } catch (NoSuchAlgorithmException e)
            {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
	        
	        if(dbCon.authenticateUser(name, new String(md5Hash)))
        	{
	        	 List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	 	        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
	 	        
	 	        Authentication auth = new UsernamePasswordAuthenticationToken(name, pw, authorities);
	 	        
	 	        return auth;
        	}
	       
        } 
		catch (DBConnectionException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		return null;
	}

	@Override
	public boolean supports(Class<?> _authentication)
	{
		return _authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
	private static String convertByteArrayToHexString(byte[] _arrayBytes) 
	{
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < _arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((_arrayBytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return stringBuffer.toString();
	}

}
