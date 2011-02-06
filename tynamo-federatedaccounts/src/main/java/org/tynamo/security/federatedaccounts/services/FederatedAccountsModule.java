package org.tynamo.security.federatedaccounts.services;

import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.FilterChainDefinition;
import org.tynamo.security.federatedaccounts.HostSymbols;
import org.tynamo.security.federatedaccounts.facebook.FacebookRealm;
import org.tynamo.security.federatedaccounts.pages.CommitFacebookOauth;
import org.tynamo.security.federatedaccounts.pages.FacebookOauth;

public class FederatedAccountsModule {
	private static final String PATH_PREFIX = "federated";
	private static String version = ModuleProperties.getVersion(FederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatingRealm.class, FacebookRealm.class).withId(FacebookRealm.class.getSimpleName());
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		String hostname = null;
		try {
			hostname = System.getenv("HOSTNAME");
		} catch (Exception e) {
		}
		if (hostname == null) hostname = "localhost";
		configuration.add(HostSymbols.HOSTNAME, hostname);
		configuration.add(HostSymbols.COMMITAFTER_OAUTH, "true");
		configuration.add(HostSymbols.HTTPCLIENT_ON_GAE, "false");
		configuration.add(FacebookRealm.FACEBOOK_PRINCIPAL, FacebookRealm.PrincipalProperty.id.name());
		configuration.add(FacebookRealm.FACEBOOK_PERMISSIONS, "");
		configuration.add(FacebookRealm.FACEBOOK_CLIENTID, "");
		configuration.add(FacebookRealm.FACEBOOK_CLIENTSECRET, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(PATH_PREFIX, "org.tynamo.security.federatedaccounts"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration,
			@InjectService("FacebookRealm") AuthenticatingRealm facebookRealm) {
		configuration.add(facebookRealm);
	}

	public static void contributeSecurityRequestFilter(OrderedConfiguration<FilterChainDefinition> configuration) {
		// TODO can there possibly be security implications for this, document properly
		// We can't use linksource here because we are not in request lifecycle
		configuration.add("facebookoauth", new FilterChainDefinition("/" + FacebookOauth.class.getSimpleName().toLowerCase(), "anon"),
				"before:*");
		configuration.add("commitfacebookoauth", new FilterChainDefinition("/" + CommitFacebookOauth.class.getSimpleName().toLowerCase(),
				"anon"), "before:*");
	}

}