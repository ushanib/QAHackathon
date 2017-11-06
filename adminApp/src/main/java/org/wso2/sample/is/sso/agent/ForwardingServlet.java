/**
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

import org.wso2.carbon.identity.sso.agent.SSOAgentConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

public class ForwardingServlet extends HttpServlet {

    protected  void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("session 1 >>>>>>>");
        if(request.getRequestURI().endsWith("/samlsso") || request.getRequestURI().endsWith("/openid") ||
                request.getRequestURI().endsWith("/token")) {

            System.out.println("session 2 >>>>>>>");
            request.getRequestDispatcher("home.jsp").forward(request, response);
//        }if(request.getRequestURI().endsWith("/rolesubmit")){
//                request.getRequestDispatcher("welcome.jsp").forward(request,response);
            }else if (request.getRequestURI().endsWith("/logout")){
            System.out.println("session 3 >>>>>>>");
            request.getRequestDispatcher("index.jsp").forward(request,response);
        }
    }

}
