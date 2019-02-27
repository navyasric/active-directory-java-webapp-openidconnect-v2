package org.xdi.oxauth.rp.websample;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/secure/aad")
public class AadController {

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String getDirectoryObjects(ModelMap model, HttpServletRequest httpRequest) {
        String accessToken = (String) httpRequest.getSession().getAttribute("access_token");
        String idToken = (String) httpRequest.getSession().getAttribute("id_token");
        if (accessToken == null) {
            model.addAttribute("error", new Exception("AuthenticationResult not found in session."));
            return "/error";
        } else {
            String data;
            try {
                data = getUserInfoFromGraph(accessToken, idToken);
                model.addAttribute("user", data);
            } catch (Exception e) {
                model.addAttribute("error", e);
                return "/error";
            }
        }
        return "/secure/aad";
    }

    private String getUserInfoFromGraph(String accessToken, String idToken) throws Exception {


        URL url = new URL("https://graph.microsoft.com/v1.0/me");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Accept","application/json");

        String goodRespStr = HttpClientHelper.getResponseStringFromConn(conn, true);
        int responseCode = conn.getResponseCode();
        JSONObject response = HttpClientHelper.processGoodRespStr(responseCode, goodRespStr);
        JSONObject jsonUser = JSONHelper.fetchDirectoryObjectJSONObject(response);

        StringBuilder builder = new StringBuilder();
        User user = new User();
        JSONHelper.convertJSONObjectToDirectoryObject(jsonUser, user);

        builder.append("<b><u>AccessToken: </u></b>" + accessToken + "<br/>");
        builder.append("<br/>");
        builder.append("<b><u>IdToken: </u></b>" + idToken + "<br/>");
        builder.append("<br/>");

        builder.append("<br/>");
        builder.append("<b><u>MS Graph Response</u></b>"+ "<br/>");
        builder.append("Name: " + user.getUserPrincipalName() + "<br/>");
        builder.append("DisplayName: " + user.getDisplayName() + "<br/>");
        builder.append("Surname: " + user.getSurname() + "<br/>");
        return builder.toString();
    }
}
