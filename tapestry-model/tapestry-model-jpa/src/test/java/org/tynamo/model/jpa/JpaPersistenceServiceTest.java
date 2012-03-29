/*
 * Copyright 2004 Chris Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
/*
 * Copyright 2004 Chris Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.tynamo.model.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.jpa.JpaModule;
import org.apache.tapestry5.services.TapestryModule;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.model.jpa.services.JPAPersistenceService;
import org.tynamo.model.jpa.services.TynamoJPAModule;
import org.tynamo.model.test.entities.Baz;
import org.tynamo.model.test.entities.Bing;
import org.tynamo.model.test.entities.Descendant;
import org.tynamo.model.test.entities.Foo;
import org.tynamo.services.DescriptorService;
import org.tynamo.services.TynamoCoreModule;


public class JpaPersistenceServiceTest
{

	private JPAPersistenceService persistenceService;
	private DescriptorService descriptorService;

	private static Registry registry;
	private EntityManager entityManager;

	@BeforeSuite
	public final void setup_registry()
	{
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(TapestryModule.class);
		builder.add(JpaModule.class);
		builder.add(TynamoCoreModule.class);
		builder.add(TynamoJPAModule.class);
		builder.add(TestModule.class);

		registry = builder.build();
		registry.performRegistryStartup();

		persistenceService = registry.getService(JPAPersistenceService.class);
		descriptorService =  registry.getService(DescriptorService.class);
		entityManager = registry.getService(EntityManager.class);

	}

	@AfterSuite
	public final void shutdown_registry()
	{
		registry.shutdown();
		registry = null;
	}

	@AfterMethod
	public final void cleanupThread()
	{
		registry.cleanupThread();
	}

	@Test
	public void testIgnoreCGLIBEnhancements()
	{
		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("boo");
		persistenceService.save(foo);
		foo = persistenceService.getInstance(Foo.class, 1);
		Assert.assertNotNull(descriptorService.getClassDescriptor(foo.getClass()));
	}

	@Test
	public void testQuery() throws Exception
	{

		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("the foo");
		persistenceService.save(foo);
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Foo> q = qb.createQuery(Foo.class);
		Root<Foo> root = q.from(Foo.class);
		q.where(qb.equal(root.get("name"), "the foo"));
		entityManager.createQuery(q).getSingleResult();
	}

//	@Test
//	public void testSingleResultQuery() throws Exception
//	{
//
//		Foo foo = new Foo();
//		foo.setId(1);
//		foo.setName("the foo");
//		persistenceService.save(foo);
//
//		DetachedCriteria criteria = DetachedCriteria.forClass(Foo.class);
//		criteria.add(Restrictions.eq("name", "the foo"));
//		Assert.assertNotNull(persistenceService.getInstance(Foo.class, criteria));
//
//		Foo foo2 = new Foo();
//		foo2.setId(2);
//		foo2.setName("the foo");
//		foo2.setId(2);
//
//		persistenceService.save(foo2);
//		try
//		{
//			persistenceService.getInstance(Foo.class, criteria);
//		}
//		catch (RuntimeException e)
//		{
//			return;
//		}
//		Assert.fail("IncorrectResultSizeDataAccessExcpetion not thrown, but two results should have been found");
//	}

//	@Test
//	public void testGetIntancesWithManyToOne() throws Exception
//	{
//		Bar bar = new Bar();
//
//		persistenceService.save(bar);
//		Wibble wibble = new Wibble();
//		persistenceService.save(wibble);
//		Session session = registry.getService(Session.class);
//		session.flush();
//		Assert.assertNotNull(wibble.getId());
//		wibble.setBar(bar);
//		DetachedCriteria criteria = DetachedCriteria.forClass(Wibble.class);
//		criteria.add(Restrictions.eq("bar", bar));
//		List list = persistenceService.getInstances(Foo.class, criteria);
//		Assert.assertEquals(1, list.size());
//	}

//	@Test
//	public void testQueryByExample() throws Exception
//	{
//		TynamoClassDescriptor fooClassDescriptor = descriptorService.getClassDescriptor(Foo.class);
//		List instances = persistenceService.getInstances(Foo.class);
//
//		Foo foo = new Foo();
//		foo.setId(1);
//		foo.setName("the foo");
//		Bar bar = new Bar();
//		bar = persistenceService.save(bar); //this is the new LINE
//		foo.setBar(bar);
//		persistenceService.save(foo);
//
//		Foo foo2 = new Foo();
//		foo2.setName("howdya doo");
//		foo2.setId(2);
//		persistenceService.save(foo2);
//
//		Foo example = new Foo();
//		example.setName("the foo");
//		instances = persistenceService.getInstances(Foo.class);
//		instances = persistenceService.getInstances(example, fooClassDescriptor);
//		Assert.assertEquals(instances.size(), 1, "found 1");
//
//		example.setName("foo");
//		instances = persistenceService.getInstances(example, fooClassDescriptor);
//		Assert.assertEquals(instances.size(), 1, "found 1");
//
//		Foo notherExample = new Foo();
//		notherExample.setBar(bar);
//		instances = persistenceService.getInstances(notherExample, fooClassDescriptor);
//		Assert.assertEquals(instances.size(), 1, "found 1");
//	}

//	@Test
//	public void testHibernateAnnotations() throws Exception
//	{
//		BlogEntry entry = new BlogEntry();
//		entry.setText("howdy doody");
//		entry = persistenceService.save(entry);
//		Assert.assertNotNull(entry.getId());
//	}

	@Test
	public void testSaveWithException() throws Exception
	{

		Foo foo = new Foo();

		RuntimeException exception = null;
		try
		{
			foo = persistenceService.save(foo);
			Assert.fail();
		}
		catch (RuntimeException pex)
		{
			exception = pex;
		}
		Assert.assertNotNull(exception, "caught exception");
	}

	@Test
	public void testMergeWithException() throws Exception
	{

		Foo foo = new Foo();

		RuntimeException exception = null;
		try
		{
			foo = persistenceService.merge(foo);
			Assert.fail();
		}
		catch (RuntimeException pex)
		{
			//pex.printStackTrace();
			exception = pex;
		}
		Assert.assertNotNull(exception, "caught exception");
	}

	@Test
	public void testSaveOrUpdateWithException() throws Exception
	{

		Foo foo = new Foo();

		RuntimeException exception = null;
		try
		{
			foo = persistenceService.saveOrUpdate(foo);
			Assert.fail();
		} catch (RuntimeException pex)
		{
			exception = pex;
		}
		Assert.assertNotNull(exception, "caught exception");
	}

	@Test
	public void testValidation() throws Exception
	{
		Baz baz = new Baz();
		try
		{
			persistenceService.save(baz);
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();;
			return;
		}
//		Assert.fail(InvalidStateException.class.getSimpleName() + " should have been thrown");
		Assert.fail("an exception should have been thrown");
	}

	@Test
	public void testSaveAndRemove() throws Exception
	{
		Baz baz = new Baz();
		baz.setDescription("stuff");
		baz = persistenceService.save(baz);
		persistenceService.remove(baz);
	}

	@Test
	public void testMerge() throws Exception
	{
		Baz baz = new Baz();
		baz.setDescription("whatever");
		Baz merged = persistenceService.merge(baz);
		Assert.assertNotNull(merged.getId());
		Assert.assertFalse(baz == merged);
	}

	@Test
	public void testSaveOrUpdate() throws Exception
	{
		Baz baz = new Baz();
		baz.setDescription("whatever");
		Baz saved = persistenceService.saveOrUpdate(baz);
		Assert.assertNotNull(saved.getId());
		Assert.assertTrue(baz == saved);
	}

	@Test
	public void testCount() throws Exception
	{
		Baz baz = new Baz();
		baz.setDescription("stuff");
		Baz baz2 = new Baz();
		baz2.setDescription("stuff2");
		persistenceService.save(baz);
		persistenceService.save(baz2);
		Assert.assertEquals(2, persistenceService.count(Baz.class));
	}

	@Test
	public void testGetInstancesWithLimit() throws Exception
	{
		Baz baz = new Baz();
		baz.setDescription("stuff");
		Baz baz2 = new Baz();
		baz2.setDescription("stuff2");
		persistenceService.save(baz);
		persistenceService.save(baz2);
		Assert.assertEquals(1, persistenceService.getInstances(Baz.class, 0, 1).size());
	}

	@Test
	public void testInheritance() throws Exception
	{
		Descendant descendant = new Descendant();
		descendant.setName("what");
		descendant = persistenceService.save(descendant);
	}

	@Test
	public void testGetInstance() throws Exception
	{
		Foo foo = new Foo();
		foo.setId(1);
		persistenceService.save(foo);
		Assert.assertNotNull(persistenceService.getInstance(Foo.class, 1));
		Assert.assertNull(persistenceService.getInstance(Foo.class, -999));
	}

	@Test
	public void testBazzes() throws Exception
	{
		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("boo");
		Baz baz = new Baz();
		baz.setDescription("one");
		baz.setFoo(foo);
		foo.getBazzes().add(baz);
		persistenceService.save(foo);

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Foo> q = qb.createQuery(Foo.class);
		Root<Foo> root = q.from(Foo.class);
		List foos = entityManager.createQuery(q).getResultList();
		foo = (Foo) foos.get(0);
		Assert.assertEquals(foo.getBazzes().size(), 1, "1 baz");
	}

	@Test
	public void remove_collection_element() throws Exception
	{
		CollectionDescriptor bazzesDescriptor = (CollectionDescriptor) descriptorService.getClassDescriptor(Foo.class).getPropertyDescriptor("bazzes");

		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("boo");
		foo = persistenceService.save(foo);

		Baz baz = new Baz();
		baz.setDescription("one");

		persistenceService.addToCollection(bazzesDescriptor, baz, foo);
		Assert.assertEquals(foo.getBazzes().size(), 1, "1 baz before flushing the session");

		fakeOpenSessionInViewResponse();

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Foo> q = qb.createQuery(Foo.class);
		Root<Foo> root = q.from(Foo.class);
		List foos = entityManager.createQuery(q).getResultList();
		foo = (Foo) foos.get(0);
		Assert.assertEquals(foo.getBazzes().size(), 1, "1 baz");

		persistenceService.removeFromCollection(bazzesDescriptor, baz, foo);

		Assert.assertEquals(foo.getBazzes().size(), 0, "no bazzes");
	}

	@Test
	public void save_mappedby_collection_element_with_add_method() throws Exception
	{
		resetTablesAfterCommit();

		CollectionDescriptor bazzesDescriptor = (CollectionDescriptor) descriptorService.getClassDescriptor(Foo.class).getPropertyDescriptor("bazzes");

		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("boo");
		foo = persistenceService.save(foo);

		Baz baz = new Baz();
		baz.setDescription("one");
		persistenceService.addToCollection(bazzesDescriptor, baz, foo);

		Assert.assertEquals(foo.getBazzes().size(), 1, "1 baz before flushing the session");

		fakeOpenSessionInViewResponse();

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Foo> q = qb.createQuery(Foo.class);
		Root<Foo> root = q.from(Foo.class);
		List foos = entityManager.createQuery(q).getResultList();
		foo = (Foo) foos.get(0);

		Assert.assertEquals(foo.getBazzes().size(), 1, "1 baz after the transaction commit");
	}

	@Test
	public void save_mappedby_collection_element_without_using_add_method() throws Exception
	{
		resetTablesAfterCommit();

		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("boo");
		foo = persistenceService.save(foo);

		Baz baz = new Baz();
		baz.setDescription("one");

		foo.getBazzes().add(baz); //add to Bazzes without using the PersistenceService

		Assert.assertEquals(foo.getBazzes().size(), 1, "1 baz before flushing the session");

		fakeOpenSessionInViewResponse();

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Foo> q = qb.createQuery(Foo.class);
		Root<Foo> root = q.from(Foo.class);
		List foos = entityManager.createQuery(q).getResultList();
		foo = (Foo) foos.get(0);

		Assert.assertTrue(foo.getBazzes().isEmpty(), "there shouldn't be any bazzes here, the relationship is reaondly");
	}

	@Test
	public void save_collection_element_with_getter() throws Exception
	{
		resetTablesAfterCommit();
		CollectionDescriptor bingsDescriptor = (CollectionDescriptor) descriptorService.getClassDescriptor(Foo.class).getPropertyDescriptor("bings");

		Foo foo = new Foo();
		foo.setId(1);
		foo.setName("boo");
		foo = persistenceService.save(foo);

		Bing bing = new Bing();
		bing.setDescription("one");
		bing = persistenceService.save(bing);

		persistenceService.addToCollection(bingsDescriptor, bing, foo);

		Assert.assertEquals(foo.getBings().size(), 1, "1 bing before flushing the session");

		fakeOpenSessionInViewResponse();

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Foo> q = qb.createQuery(Foo.class);
		Root<Foo> root = q.from(Foo.class);
		List foos = entityManager.createQuery(q).getResultList();
		foo = (Foo) foos.get(0);

		Assert.assertEquals(foo.getBings().size(), 1, "1 bing after the transaction commit");
	}


	private void resetTablesAfterCommit()
	{
		entityManager.createQuery("TRUNCATE TABLE Bing").executeUpdate();
		entityManager.createQuery("TRUNCATE TABLE Baz").executeUpdate();
		entityManager.createQuery("DELETE FROM Foo").executeUpdate();
		fakeOpenSessionInViewResponse();
	}

	private void fakeOpenSessionInViewResponse()
	{
		entityManager.getTransaction().commit();
		registry.cleanupThread();
	}

}