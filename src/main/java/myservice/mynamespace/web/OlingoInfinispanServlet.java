/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package myservice.mynamespace.web;

import java.io.IOException;
import java.lang.Override;import java.lang.RuntimeException;import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import myservice.mynamespace.data.InfinispanStorage;

import myservice.mynamespace.service.*;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Martin Ribaric
 */

public class OlingoInfinispanServlet extends HttpServlet {
    
  private static final Logger LOG = LoggerFactory.getLogger(OlingoInfinispanServlet.class);
  InfinispanStorage infinispanStorage = null;

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      //HttpSession session = req.getSession(true);
      
     // InfinispanStorage infinispanStorage = (InfinispanStorage) session.getAttribute(InfinispanStorage.class.getName());
      
      if (infinispanStorage == null){
         infinispanStorage = new InfinispanStorage();
         //session.setAttribute(InfinispanStorage.class.getName(), infinispanStorage);
      }

      // create odata handler and configure it with OlingoProvider and Processor
      OData odata = OData.newInstance();
      ServiceMetadata edm = odata.createServiceMetadata(new OlingoProvider(), new ArrayList<EdmxReference>());
      ODataHttpHandler handler = odata.createHandler(edm);
      
      handler.register(new InfinispanEntityProcessor(infinispanStorage)); 
      handler.register(new InfinispanCollectionProcessor(infinispanStorage));

      // let the handler do the work
      handler.process(req, resp);
    } catch (RuntimeException e) {
      LOG.error("Server Error occurred in ExampleServlet", e);
      throw new ServletException(e);
    }
  }
}
