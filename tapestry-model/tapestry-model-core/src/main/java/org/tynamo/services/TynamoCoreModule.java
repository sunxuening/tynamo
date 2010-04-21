package org.tynamo.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.services.BeanBlockContribution;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.VersionedModule;
import org.tynamo.blob.BlobManager;
import org.tynamo.blob.DefaultBlobManager;
import org.tynamo.builder.BuilderDirector;
import org.tynamo.descriptor.*;
import org.tynamo.descriptor.annotation.AnnotationDecorator;
import org.tynamo.util.Pair;

public class TynamoCoreModule extends VersionedModule
{

	public final static String PROPERTY_DISPLAY_BLOCKS = "tynamo/PropertyDisplayBlocks";
	public final static String PROPERTY_EDIT_BLOCKS = "tynamo/PropertyEditBlocks";

	public static void bind(ServiceBinder binder)
	{
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.

		binder.bind(BuilderDirector.class, BuilderDirector.class);
		binder.bind(DescriptorFactory.class, ReflectionDescriptorFactory.class);
		binder.bind(PropertyDescriptorFactory.class, PropertyDescriptorFactoryImpl.class);
		binder.bind(MethodDescriptorFactory.class, MethodDescriptorFactoryImpl.class);
		binder.bind(EntityCoercerService.class, EntityCoercerServiceImpl.class);
		binder.bind(DescriptorService.class, DescriptorServiceImpl.class);
		binder.bind(TynamoDataTypeAnalyzer.class, TynamoDataTypeAnalyzer.class);

		binder.bind(BlobManager.class, DefaultBlobManager.class).withId("DefaultBlobManager");

	}

