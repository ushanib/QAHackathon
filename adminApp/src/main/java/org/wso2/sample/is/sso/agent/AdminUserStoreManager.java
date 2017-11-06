/**
 * Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.NodeList;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;

import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.sso.agent.SSOAgentFilter;
import org.wso2.carbon.identity.sso.agent.SSOAgentConstants;
import org.wso2.carbon.um.ws.api.WSRealmBuilder;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceStub;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.ListedResource;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.SCIMConstants;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.css.sac.InputSource;


public class AdminUserStoreManager extends SSOAgentFilter {

    private static Logger LOGGER = Logger.getLogger("org.wso2.sample.is.sso.agent");


//    private final static String SERVER_URL = "https://localhost:9443/services/";
//    private final static String APP_ID = "myrole";
//    AuthenticationAdminStub authstub = null;
//    ConfigurationContext configContext = null;
//    String cookie = null;
//    String newUser = "prabath2";
//    String[] usersforRole = {"user1"};
//    String[] permision = {"1", "permission/admin"};


//    public void UserManagerEvent() {
//
//
//
//        System.setProperty("javax.net.ssl.trustStore", "wso2carbon.jks");
//        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

    public int RoleManagerEvent(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {


        //Code to make   a webservice HTTP request
        String roleName = servletRequest.getParameter("roleName");
        String users = servletRequest.getParameter("users");
        String permission = servletRequest.getParameter("permission");
        String token = servletRequest.getParameter("authorization");
//                String users = request.getParameter("users");
//                String subjectId = request.getParameter("subjectId");

        //  HttpSession session = request.getSession();


        System.out.println(token);

//        System.out.println(roleName);
//        System.out.println(users);
//        System.out.println(permission);

        String responseString = "";
        String outputString = "";


        String wsURL = "https://localhost:9443/services/UserAdmin/";
        URL url = new URL(wsURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String xmlInput =

                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://org.apache.axis2/xsd\">\n" +
        "  <soapenv:Header/>\n" +
                " <soapenv:Body>\n" +
                "   <xsd:addRole>\n" +
                "    <xsd:roleName>"+ roleName +"</xsd:roleName>\n" +
                "     <xsd:userList>"+ users +"</xsd:userList>\n" +
                "   <xsd:permissions>"+ permission +"</xsd:permissions>\n" +
                "         <xsd:isSharedRole>false</xsd:isSharedRole>\n" +
                "</xsd:addRole>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>";


              /*  " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://litwinconsulting.com/webservices/\">\n" +
                        " <soapenv:Header/>\n" +
                        " <soapenv:Body>\n" +
                        " <web:GetWeather>\n" +
                        " <!--Optional:-->\n" +
                        " <web:City>" + city + "</web:City>\n" +
                        " </web:GetWeather>\n" +
                        " </soapenv:Body>\n" +
                        " </soapenv:Envelope>";*/

        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();
        String SOAPAction ="addRole";
// Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestProperty("Authorization", "Basic " +token);
        httpConn.setRequestProperty("SOAPAction", SOAPAction);
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();
//Write the content of the request to the outputstream of the HTTP Connection.
        out.write(b);
        out.close();

        int responseCode = httpConn.getResponseCode();
        String responsemsg = httpConn.getResponseMessage();

        System.out.println("Response >>>");

        System.out.println(responseCode);
        System.out.println(responsemsg);

        return responseCode;
  }



    public void UserManagerEvent (ServletRequest servletRequest, ServletResponse servletResponse) throws IOException,  ServletException {

        try{
        //Code to make   a webservice HTTP request
        String userName = servletRequest.getParameter("userName");
        String password = servletRequest.getParameter("password");
        String role = servletRequest.getParameter("role");
        String token = servletRequest.getParameter("authorization");

        System.out.println(token);

        String responseString = "";
        String outputString = "";
        String wsURL = "https://localhost:9443/services/UserAdmin/";
        URL url = new URL(wsURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String xmlInput =

                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://org.apache.axis2/xsd\">\n" +
                        "  <soapenv:Header/>\n" +
                        " <soapenv:Body>\n" +
                        "   <xsd:addUser>\n" +
                        "    <xsd:userName>" + userName + "</xsd:userName>\n" +
                        "     <xsd:password>" + password + "</xsd:password>\n" +
                        "   <xsd:roles>" + role + "</xsd:roles>\n" +
                        "         <xsd:profileName>Default</xsd:profileName>\n" +
                        "</xsd:addUser>\n" +
                        "</soapenv:Body>\n" +
                        "</soapenv:Envelope>";


        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();
        String SOAPAction = "addUser";
// Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestProperty("Authorization", "Basic " + token);
        httpConn.setRequestProperty("SOAPAction", SOAPAction);
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();
//Write the content of the request to the outputstream of the HTTP Connection.
        out.write(b);
        out.close();

//  response.sendRedirect("success.jsp");
//Ready with sending the request.


//Read the response.
        InputStreamReader isr =
                new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

//Write the SOAP message response to a String.
        while ((responseString = in.readLine()) != null) {
            outputString = outputString + responseString;
        }


    }catch (IOException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }
    

    //format the XML in your String
    public String formatXML(String unformattedXml) {
        try {
            Document document = parseXmlFile(unformattedXml);
            OutputFormat format = new OutputFormat(document);
            format.setIndenting(true);
            format.setIndent(3);
            format.setOmitXMLDeclaration(true);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            //InputStream ins = new StringBufferInputStream(in);
            return db.parse(String.valueOf(is));
//            InputSource is = new InputSource(new StringReader(in));
//            return db.parse(is);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}




//        try {
//            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(
//                    "repo", "repo/conf/client.axis2.xml");
//            authstub = new AuthenticationAdminStub(configContext, SERVER_URL
//                    + "AuthenticationAdmin");
/*

        UserStoreManager storeManager = null;
        try {
            configContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        String endPoint = SERVER_URL + "RemoteUserStoreManagerService";
        try {
            RemoteUserStoreManagerServiceStub remoteUserStoreManagerServiceStub = new RemoteUserStoreManagerServiceStub(endPoint);
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        //AuthenticateStub.authenticateStub(userName, password, remoteUserStoreManagerServiceStub);
        UserRealm realm = null;
        try {
            realm = WSRealmBuilder.createWSRealm(SERVER_URL, cookie, configContext);
        } catch (org.wso2.carbon.user.core.UserStoreException e) {
            e.printStackTrace();
        }
        try {
            storeManager = realm.getUserStoreManager();
        } catch (org.wso2.carbon.user.core.UserStoreException e) {
            e.printStackTrace();
        }


        // Authenticates as a user having rights to add users.
           // if (authstub.login("admin", "admin", null)) {
             //   cookie = (String) authstub._getServiceClient().getServiceContext().getProperty(
               //         HTTPConstants.COOKIE_STRING);

                //UserRealm realm = WSRealmBuilder.createWSRealm(SERVER_URL, cookie, configContext);
                //UserStoreManager storeManager = realm.getUserStoreManager();

        // Add a new role - with no users - with APP_ID as the role name

        try {
            if (!storeManager.isExistingRole(APP_ID)) {

                storeManager.addRole(APP_ID, null, null);
                System.out.println("The role added successfully to the system");
            } else {
                System.out.println("The role trying to add - already there in the system");
            }
        } catch (UserStoreException e) {
            e.printStackTrace();
        }


        // Now let's see the given user [newUser] belongs to the role APP_ID.
        String[] userRoles = new String[0];
        try {
            userRoles = storeManager.getRoleListOfUser(newUser);
        } catch (UserStoreException e) {
            e.printStackTrace();
        }
        boolean found = false;

                if (userRoles != null) {
                    for (int i = 0; i < userRoles.length; i++) {
                        if (APP_ID.equals(userRoles[i])) {
                            found = true;
                            System.out.println("The user is in the required role");
                            break;
                        }
                    }
                }

                if (!found){
                    System.out.println("The user is NOT in the required role");
                }
            }*/
