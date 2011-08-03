package org.tynamo.security.federatedaccounts.twitter.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.tynamo.security.federatedaccounts.twitter.base.TwitterOauthComponentBase;
import org.tynamo.security.federatedaccounts.util.WindowMode;

@Import(library = "TwitterSignIn.js")
public class TwitterSignIn extends TwitterOauthComponentBase {
	// from https://dev.twitter.com/docs/auth/sign-in-with-twitter
	public enum ButtonStyle {
		darker("sign-in-with-twitter-d.png"), darker_small("sign-in-with-twitter-d-sm.png"), lighter(
			"sign-in-with-twitter-l.png"), ligter_small("sign-in-with-twitter-l-sm.png");
		private String resourceName;

		ButtonStyle(String resourceName) {
			this.resourceName = resourceName;
		}

		@Override
		public String toString() {
			return resourceName;
		}
	}

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "800")
	@Property
	private Integer width;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "400")
	@Property
	private Integer height;

	@Parameter(value = "blank", required = false, defaultPrefix = "literal")
	private ButtonStyle buttonStyle;

	@Parameter(value = "blank", required = false, defaultPrefix = "literal")
	private WindowMode windowMode;

	@Inject
	private AssetSource assetSource;

	public Asset getSignInImage() {
		return assetSource.getClasspathAsset(buttonStyle.toString());
	}

	public boolean isWindowMode(String mode) {
		if (mode == null) throw new IllegalArgumentException("Window mode argument cannot be null");
		return windowMode.equals(WindowMode.valueOf(mode));
	}

	public String getOauthAuthorizationLink() {
		StringBuilder sb = new StringBuilder();
		sb.append("https://graph.facebook.com/oauth/authorize?client_id=");
		sb.append(getOauthClientId());
		sb.append("&redirect_uri=");
		sb.append(getOauthRedirectLink(windowMode));
		sb.append("&display=popup");
		sb.append("&scope=");
		// sb.append(facebookPermissions);

		return sb.toString();
	}
}
