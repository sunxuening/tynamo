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
package org.trails.component;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.services.ExpressionEvaluator;
import org.apache.tapestry.util.ComponentAddress;
import org.trails.descriptor.TrailsPropertyDescriptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author fus8882
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ObjectTable extends ObjectComponent
{
	public static final String LINK_COLUMN="linkColumnValue";
    public abstract boolean isShowCollections();

    public abstract void setShowCollections(boolean ShowCollections);

    public ComponentAddress getLinkBlockAddress()
    {
        return new ComponentAddress(getComponent(LINK_COLUMN));
    }

    @InjectObject("service:tapestry.ognl.ExpressionEvaluator")
    public abstract ExpressionEvaluator getEvaluator();

    /**
     * 
     * @return
     */
    public List<TrailsTableColumn> getColumns()
    {	
        ArrayList<TrailsTableColumn> columns = new ArrayList<TrailsTableColumn>();
        for (Iterator iter = getPropertyDescriptors().iterator(); iter.hasNext();)
        {
            TrailsPropertyDescriptor descriptor = (TrailsPropertyDescriptor) iter.next();
            if (displaying(descriptor))
            {
                if (getLinkProperty().equals(descriptor.getName()))
                {/*
                	Add a link for the edit page following these rules:
                	a) Use the identifier descriptor if is displayable ( summary=true ).
                	b) Use the first displayable property if the identifier property is not displayable (summary=false)
                 */
                    columns.add(new LinkTableColumn(descriptor, getLinkBlockAddress(), getEvaluator()));
                    
                } else
                {
                    columns.add(new TrailsTableColumn(descriptor, getEvaluator()));
                }

            }
        }

        return columns;
    }

    /**
     * @param descriptor
     * @return
     */
    private boolean displaying(TrailsPropertyDescriptor descriptor)
    {
        if (descriptor.isHidden() || !descriptor.isSummary())
        {
            return false;
        } else if ((descriptor.isCollection() && isShowCollections()) || (!descriptor.isCollection()))
        {
            return true;
        } else
        {
            return false;
        }

    }

    /**
     * @return
     */
    public String getIdentifierProperty()
    {
        return this.getIdentifierPropertyDescriptor().getName();

    }
    
    
    /**
     * Returns the name of the property to be used as link to the editor.
     * If the default Id property is not displayable then return the name
     * of the first displayable property.
     * @return
     */
    public String getLinkProperty()
    {
    	TrailsPropertyDescriptor propertyDescriptor = this.getIdentifierPropertyDescriptor();
        if(!this.displaying(propertyDescriptor))
        	propertyDescriptor = this.getFirstDisplayableProperty();
        
        return propertyDescriptor.getName();
    }
    
    /**
     * Returns the first displayable property.
     * @return
     */
    protected TrailsPropertyDescriptor getFirstDisplayableProperty()
    {
        for (Iterator iter = getPropertyDescriptors().iterator(); iter.hasNext();)
        {
            TrailsPropertyDescriptor descriptor = (TrailsPropertyDescriptor) iter.next();
            if (displaying(descriptor))
            {
                return descriptor;

            }
        }
        
        return null; //If we get here, that means we have no displayable descriptors
        //TODO check if we should throw an exception instead

    }
    
    /**
     * @return
     */
    protected TrailsPropertyDescriptor getIdentifierPropertyDescriptor()
    {
        return (TrailsPropertyDescriptor)getClassDescriptor().getIdentifierDescriptor();

    }
}
