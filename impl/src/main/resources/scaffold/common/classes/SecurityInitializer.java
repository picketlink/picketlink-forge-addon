/*
 ** JBoss, Home of Professional Open Source
 ** Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 ** contributors by the @authors tag. See the copyright.txt in the
 ** distribution for a full listing of individual contributors.
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 ** http://www.apache.org/licenses/LICENSE-2.0
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 **/
package __package_name;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.GroupMembership;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;

/**
 * <p>This class is only necessary in order to initialize the underlying identity stores with some default data during the application startup.
 * In real world applications, you'll prefer to provide a specific page for user registration and role and group management.</p>
 *
 * <p>We are defining this bean as an EJB only because we may need to deal with transactions to actually persist data to the JPA identity store,
 * if it is being used by your project. You're not required to use EJB to persist identity data using PicketLink. However you should make sure
 * you're handling transactions properly if you are using JPA.</p>
 */
@Singleton
@Startup
public class SecurityInitializer {

    @Inject
    private PartitionManager partitionManager;

    @PostConstruct
    public void create() {
        User defaultUser = new User("picketlink");

        defaultUser.setEmail("picketlink@jboss.org");
        defaultUser.setFirstName("PicketLink");
        defaultUser.setLastName("User");

        // creates a default user
        addUser(defaultUser);

        Role userRole = new Role("User");

        // creates a generic role representing users
        addRole(userRole);

        Group administratorGroup = new Group("Administrators");

        // creates an administrator group
        addGroup(administratorGroup);

        // grant a role to an user
        grantRole(defaultUser, userRole);

        // add an user as a member of a group
        addMember(defaultUser, administratorGroup);
    }

    /**
     * <p>This is a very simple example on how to create users and query them using the PicketLink IDM API. In this case, we only
     * create an user if he is not persisted already.</p>
     *
     * @param user
     */
    public void addUser(User user) {
        IdentityManager identityManager = getIdentityManager();
        IdentityQuery<User> query = identityManager.createIdentityQuery(User.class);

        query.setParameter(User.LOGIN_NAME, user.getLoginName());

        List<User> result = query.getResultList();

        if (result.isEmpty()) {
            identityManager.add(user);
            identityManager.updateCredential(user, new Password(user.getLoginName()));
        }
    }

    /**
     * <p>This is a very simple example on how to create roles and query them using the PicketLink IDM API. In this case, we only
     * create a role if it is not persisted already.</p>
     *
     * @param role
     */
    public void addRole(Role role) {
        IdentityManager identityManager = getIdentityManager();
        IdentityQuery<Role> query = identityManager.createIdentityQuery(Role.class);

        query.setParameter(Role.NAME, role.getName());

        List<Role> result = query.getResultList();

        if (result.isEmpty()) {
            identityManager.add(role);
        }
    }

    /**
     * <p>This is a very simple example on how to create groups and query them using the PicketLink IDM API. In this case, we only
     * create a group if it is not persisted already.</p>
     *
     * @param group
     */
    public void addGroup(Group group) {
        IdentityManager identityManager = getIdentityManager();
        IdentityQuery<Group> query = identityManager.createIdentityQuery(Group.class);

        query.setParameter(Group.NAME, group.getName());

        List<Group> result = query.getResultList();

        if (result.isEmpty()) {
            identityManager.add(group);
        }
    }

    /**
     * <p>This is a very simple example on how to grant a role to an user.</p>
     *
     * @param user
     * @param role
     */
    public void grantRole(User user, Role role) {
        RelationshipManager relationshipManager = getRelationshipManager();

        relationshipManager.add(new Grant(user, role));
    }

    /**
     * <p>This is a very simple example on how to add an user as a member of a group.</p>
     *
     * @param user
     * @param group
     */
    public void addMember(User user, Group group) {
        RelationshipManager relationshipManager = getRelationshipManager();

        relationshipManager.add(new GroupMembership(user, group));
    }

    private IdentityManager getIdentityManager() {
        return this.partitionManager.createIdentityManager();
    }

    private RelationshipManager getRelationshipManager() {
        return this.partitionManager.createRelationshipManager();
    }

}