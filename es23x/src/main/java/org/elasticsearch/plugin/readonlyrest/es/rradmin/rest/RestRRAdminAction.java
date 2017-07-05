/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
//
//package org.elasticsearch.plugin.readonlyrest.es.rradmin.rest;
//
//import org.elasticsearch.client.Client;
//import org.elasticsearch.common.inject.Inject;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.plugin.readonlyrest.es.rradmin.RRAdminAction;
//import org.elasticsearch.plugin.readonlyrest.es.rradmin.RRAdminRequest;
//import org.elasticsearch.rest.BaseRestHandler;
//import org.elasticsearch.rest.RestChannel;
//import org.elasticsearch.rest.RestController;
//import org.elasticsearch.rest.RestHandler;
//import org.elasticsearch.rest.RestRequest;
//
///**
// * Created by sscarduzio on 21/03/2017.
// */
//public class RestRRAdminAction extends BaseRestHandler implements RestHandler {
//  @Inject
//  public RestRRAdminAction(Settings settings, RestController controller, Client client) {
//    super(settings, controller, client);
//    controller.registerHandler(RestRequest.Method.POST, "/_readonlyrest/admin/refreshconfig", this);
//  }
//
//  @Override
//  protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
//    client.execute(RRAdminAction.INSTANCE, new RRAdminRequest());
//  }
//}