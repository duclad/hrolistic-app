package ro.optimizit.hrolistic.testbench;

import org.junit.Assert;
import org.junit.Test;

import ro.optimizit.hrolistic.testbench.elements.components.DashboardLCounterLabelElement;
import ro.optimizit.hrolistic.testbench.elements.ui.DashboardViewElement;
import ro.optimizit.hrolistic.testbench.elements.ui.StorefrontViewElement;

public class DashboardViewIT extends AbstractIT<DashboardViewElement> {

	@Override
	protected DashboardViewElement openView() {
		StorefrontViewElement storefront = openLoginView().login("admin@vaadin.com", "admin");
		return storefront.getMenu().navigateToDashboard();
	}

	@Test
	public void checkRowsCount() {
		DashboardViewElement dashboardPage = openView();
		Assert.assertEquals(4, dashboardPage.getBoard().getRows().size());
	}

	@Test
	public void checkCounters() {
		DashboardViewElement dashboardPage = openView();
		int numLabels = dashboardPage.getBoard().getRows().get(0).$(DashboardLCounterLabelElement.class).all().size();
		Assert.assertEquals(4, numLabels);
	}

}
