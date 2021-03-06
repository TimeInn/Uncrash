package net.uncrash.authorization.api.web;


import javax.servlet.http.HttpServletRequest;

public interface TokenParser {

    ParsedToken parseToken(HttpServletRequest request);

    ParsedToken parseJwt(String jwtToken);

}
