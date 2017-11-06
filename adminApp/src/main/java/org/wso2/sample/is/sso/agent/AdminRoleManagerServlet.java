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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminRoleManagerServlet extends HttpServlet {

    //private static final long serialVersionUID = 1L;
    AdminUserStoreManager adminUserStoreManager = new AdminUserStoreManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            try {

//               adminUserStoreManager.RoleManagerEvent(request, response);

                int responseCode = 0;


                responseCode = adminUserStoreManager.RoleManagerEvent(request, response);


                System.out.println("Response code >>>");

                System.out.println(responseCode);

                if (responseCode == 202) {
                response.sendRedirect("welcome.jsp");
                System.out.println("Response code end >>>");
                }
                else
                    response.sendRedirect("urmgterrors.jsp");

            } catch (ServletException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }
