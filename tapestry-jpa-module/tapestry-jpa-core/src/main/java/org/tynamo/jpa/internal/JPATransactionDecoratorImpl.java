// Copyright 2008, 2009 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.tynamo.jpa.internal;

import org.apache.tapestry5.ioc.services.AspectDecorator;
import org.apache.tapestry5.ioc.services.AspectInterceptorBuilder;
import org.tynamo.jpa.JPATransactionAdvisor;
import org.tynamo.jpa.JPATransactionDecorator;

public class JPATransactionDecoratorImpl implements JPATransactionDecorator
{
	private final AspectDecorator aspectDecorator;

	private final JPATransactionAdvisor advisor;

	public JPATransactionDecoratorImpl(AspectDecorator aspectDecorator, JPATransactionAdvisor advisor)
	{
		this.aspectDecorator = aspectDecorator;
		this.advisor = advisor;
	}

	public <T> T build(Class<T> serviceInterface, T delegate, String serviceId)
	{
		assert serviceInterface != null;
		assert delegate != null;
		assert serviceId != null && !serviceId.isEmpty();

		String description = String.format("<JPA Transaction interceptor for %s(%s)>", serviceId, serviceInterface
		        .getName());

		AspectInterceptorBuilder<T> builder = aspectDecorator.createBuilder(serviceInterface, delegate, description);

		advisor.addTransactionCommitAdvice(builder);

		return builder.build();
	}
}