	/**
	 * Add our components and pages to the "Tynamo" library.
	 */
	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration)
	{
		configuration.add(new LibraryMapping("tynamo", "org.tynamo"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration)
	{
		configuration.add("tynamo-" + version, "org/tynamo");
	}


	/**
	 * Contribution to the BeanBlockSource service to tell the BeanEditForm component about the editors.
	 */
	public static void contributeBeanBlockSource(Configuration<BeanBlockContribution> configuration)
	{
		configuration.add(new BeanBlockContribution("hidden", PROPERTY_EDIT_BLOCKS, "hidden", true));
		configuration.add(new BeanBlockContribution("dateEditor", PROPERTY_EDIT_BLOCKS, "date", true));
		configuration.add(new BeanBlockContribution("fckEditor", PROPERTY_EDIT_BLOCKS, "fckEditor", true));
		configuration.add(new BeanBlockContribution("readOnly", PROPERTY_EDIT_BLOCKS, "readOnly", true));
		configuration.add(new BeanBlockContribution("single-valued-association", PROPERTY_EDIT_BLOCKS, "select", true));
		configuration.add(new BeanBlockContribution("identifierEditor", PROPERTY_EDIT_BLOCKS, "identifierEditor", true));
		configuration.add(new BeanBlockContribution("many-valued-association", PROPERTY_EDIT_BLOCKS, "palette", true));
		configuration.add(new BeanBlockContribution("composition", PROPERTY_EDIT_BLOCKS, "editComposition", true));
		configuration.add(new BeanBlockContribution("embedded", PROPERTY_EDIT_BLOCKS, "embedded", true));
		configuration.add(new BeanBlockContribution("blob", PROPERTY_EDIT_BLOCKS, "blob", true));

		configuration.add(new BeanBlockContribution("hidden", PROPERTY_DISPLAY_BLOCKS, "hidden", false));
		configuration.add(new BeanBlockContribution("single-valued-association", PROPERTY_DISPLAY_BLOCKS, "showPageLink", false));
		configuration.add(new BeanBlockContribution("many-valued-association", PROPERTY_DISPLAY_BLOCKS, "showPageLinks", false));
		configuration.add(new BeanBlockContribution("composition", PROPERTY_DISPLAY_BLOCKS, "showPageLinks", false));
		configuration.add(new BeanBlockContribution("blob", PROPERTY_DISPLAY_BLOCKS, "download", false));
	}

	public static void contributeDataTypeAnalyzer(OrderedConfiguration<DataTypeAnalyzer> configuration,
	                                              @InjectService("TynamoDataTypeAnalyzer")
	                                              DataTypeAnalyzer tynamoDataTypeAnalyzer)
	{
		configuration.add("Tynamo", tynamoDataTypeAnalyzer, "before:Default");
	}

	/**
	 * Contributes a set of standard type coercions to the {@link org.apache.tapestry5.ioc.services.TypeCoercer} service:
	 * <ul>
	 * <li>Class to String</li>
	 * <li>String to Double</li>
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public static void contributeTypeCoercer(final Configuration<CoercionTuple> configuration,
	                                         @InjectService("EntityCoercerService")
	                                         EntityCoercerService entityCoercerService)
	{
		configuration.add(new CoercionTuple<Class, String>(Class.class, String.class, new ClassToStringCoercion(entityCoercerService)));
		configuration.add(new CoercionTuple<String, Class>(String.class, Class.class, new StringToClassCoercion(entityCoercerService)));
	}

	public static void contributeDescriptorFactory(OrderedConfiguration<DescriptorDecorator> configuration)
	{
		configuration.add("Annotation", new AnnotationDecorator());
	}

	public static void contributeTynamoDataTypeAnalyzer(OrderedConfiguration<Pair> configuration)
	{

		addPairToOrderedConfiguration(configuration, "hidden", "hidden");
		addPairToOrderedConfiguration(configuration, "readOnly", "readOnly");
		addPairToOrderedConfiguration(configuration, "richText", "fckEditor");
//		addPairToOrderedConfiguration(configuration, "name.toLowerCase().endsWith('password')", "passwordEditor"); //USE @DataType("password")
//		addPairToOrderedConfiguration(configuration, "string and !large and !identifier", "stringEditor"); //managed by Tapestry
		addPairToOrderedConfiguration(configuration, "string and large and !identifier", "longtext");
		addPairToOrderedConfiguration(configuration, "date", "dateEditor");
//		addPairToOrderedConfiguration(configuration, "numeric and !identifier", "numberEditor"); //managed by Tapestry
		addPairToOrderedConfiguration(configuration, "identifier && generated", "readOnly");
		addPairToOrderedConfiguration(configuration, "identifier && not(generated) && string", "identifierEditor");
//		addPairToOrderedConfiguration(configuration, "identifier && objectReference", "objectReferenceIdentifierEditor");
//		addPairToOrderedConfiguration(configuration, "boolean", "booleanEditor"); //managed by Tapestry
//		addPairToOrderedConfiguration(configuration, "supportsExtension('org.tynamo.descriptor.extension.EnumReferenceDescriptor')", "enumEditor"); //managed by Tapestry
		addPairToOrderedConfiguration(configuration, "supportsExtension('org.tynamo.descriptor.extension.BlobDescriptorExtension')", "blob");

		addPairToOrderedConfiguration(configuration, "objectReference", "single-valued-association" /* (aka: ManyToOne) */);
		addPairToOrderedConfiguration(configuration, "collection && not(childRelationship)", "many-valued-association" /* (aka: ManyToMany) */);
		addPairToOrderedConfiguration(configuration, "collection && childRelationship", "composition");
		addPairToOrderedConfiguration(configuration, "name == 'id'", "readOnly");
		addPairToOrderedConfiguration(configuration, "embedded", "embedded");
	}

	private static void addPairToOrderedConfiguration(OrderedConfiguration<Pair> configuration, String key, String value)
	{
		configuration.add(key, new Pair<String, String>(key, value));
	}

	public static void contributePropertyDescriptorFactory(Configuration<String> configuration)
	{
		configuration.add("exclude.*");
		configuration.add("class");
	}

	public static void contributeMethodDescriptorFactory(Configuration<String> configuration)
	{
		configuration.add("shouldExclude");
		configuration.add("set.*");
		configuration.add("get.*");
		configuration.add("is.*");
		configuration.add("equals");
		configuration.add("wait");
		configuration.add("toString");
		configuration.add("notify.*");
		configuration.add("hashCode");
	}


}
