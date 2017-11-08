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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminUserRolesViewServlet extends HttpServlet {

    //private static final long serialVersionUID = 1L;
    AdminUserStoreManager adminUserStoreManager = new AdminUserStoreManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            try {

             adminUserStoreManager.UserRoleViewEvent(request, response);

                List<String> names = new ArrayList<String>();
                names = (List<String>) request.getAttribute("users");

                int responseCode = (int) request.getAttribute("responseCode");

                System.out.println(names);

                if (responseCode == 200){

                    System.out.println("responseCode 3 >>>>>>>");
                    System.out.println(request.getAttribute("responseCode"));
                    System.out.println(names);
                    request.getRequestDispatcher("/userlist.jsp").forward(request, response);

                }
                else
                    request.getRequestDispatcher("/urmgterrors.jsp").forward(request, response);


// names arraylist
                //https://stackoverflow.com/questions/9360223/send-array-of-objects-from-servlet-to-jsp
             //   request.setAttribute("message", names);
              //  request.getRequestDispatcher("/login.jsp").forward(request, response);
//https://stackoverflow.com/questions/5895079/how-can-i-send-an-array-of-string-from-servlet-to-jsp-and-recieve-it-in-jsp

            } catch (ServletException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }
