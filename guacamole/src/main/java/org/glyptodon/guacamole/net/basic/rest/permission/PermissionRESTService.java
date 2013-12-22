package org.glyptodon.guacamole.net.basic.rest.permission;

/*
 *  Guacamole - Clientless Remote Desktop
 *  Copyright (C) 2010  Michael Jumper
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.google.inject.Inject;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.glyptodon.guacamole.GuacamoleClientException;
import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.GuacamoleSecurityException;
import org.glyptodon.guacamole.net.auth.User;
import org.glyptodon.guacamole.net.auth.UserContext;
import org.glyptodon.guacamole.net.basic.rest.AuthProviderRESTExposure;
import org.glyptodon.guacamole.net.basic.rest.HTTPException;
import org.glyptodon.guacamole.net.basic.rest.auth.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A REST Service for handling connection CRUD operations.
 * 
 * @author James Muehlner
 */
@Path("/api/permission")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PermissionRESTService {

    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(PermissionRESTService.class);
    
    /**
     * A service for authenticating users from auth tokens.
     */
    @Inject
    private AuthenticationService authenticationService;
    
    /**
     * A service for managing the REST endpoint APIPermission objects. 
     */
    @Inject
    private PermissionService permissionService;
    
    /**
     * Gets a list of permissions for the user with the given userID.
     * 
     * @param authToken The authentication token that is used to authenticate
     *                  the user performing the operation.
     * @param userID The ID of the user to retrieve permissions for.
     * @return The permission list.
     * @throws GuacamoleException If a problem is encountered while listing permissions.
     */
    @GET
    @Path("/{userID}")
    @AuthProviderRESTExposure
    public List<APIPermission> getPermissions(@QueryParam("token") String authToken, @PathParam("userID") String userID) 
            throws GuacamoleException {
        UserContext userContext = authenticationService.getUserContextFromAuthToken(authToken);

        // Get the user
        User user = userContext.getUserDirectory().get(userID);

        if(user == null)
            throw new HTTPException(Status.NOT_FOUND, "User not found with the provided userID.");

        return permissionService.convertPermissionList(user.getPermissions());
    }
    
    /**
     * Adds a permissions for a user with the given userID.
     * 
     * @param authToken The authentication token that is used to authenticate
     *                  the user performing the operation.
     * @param userID The user ID to add the permission for.
     * @param permission The permission to add for the user with the given userID.
     * @throws GuacamoleException If a problem is encountered while adding the permission.
     */
    @POST
    @Path("/{userID}")
    @AuthProviderRESTExposure
    public void addPermission(@QueryParam("token") String authToken, 
            @PathParam("userID") String userID, APIPermission permission) 
            throws GuacamoleException {
        UserContext userContext = authenticationService.getUserContextFromAuthToken(authToken);

        // Get the user
        User user = userContext.getUserDirectory().get(userID);

        if(user == null)
            throw new HTTPException(Status.NOT_FOUND, "User not found with the provided userID.");

        // Add the new permission
        user.addPermission(permission.toPermission());
    }
    
    /**
     * Removes a permissions for a user with the given userID.
     * 
     * @param authToken The authentication token that is used to authenticate
     *                  the user performing the operation.
     * @param userID The user ID to remove the permission for.
     * @param permission The permission to remove for the user with the given userID.
     * @throws GuacamoleException If a problem is encountered while removing the permission.
     */
    @POST
    @Path("/remove{userID}/")
    @AuthProviderRESTExposure
    public void removePermission(@QueryParam("token") String authToken, 
            @PathParam("userID") String userID, APIPermission permission) 
            throws GuacamoleException {
        UserContext userContext = authenticationService.getUserContextFromAuthToken(authToken);
        
        // Get the user
        User user = userContext.getUserDirectory().get(userID);

        if(user == null)
            throw new HTTPException(Status.NOT_FOUND, "User not found with the provided userID.");

        // Remove the permission
        user.removePermission(permission.toPermission());
    }

}