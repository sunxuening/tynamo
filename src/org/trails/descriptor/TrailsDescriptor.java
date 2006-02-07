/*
 * Created on Jan 28, 2005
 *
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
package org.trails.descriptor;

import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;

/**
 * @author fus8882
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrailsDescriptor implements IDescriptor
{

    private String displayName;
    private String shortDescription;
    protected Class type;
    private boolean hidden;

    public TrailsDescriptor(IDescriptor descriptor)
    {
        copyFrom(descriptor);
    }
    
    public TrailsDescriptor(Class type)
    {
        this.type = type;
    }
    
    
    @Override
    public Object clone()
    {
        return new TrailsDescriptor(this);
    }

    /**
     * @param name
     * @return
     */
    public String unCamelCase(String name)
    {
        ArrayList words = new ArrayList();
        Perl5Util perl = new Perl5Util();

        while (perl.match("/(\\w+?)([A-Z].*)/", name))
        {
            String word = perl.group(1);
            name = perl.group(2);
            words.add(StringUtils.capitalise(word));
        }

        words.add(StringUtils.capitalise(name));

        return StringUtils.join(words.iterator(), " ");
    }

    public String getDisplayName()
    {
        return unCamelCase(displayName);
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    protected void copyFrom(IDescriptor descriptor)
    {
        try
        {
            BeanUtils.copyProperties(this, descriptor);
        }
        catch (Exception ex)
        {
            //ex.printStackTrace();
        }
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public Class getType()
    {
        return type;
    }

    public void setType(Class type)
    {
        this.type = type;
    }

}
