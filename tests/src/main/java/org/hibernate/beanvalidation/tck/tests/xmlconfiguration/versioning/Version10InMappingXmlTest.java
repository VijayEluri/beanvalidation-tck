/*
* JBoss, Home of Professional Open Source
* Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.beanvalidation.tck.tests.xmlconfiguration.versioning;

import javax.validation.Validator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.testng.Assert.assertFalse;

/**
 * @author Hardy Ferentschik
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class Version10InMappingXmlTest extends Arquillian {

	private static final String MAPPING_FILE = "Version10InMappingXmlTest.xml";

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( Version10InMappingXmlTest.class )
				.withClass( TestEntity.class )
				.withResource( MAPPING_FILE )
				.build();
	}

	@Test
	@SpecAssertion(section = "8.2", id = "a")
	public void testValidBeanValidation10Mapping() {
		Validator validator = TestUtil.getConfigurationUnderTest()
				.addMapping( Version10InMappingXmlTest.class.getResourceAsStream( MAPPING_FILE ) )
				.buildValidatorFactory()
				.getValidator();

		assertFalse( validator.getConstraintsForClass( TestEntity.class ).isBeanConstrained() );
	}
}
