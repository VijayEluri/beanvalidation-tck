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
package org.hibernate.beanvalidation.tck.tests.integration.ee;

import java.util.Set;
import javax.naming.InitialContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import org.hibernate.beanvalidation.tck.util.IntegrationTest;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectConstraintViolationMessages;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Gunnar Morling
 */
@IntegrationTest
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class JndiRetrievalTest extends Arquillian {

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClass( JndiRetrievalTest.class )
				.withClass( ConstantMessageInterpolator.class )
				.withClass( Foo.class )
				.withValidationXml( "test-validation.xml" )
				.withEmptyBeansXml()
				.build();
	}

	@Test
	@SpecAssertion(section = "10.2", id = "a")
	private void testDefaultValidatorFactoryCanBeRetrievedFromJndi() throws Exception {
		ValidatorFactory validatorFactory = InitialContext.doLookup( "java:comp/ValidatorFactory" );
		assertNotNull(
				validatorFactory,
				"Default validator factory should be bound to JNDI tree."
		);
		assertTrue(
				validatorFactory.getMessageInterpolator() instanceof ConstantMessageInterpolator,
				"Default validator factory bound to JNDI should be configured based on META-INF/validation.xml."
		);

		Set<ConstraintViolation<Foo>> violations = validatorFactory.getValidator()
				.validate( new Foo() );

		//expecting message from interpolator configured in META-INF/validation.xml
		assertCorrectConstraintViolationMessages( violations, "Invalid constraint" );
	}

	@Test
	@SpecAssertion(section = "10.2", id = "a")
	private void testDefaultValidatorCanBeRetrievedFromJndi() throws Exception {
		Validator validator = InitialContext.doLookup( "java:comp/Validator" );
		assertNotNull( validator, "Default validator should be bound to JNDI tree." );

		Set<ConstraintViolation<Foo>> violations = validator.validate( new Foo() );

		//expecting message from interpolator configured in META-INF/validation.xml
		assertCorrectConstraintViolationMessages( violations, "Invalid constraint" );
	}
}
