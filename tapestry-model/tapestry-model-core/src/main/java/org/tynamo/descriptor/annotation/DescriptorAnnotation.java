package org.tynamo.descriptor.annotation;

import org.tynamo.descriptor.annotation.handlers.DescriptorAnnotationHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DescriptorAnnotation
{
	Class<? extends DescriptorAnnotationHandler> value();
}
