package org.xdi.oxauth.rp.websample;

import com.google.common.base.Strings;
import com.nimbusds.jwt.JWTParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xdi.oxauth.client.*;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author aparna.
 */
public class LoginFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(LoginFilter.class);
    public static final String STATES = "states";
    public static final String STATE = "state";
    public static final Integer STATE_TTL = 3600;
    public static final String FAILED_TO_VALIDATE_MESSAGE = "Failed to validate data received from Authorization service - ";

    public static final String WELL_KNOWN_CONNECT_PATH = "/.well-known/openid-configuration";

    private String authorizeParameters;
    private String redirectUri;
    private String authorizationServerHost;
    private String clientId;
    private String clientSecret;
    private OpenIdConfigurationResponse discoveryResponse;

    @Override
    public void init(FilterConfig filterConfig) {
        authorizeParameters = filterConfig.getInitParameter("authorizeParameters");
        redirectUri = filterConfig.getInitParameter("redirectUri");
        authorizationServerHost = filterConfig.getInitParameter("authorizationServerHost");
        clientId = filterConfig.getInitParameter("clientId");
        clientSecret = filterConfig.getInitParameter("clientSecret");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        boolean redirectForLogin;
        try {
            redirectForLogin = fetchTokenIfCodeIsPresent(request);
        } catch (Throwable e) {
            return;
        }

        Object accessToken = request.getSession(true).getAttribute("access_token");
        if (accessToken == null) {
            if (redirectForLogin) {
                redirectToLogin(request, response);
            } else {
                LOG.trace("Login failed.");
                response.setContentType("text/html;charset=utf-8");

                PrintWriter pw = response.getWriter();
                pw.println("<h3>Login failed.</h3>");
            }
        } else {
            LOG.trace("User is already authenticated.");
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void fetchDiscovery() {
        try {
            if (discoveryResponse != null) { // already initialized
                return;
            }

            OpenIdConfigurationClient discoveryClient = new OpenIdConfigurationClient(authorizationServerHost + WELL_KNOWN_CONNECT_PATH);

            discoveryResponse = discoveryClient.execOpenIdConfiguration();
            LOG.trace("Discovery: " + discoveryResponse);

            if (discoveryResponse.getStatus() == 200) {
                return;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        throw new RuntimeException("Failed to fetch discovery information at : " + authorizationServerHost + WELL_KNOWN_CONNECT_PATH);
    }

    /**
     * @param request request
     * @return whether login is still required
     */
    private boolean fetchTokenIfCodeIsPresent(HttpServletRequest request) throws Throwable {
        String code = request.getParameter("code");

        HashMap<String, String> params = new HashMap<String, String>();
        for (String key : request.getParameterMap().keySet()) {
            params.put(key, request.getParameterMap().get(key)[0]);
        }

        StateData stateData;
        if (code != null && !code.trim().isEmpty()) {
            LOG.trace("Fetching token for code " + code + " ...");
            fetchDiscovery();

            TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE);
            tokenRequest.setCode(code);
            tokenRequest.setRedirectUri(redirectUri);
            tokenRequest.setAuthUsername(clientId);
            tokenRequest.setAuthPassword(clientSecret);
            tokenRequest.setAuthenticationMethod(AuthenticationMethod.CLIENT_SECRET_BASIC);

            TokenClient tokenClient = new TokenClient(discoveryResponse.getTokenEndpoint());
            tokenClient.setRequest(tokenRequest);

            TokenResponse tokenResponse = tokenClient.exec();

            try {
                stateData = validateState(request.getSession(), params.get(STATE));
            } catch (Exception e) {
                throw new Exception("State data invalid", e);
            }
            
            validateNonce(stateData, getClaimValueFromIdToken(tokenResponse.getIdToken(), "nonce"));

            if (!Strings.isNullOrEmpty(tokenResponse.getAccessToken())) {
                LOG.trace("Token is successfully fetched.");

                LOG.trace("Put in session access_token: " + tokenResponse.getAccessToken() + ", id_token: " + tokenResponse.getIdToken() + ", graph_endpoint: " + discoveryResponse.getUserInfoEndpoint());
                request.getSession(true).setAttribute("access_token", tokenResponse.getAccessToken());
                request.getSession(true).setAttribute("id_token", tokenResponse.getIdToken());
            } else {
                LOG.trace("Failed to obtain token. Status: " + tokenResponse.getStatus() + ", entity: " + tokenResponse.getEntity());
            }
            return false;
        }
        return true;
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        fetchDiscovery();

        String state = UUID.randomUUID().toString();

        String nonce = UUID.randomUUID().toString();

        storeStateInSession(request.getSession(), state, nonce);

        String redirectTo = discoveryResponse.getAuthorizationEndpoint() +
                "?redirect_uri=" + redirectUri + "&client_id=" + clientId + "&" + authorizeParameters + "&state=" + state +"&nonce=" +nonce;
        LOG.trace("Redirecting to authorization url : " + redirectTo);
        response.sendRedirect(redirectTo);
    }

    private void validateNonce(StateData stateData, String nonce) throws Exception {
        if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
            throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate nonce");
        }
    }

    private String getClaimValueFromIdToken(String idToken, String claimKey) throws ParseException {
        return (String) JWTParser.parse(idToken).getJWTClaimsSet().getClaim(claimKey);
    }

    @SuppressWarnings("unchecked")
    private void storeStateInSession(HttpSession session, String state, String nonce) {
        if (session.getAttribute(STATES) == null) {
            session.setAttribute(STATES, new HashMap<String, StateData>());
        }
        ((Map<String, StateData>) session.getAttribute(STATES)).put(state, new StateData(nonce, new Date()));
    }

    /**
     * make sure that state is stored in the session,
     * delete it from session - should be used only once
     *
     * @param session
     * @param state
     * @throws Exception
     */
    private StateData validateState(HttpSession session, String state) throws Exception {
        if (StringUtils.isNotEmpty(state)) {
            StateData stateDataInSession = removeStateFromSession(session, state);
            if (stateDataInSession != null) {
                return stateDataInSession;
            }
        }
        throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate state");
    }

    @SuppressWarnings("unchecked")
    private StateData removeStateFromSession(HttpSession session, String state) {
        Map<String, StateData> states = (Map<String, StateData>) session.getAttribute(STATES);
        if (states != null) {
            eliminateExpiredStates(states);
            StateData stateData = states.get(state);
            if (stateData != null) {
                states.remove(state);
                return stateData;
            }
        }
        return null;
    }

    private void eliminateExpiredStates(Map<String, StateData> map) {
        Iterator<Map.Entry<String, StateData>> it = map.entrySet().iterator();

        Date currTime = new Date();
        while (it.hasNext()) {
            Map.Entry<String, StateData> entry = it.next();
            long diffInSeconds = TimeUnit.MILLISECONDS.
                    toSeconds(currTime.getTime() - entry.getValue().getExpirationDate().getTime());

            if (diffInSeconds > STATE_TTL) {
                it.remove();
            }
        }
    }

    private class StateData {
        private String nonce;
        private Date expirationDate;

        public StateData(String nonce, Date expirationDate) {
            this.nonce = nonce;
            this.expirationDate = expirationDate;
        }

        public String getNonce() {
            return nonce;
        }

        public Date getExpirationDate() {
            return expirationDate;
        }
    }

    @Override
    public void destroy() {

    }

}
