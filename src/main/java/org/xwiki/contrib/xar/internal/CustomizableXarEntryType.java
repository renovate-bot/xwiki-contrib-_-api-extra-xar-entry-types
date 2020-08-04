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

import org.xwiki.xar.type.AbstractXarEntryType;

/**
 * Defines a customizable XAR entry type that can then be registered against a component manager.
 *
 * @version $Id$
 * @since 1.0
 */
public class CustomizableXarEntryType extends AbstractXarEntryType
{
    private static final String NAME_DELIMITER = "-";

    /**
     * This is the default constructor of the entry type, which will be generating its own name based on the
     * parameters of its constructor.
     *
     * @param upgradeType the type of upgrade that should be used, see {@link #setUpgradeType(UpgradeType)}
     * @param editAllowed see {@link #setEditAllowed(boolean)}
     * @param deleteAllowed see {@link #setDeleteAllowed(boolean)}
     */
    public CustomizableXarEntryType(UpgradeType upgradeType, boolean editAllowed, boolean deleteAllowed)
    {
        super(generateXarEntryTypeName(upgradeType, editAllowed, deleteAllowed));

        setUpgradeType(upgradeType);
        setEditAllowed(editAllowed);
        setDeleteAllowed(deleteAllowed);
    }

    /**
     * Generate the name of the XAR entry type.
     *
     * @param upgradeType the type of upgrade
     * @param editAllowed whether the edition of the entry is allowed or not
     * @param deleteAllowed whether the deletion of the entry is allowed or not
     * @return the name of the entry
     */
    public static String generateXarEntryTypeName(UpgradeType upgradeType, boolean editAllowed, boolean deleteAllowed)
    {
        StringBuilder sb = new StringBuilder();

        sb.append((editAllowed) ? "edit" : "noedit");
        sb.append(NAME_DELIMITER);
        sb.append((deleteAllowed) ? "delete" : "nodelete");
        sb.append(NAME_DELIMITER);
        sb.append(upgradeType.toString().toLowerCase());

        return sb.toString();
    }
}
