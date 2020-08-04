/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.xar.internal;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.xwiki.bridge.event.ApplicationReadyEvent;
import org.xwiki.bridge.event.WikiReadyEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.manager.ComponentRepositoryException;
import org.xwiki.extension.InstalledExtension;
import org.xwiki.extension.event.ExtensionInstalledEvent;
import org.xwiki.extension.event.ExtensionUpgradedEvent;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;
import org.xwiki.xar.XarEntryType;
import org.xwiki.xar.XarEntryType.UpgradeType;

/**
 * This listener is responsible for handling the registration of XAR entry types upon the installation of the extension
 * and upon wiki startup.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named(XAREntryTypeRegistrationListener.LISTENER_NAME)
public class XAREntryTypeRegistrationListener implements EventListener
{
    /**
     * The name of the listener.
     */
    public static final String LISTENER_NAME = "XAREntryTypeRegistrationListener";

    /**
     * The ID of the extension for which the listener should react.
     */
    private static final String EXTENSION_ID = "org.xwiki.contrib:api-extra-xar-entry-types";

    @Inject
    private ComponentManager componentManager;

    @Inject
    private Logger logger;

    @Override
    public String getName()
    {
        return LISTENER_NAME;
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.asList(new ApplicationReadyEvent(), new WikiReadyEvent(), new ExtensionInstalledEvent(),
            new ExtensionUpgradedEvent());
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        if (event instanceof ExtensionInstalledEvent || event instanceof ExtensionUpgradedEvent) {
            // Install and upgrade events provide the same source
            InstalledExtension installedExtension = (InstalledExtension) source;

            if (installedExtension.getId().getId().equals(EXTENSION_ID)) {
                registerXarEntryTypes();
            }
        } else {
            registerXarEntryTypes();
        }
    }

    private void registerXarEntryTypes()
    {
        for (UpgradeType upgradeType : UpgradeType.values())
        {
            // Register a XAR entry type for every possible scenario
            createAndRegisterEntryType(upgradeType, true, true);
            createAndRegisterEntryType(upgradeType, true, false);
            createAndRegisterEntryType(upgradeType, false, true);
            createAndRegisterEntryType(upgradeType, false, false);
        }
    }

    private void createAndRegisterEntryType(UpgradeType upgradeType, boolean isEditAllowed, boolean isDeleteAllowed)
    {
        CustomizableXarEntryType customizableXarEntryType =
            new CustomizableXarEntryType(upgradeType, isEditAllowed, isDeleteAllowed);

        DefaultComponentDescriptor<XarEntryType> entryTypeDescriptor = new DefaultComponentDescriptor<>();
        entryTypeDescriptor.setImplementation(CustomizableXarEntryType.class);
        entryTypeDescriptor.setRoleType(XarEntryType.class);
        entryTypeDescriptor.setRoleHint(customizableXarEntryType.getName());

        logger.debug("Registering XAR entry type [{}].", customizableXarEntryType.getName());
        try {
            if (componentManager.hasComponent(XarEntryType.class, customizableXarEntryType.getName())) {
                logger.debug("Removing previously registered entry type [{}]", customizableXarEntryType.getName());
                componentManager.unregisterComponent(XarEntryType.class, customizableXarEntryType.getName());
            }

            componentManager.registerComponent(entryTypeDescriptor, customizableXarEntryType);
        } catch (ComponentRepositoryException e) {
            logger.error("Failed to register XAR entry type [{}]. The installation of extensions using this type may "
                + "fail !", customizableXarEntryType.getName());
        }
    }
}
