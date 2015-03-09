/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Vincent Vergnolle (vvergnolle@nuxeo.com)
 */
package org.nuxeo.binary.metadata.example.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class })
@Deploy({ "org.nuxeo.binary.metadata", "org.nuxeo.ecm.platform.commandline.executor", "org.nuxeo.ecm.actions" })
@LocalDeploy("org.nuxeo.binary.metadata.example:OSGI-INF/binary-metadata-example-contrib.xml")
@RepositoryConfig(cleanup = Granularity.METHOD)
public class BinaryMetadataExampleContribTest {

    static final String PSD_FILE_NAME = "sample.psd";

    @Inject
    CoreSession session;

    @Test
    public void itShouldUpdatePhotoshopProperties() throws Exception {
        assertNotNull(session);
        DocumentModel doc = getNewDocumentModel();

        /*
         * Explicitly add the contributed facet in order to enable the mapping rule
         */
        doc.addFacet(PhotoshopConstants.FACET);
        doc.setPropertyValue("file:content", getPsdFile());
        doc = session.createDocument(doc);

        /*
         * At this point, the BinaryMetadataService should have been updated the photoshop properties (see
         * schemas/photoshop.xsd)
         */

        assertEquals(1300L, doc.getProperty(PhotoshopConstants.WIDTH).getValue());
        assertEquals(2400L, doc.getProperty(PhotoshopConstants.HEIGHT).getValue());
        assertEquals(3L, doc.getProperty(PhotoshopConstants.NUM_CHANNELS).getValue());
        assertEquals("(Binary data 1787 bytes)", doc.getProperty(PhotoshopConstants.THUMBNAIL).getValue());
    }

    protected DocumentModel getNewDocumentModel() {
        return session.createDocumentModel("/", PSD_FILE_NAME, "File");
    }

    protected FileBlob getPsdFile() {
        return new FileBlob(FileUtils.getResourceFileFromContext("files/" + PSD_FILE_NAME));
    }
}
