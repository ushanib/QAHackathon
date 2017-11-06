/*
 *  Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.sample.is.sso.agent;

import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.identity.sso.agent.SSOAgentConstants;
import org.wso2.carbon.identity.sso.agent.SSOAgentFilter;
import org.wso2.carbon.identity.sso.agent.bean.SSOAgentConfig;
import org.wso2.carbon.registry.core.session.CurrentSession;
import org.wso2.carbon.um.ws.api.WSRealmBuilder;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Properties;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecUsernameToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import static org.wso2.carbon.registry.core.session.CurrentSession.*;

public class SSOAgentSampleFilter extends SSOAgentFilter {

    private static Logger LOGGER = Logger.getLogger("org.wso2.sample.is.sso.agent");


//    private final static String SERVER_URL = "https://localhost:9443/services/";
//    private final static String APP_ID = "myapp";
//    AuthenticationAdminStub authstub = null;
//    ConfigurationContext configContext = null;
//    String cookie = null;
//    String newUser = "prabath2";


    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static Properties properties;
    protected FilterConfig filterConfig = null;



    static{
        properties = SampleContextEventListener.getProperties();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {


//
//        WSSecUsernameToken builder = new WSSecUsernameToken();
//
//        builder.setPasswordType(WSConstants.PASSWORD_TEXT);
//        builder.setUserInfo(servletRequest.getParameter(USERNAME), servletRequest.getParameter(PASSWORD));
//        builder.prepare(doc);
//        builder.appendToHeader(secHeader);


//        HttpTransportProperties.Authenticator authenticator = null;
//        Properties properties = null;
//
//        authenticator = new HttpTransportProperties.Authenticator();
//// Add user creadentials to the transport header
//        authenticator.setUsername(servletRequest.getParameter(USERNAME));
//        authenticator.setPassword(servletRequest.getParameter(PASSWORD));
//        authenticator.setRealm("facilelogin");


        String httpBinding = servletRequest.getParameter(
                SSOAgentConstants.SSOAgentConfig.SAML2.HTTP_BINDING);
        if(httpBinding != null && !httpBinding.isEmpty()){
            if("HTTP-POST".equals(httpBinding)){

                httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
            } else if ("HTTP-Redirect".equals(httpBinding)) {

                httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
            } else {

                LOGGER.log(Level.INFO, "Unknown SAML2 HTTP Binding. Defaulting to HTTP-POST");
                httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
            }
        } else {

            LOGGER.log(Level.INFO, "SAML2 HTTP Binding not found in request. Defaulting to HTTP-POST");
            httpBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";

        }
        SSOAgentConfig config = (SSOAgentConfig)filterConfig.getServletContext().
                getAttribute(SSOAgentConstants.CONFIG_BEAN_NAME);
        config.getSAML2().setHttpBinding(httpBinding);
        config.getOpenId().setClaimedId(servletRequest.getParameter(
                SSOAgentConstants.SSOAgentConfig.OpenID.CLAIMED_ID));
        config.getOpenId().setMode(servletRequest.getParameter(
                SSOAgentConstants.OpenID.OPENID_MODE));

        if (StringUtils.isNotEmpty(servletRequest.getParameter(USERNAME)) &&
                StringUtils.isNotEmpty(servletRequest.getParameter(PASSWORD))) {

            String authorization = servletRequest.getParameter(USERNAME) + ":" + servletRequest.getParameter(PASSWORD);
            // Base64 encoded username:password value
            authorization = new String(Base64.encode(authorization.getBytes(CHARACTER_ENCODING)));
            String htmlPayload = "<html>\n" +
                    "<body>\n" +
                    "<p>You are now redirected back to " + properties.getProperty("SAML2.IdPURL") + " \n" +
                    "If the redirection fails, please click the post button.</p>\n" +
                    "<form method='post' action='" +  properties.getProperty("SAML2.IdPURL") + "'>\n" +
                    "<input type='hidden' name='sectoken' value='" + authorization + "'/>\n" +
                    "<p>\n" +
                    "<!--$saml_params-->\n" +
                    "<button type='submit'>POST</button>\n" +
                    "</p>\n" +
                    "</form>\n" +
                    "<script type='text/javascript'>\n" +
                    "document.forms[0].submit();\n" +
                    "</script>\n" +
                    "</body>\n" +
                    "</html>";

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            HttpSession session = request.getSession();
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            session.setAttribute("UserName", username);
            session.setAttribute("authorization", authorization);

//        Cookie cookPwrd = new Cookie("password", password);
//        cookPwrd.setMaxAge(30 * 60);
//        response.addCookie(cookPwrd);

            System.out.println("session password >>>>>>>");
            System.out.println(password);
            System.out.println(session.getAttribute("authorization"));

              config.getSAML2().setPostBindingRequestHTMLPayload(htmlPayload);
        } else {


                      // Reset previously sent HTML payload
            config.getSAML2().setPostBindingRequestHTMLPayload(null);
                  }

        servletRequest.setAttribute(SSOAgentConstants.CONFIG_BEAN_NAME,config);
        super.doFilter(servletRequest, servletResponse, filterChain);
    }



//    public void UserManagerEvent() {
//        try {
//            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(
//                    "repo", "repo/conf/client.axis2.xml");
//        } catch (AxisFault axisFault) {
//            axisFault.printStackTrace();
//        }
//        try {
//            authstub = new AuthenticationAdminStub(configContext, SERVER_URL
//                    + "AuthenticationAdmin");
//        } catch (AxisFault axisFault) {
//            axisFault.printStackTrace();
//        }
//        try {
//            if (authstub.login("admin", "admin", APP_ID)) {
//                cookie = (String) authstub._getServiceClient().getServiceContext().getProperty(
//                        HTTPConstants.COOKIE_STRING);
//
//                UserRealm realm = WSRealmBuilder.createWSRealm(SERVER_URL, cookie, configContext);
//                UserStoreManager storeManager = realm.getUserStoreManager();
//
//                // Add a new role - with no users - with APP_ID as the role name
//
//                try {
//                    if (!storeManager.isExistingRole(APP_ID)) {
//
//                        storeManager.addRole(APP_ID, null, null);
//                        System.out.println("The role added successfully to the system");
//                    } else {
//                        System.out.println("The role trying to add - alraedy there in the system");
//                    }
//                } catch (org.wso2.carbon.user.api.UserStoreException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    if (!storeManager.isExistingUser(newUser)) {
//                        // Let's the this user to APP_ID role we just created.
//
//                        // First let's create claims for users.
//                        // If you are using a claim that does not exist in default IS instance,
//                        Map<String, String> claims = new HashMap<String, String>();
//
//                        // TASK-1 and TASK-2 should be completed by now.
//                        // Here I am using an already existing claim
//                        claims.put("http://wso2.org/claims/businessphone", "0112842302");
//
//                        // Here we pass null for the profile - so it will use the default profile.
//                        try {
//                            storeManager.addUser(newUser, "password", new String[] { APP_ID, "loginOnly" },
//                                    claims, null);
//                        } catch (UserStoreException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println("The use added successfully to the system");
//                    } else {
//                        System.out.println("The user trying to add - alraedy there in the system");
//                    }
//                } catch (UserStoreException e) {
//                    e.printStackTrace();
//                }
//
//                // Now let's see the given user [newUser] belongs to the role APP_ID.
//                String[] userRoles = storeManager.getRoleListOfUser(newUser);
//                boolean found = false;
//
//                if (userRoles != null) {
//                    for (int i = 0; i < userRoles.length; i++) {
//                        if (APP_ID.equals(userRoles[i])) {
//                            found = true;
//                            System.out.println("The user is in the required role");
//                            break;
//                        }
//                    }
//                }
//
//                if (!found){
//                    System.out.println("The user is NOT in the required role");
//                }
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (LoginAuthenticationExceptionException e) {
//            e.printStackTrace();
//        } catch (UserStoreException e) {
//            e.printStackTrace();
//        }
//
//    }



    @Override
    public void destroy() {

    }
}
