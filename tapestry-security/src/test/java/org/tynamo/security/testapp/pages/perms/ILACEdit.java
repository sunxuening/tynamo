package org.tynamo.security.testapp.pages.perms;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.tynamo.security.testapp.pages.AccessiblePage;

@RequiresPermissions("ilac:edit")
public class ILACEdit extends AccessiblePage {

}
