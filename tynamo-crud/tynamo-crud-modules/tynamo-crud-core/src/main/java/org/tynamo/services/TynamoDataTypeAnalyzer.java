package org.tynamo.services;

import ognl.Ognl;
import ognl.OgnlException;
import org.apache.tapestry5.beaneditor.DataType;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.tynamo.descriptor.TynamoClassDescriptor;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.util.Pair;

import java.util.List;

public class TynamoDataTypeAnalyzer implements DataTypeAnalyzer
{

	private final DescriptorService descriptorService;
	private final List<Pair> editorMap;

	/**
	 * @param descriptorService
	 * @param editorMap		 A map where the keys are OGNL expressions and the values are data type identifier used to select editor (or display) blocks
	 */
	public TynamoDataTypeAnalyzer(final DescriptorService descriptorService, final List<Pair> editorMap)
	{
		this.descriptorService = descriptorService;
		this.editorMap = editorMap;
	}

	/**
	 * The first data type identifier in the editorMap whose "key" evaluates to true for the descriptor will be used
	 * to load an editor (or display) block for the descriptor. Returns null if no match is found.
	 *
	 * @return returns the data type identifier, if known, or returns null if not known.
	 */
	public String identifyDataType(PropertyAdapter adapter)
	{
		if (adapter.getAnnotation(DataType.class) == null)
		{
			TynamoPropertyDescriptor propertyDescriptor = getPropertyDescriptor(adapter);
			for (Pair<String, String> entry : editorMap)
			{
				try
				{
					if ((Boolean) Ognl.getValue(entry.getKey(), propertyDescriptor))
					{
						return entry.getValue();
					}
				} catch (OgnlException e)
				{
					//do nothing;
				}
			}
		}

		// To avoid "no strategy" exceptions, we expect a contribution of Object.class to the empty
		// string. We convert that back to a null.
		return null;
	}

	private TynamoPropertyDescriptor getPropertyDescriptor(PropertyAdapter adapter)
	{

		TynamoClassDescriptor classDescriptor = descriptorService.getClassDescriptor(adapter.getBeanType());
		TynamoPropertyDescriptor propertyDescriptor = null;

		if (classDescriptor != null)
		{
			propertyDescriptor = classDescriptor.getPropertyDescriptor(adapter.getName());
		}

		return propertyDescriptor;
	}

}

