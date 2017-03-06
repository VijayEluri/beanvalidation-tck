/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual contributors
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
package org.hibernate.beanvalidation.tck.tests.constraints.constraintdefinition;

import static org.hibernate.beanvalidation.tck.util.TestUtil.assertCorrectNumberOfViolations;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.beanvalidation.tck.util.TestUtil;
import org.hibernate.beanvalidation.tck.util.shrinkwrap.WebArchiveBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * @author Hardy Ferentschik
 * @author Guillaume Smet
 */
@SpecVersion(spec = "beanvalidation", version = "2.0.0")
public class ConstraintDefinitionsTest extends Arquillian {

	@Deployment
	public static WebArchive createTestArchive() {
		return new WebArchiveBuilder()
				.withTestClassPackage( ConstraintDefinitionsTest.class )
				.build();
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "3.1.1", id = "a"),
			@SpecAssertion(section = "3.2", id = "a")
	})
	public void testConstraintWithCustomAttributes() {
		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintDescriptor<?>> descriptors = validator.getConstraintsForClass( Person.class )
				.getConstraintsForProperty( "lastName" )
				.getConstraintDescriptors();

		assertEquals( descriptors.size(), 2, "There should be two constraints on the lastName property." );
		for ( ConstraintDescriptor<?> descriptor : descriptors ) {
			assertEquals(
					descriptor.getAnnotation().annotationType().getName(),
					AlwaysValid.class.getName(),
					"Wrong annotation type."
			);
		}

		Set<ConstraintViolation<Person>> constraintViolations = validator.validate( new Person( "John", "Doe" ) );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "3.2", id = "a"),
			@SpecAssertion(section = "3.2", id = "b")
	})
	public void testRepeatableConstraint() {
		Validator validator = TestUtil.getValidatorUnderTest();
		Set<ConstraintDescriptor<?>> descriptors = validator.getConstraintsForClass( Movie.class )
				.getConstraintsForProperty( "title" )
				.getConstraintDescriptors();

		assertEquals( descriptors.size(), 2, "There should be two constraints on the title property." );
		for ( ConstraintDescriptor<?> descriptor : descriptors ) {
			assertEquals(
					descriptor.getAnnotation().annotationType().getName(),
					Size.class.getName(),
					"Wrong annotation type."
			);
		}

		Set<ConstraintViolation<Movie>> constraintViolations = validator.validate( new Movie( "Title" ) );
		assertCorrectNumberOfViolations( constraintViolations, 0 );

		constraintViolations = validator.validate( new Movie( "A" ) );
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		constraintViolations = validator.validate( new Movie( "A movie title far too long that does not respect the constraint" ) );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
	}

	@Test
	@SpecAssertion(section = "3.1.1.2", id = "d")
	public void testDefaultGroupAssumedWhenNoGroupsSpecified() {
		Validator validator = TestUtil.getValidatorUnderTest();
		ConstraintDescriptor<?> descriptor = validator.getConstraintsForClass( Person.class )
				.getConstraintsForProperty( "firstName" )
				.getConstraintDescriptors()
				.iterator()
				.next();

		Set<Class<?>> groups = descriptor.getGroups();
		assertEquals( groups.size(), 1, "The group set should only contain one entry." );
		assertEquals( groups.iterator().next(), Default.class, "The Default group should be returned." );
	}
}
