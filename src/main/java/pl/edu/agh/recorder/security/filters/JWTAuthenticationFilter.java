package pl.edu.agh.recorder.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.edu.agh.recorder.entity.ApplicationUser;
import pl.edu.agh.recorder.exception.MalformedCredentialsException;
import pl.edu.agh.recorder.security.SecurityConstants;
import pl.edu.agh.recorder.security.UserPrincipal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private SecurityConstants securityConstants;
    private AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ApplicationUser applicationUser = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(applicationUser.getUsername(), applicationUser.getPassword()));
        } catch (IOException e) {
            throw new MalformedCredentialsException("Unprocessable entity - check json");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        ZonedDateTime expirationTimeUTC = ZonedDateTime.now(ZoneOffset.UTC).plus(securityConstants.getExpirationTime(), ChronoUnit.MILLIS);
        String token = Jwts.builder().setSubject(((UserDetails) authResult.getPrincipal()).getUsername())
                .setExpiration(Date.from(expirationTimeUTC.toInstant()))
                .setId(((UserPrincipal) authResult.getPrincipal()).getApplicationUser().getId().toString())
                .signWith(SignatureAlgorithm.HS512, securityConstants.getSecret())
                .compact();
        response.getWriter().write("{\"token\":\"" + securityConstants.getTokenPrefix() + token + "\"}");
        response.addHeader("Content-Type", "application/json");
        response.addHeader(securityConstants.getHeaderString(), securityConstants.getTokenPrefix() + token);
    }
}
