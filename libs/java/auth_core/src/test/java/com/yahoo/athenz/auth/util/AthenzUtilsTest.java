/*
 * Copyright 2018 Oath Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yahoo.athenz.auth.util;

import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.testng.Assert.*;

public class AthenzUtilsTest {

    @Test
    public void testExtractServicePrincipal() throws Exception {
        try (InputStream inStream = new FileInputStream("src/test/resources/x509_altnames_singleip.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertEquals("athenz.production", AthenzUtils.extractServicePrincipal(cert));
        }

        try (InputStream inStream = new FileInputStream("src/test/resources/ec_public_x509.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertEquals("athenz.syncer", AthenzUtils.extractServicePrincipal(cert));
        }
    }

    @Test
    public void testExtractServicePrincipalRoleCert() throws Exception {
        try (InputStream inStream = new FileInputStream("src/test/resources/valid_email_x509.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertEquals("athens.zts", AthenzUtils.extractServicePrincipal(cert));
        }
    }

    @Test
    public void testExtractServicePrincipalNoCn() throws Exception {
        try (InputStream inStream = new FileInputStream("src/test/resources/no_cn_x509.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertNull(AthenzUtils.extractServicePrincipal(cert));
        }
    }

    @Test
    public void testExtractServicePrincipalInvalidEmailCount() throws Exception {
        try (InputStream inStream = new FileInputStream("src/test/resources/no_email_x509.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertNull(AthenzUtils.extractServicePrincipal(cert));
        }

        try (InputStream inStream = new FileInputStream("src/test/resources/multiple_email_x509.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertNull(AthenzUtils.extractServicePrincipal(cert));
        }
    }

    @Test
    public void testExtractServicePrincipalInvalidEmailFormat() throws Exception {
        try (InputStream inStream = new FileInputStream("src/test/resources/invalid_email_x509.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertNull(AthenzUtils.extractServicePrincipal(cert));
        }
    }

    @Test
    public void testIsRoleCertificateServiceCertificate() throws Exception {
        try (InputStream inStream = new FileInputStream("src/test/resources/x509_altnames_singleip.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertFalse(AthenzUtils.isRoleCertificate(cert));
        }
    }

    @Test
    public void testIsRoleCertificateRoleCertificate() throws Exception {
        try (InputStream inStream = new FileInputStream("src/test/resources/valid_email_x509.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertTrue(AthenzUtils.isRoleCertificate(cert));
        }
    }

    @Test
    public void testIsRoleCertificateNoCn() throws Exception {
        try (InputStream inStream = new FileInputStream("src/test/resources/no_cn_x509.cert")) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);

            assertFalse(AthenzUtils.isRoleCertificate(cert));
        }
    }

    @Test
    public void testExtractRoleName() {
        assertEquals(AthenzUtils.extractRoleName("athenz:role.readers"), "readers");
        assertEquals(AthenzUtils.extractRoleName("athenz.api:role.readers"), "readers");
        assertEquals(AthenzUtils.extractRoleName("athenz.api.test:role.readers"), "readers");

        assertNull(AthenzUtils.extractRoleName("athenz:roles.readers"));
        assertNull(AthenzUtils.extractRoleName("athenz.role.readers"));
        assertNull(AthenzUtils.extractRoleName("athenz:role."));
        assertNull(AthenzUtils.extractRoleName(":role.readers"));
        assertNull(AthenzUtils.extractRoleName("athenz.readers"));
    }

    @Test
    public void testExtractRoleDomainName() {
        assertEquals(AthenzUtils.extractRoleDomainName("athenz:role.readers"), "athenz");
        assertEquals(AthenzUtils.extractRoleDomainName("athenz.api:role.readers"), "athenz.api");
        assertEquals(AthenzUtils.extractRoleDomainName("athenz.api.test:role.readers"), "athenz.api.test");

        assertNull(AthenzUtils.extractRoleDomainName("athenz.role.readers"));
        assertNull(AthenzUtils.extractRoleDomainName("athenz:roles.readers"));
        assertNull(AthenzUtils.extractRoleDomainName("athenz:role."));
        assertNull(AthenzUtils.extractRoleDomainName(":role.readers"));
        assertNull(AthenzUtils.extractRoleDomainName("athenz.readers"));
    }

    @Test
    public void testExtractPrincipalDomainName() {
        assertEquals(AthenzUtils.extractPrincipalDomainName("athenz.reader"), "athenz");
        assertEquals(AthenzUtils.extractPrincipalDomainName("athenz.api.reader"), "athenz.api");
        assertEquals(AthenzUtils.extractPrincipalDomainName("athenz.api.test.reader"), "athenz.api.test");

        assertNull(AthenzUtils.extractPrincipalDomainName("athenz"));
        assertNull(AthenzUtils.extractPrincipalDomainName("athenz."));
        assertNull(AthenzUtils.extractPrincipalDomainName(".athenz"));
    }

    @Test
    public void testExtractPrincipalServiceName() {
        assertEquals(AthenzUtils.extractPrincipalServiceName("athenz.reader"), "reader");
        assertEquals(AthenzUtils.extractPrincipalServiceName("athenz.api.reader"), "reader");
        assertEquals(AthenzUtils.extractPrincipalServiceName("athenz.api.test.reader"), "reader");

        assertNull(AthenzUtils.extractPrincipalServiceName("athenz"));
        assertNull(AthenzUtils.extractPrincipalServiceName("athenz."));
        assertNull(AthenzUtils.extractPrincipalServiceName(".athenz"));
    }
}
