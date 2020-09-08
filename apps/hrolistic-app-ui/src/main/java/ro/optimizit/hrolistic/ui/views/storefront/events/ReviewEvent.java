package ro.optimizit.hrolistic.ui.views.storefront.events;

import com.vaadin.flow.component.ComponentEvent;
import ro.optimizit.hrolistic.ui.views.orderedit.OrderEditor;

public class ReviewEvent extends ComponentEvent<OrderEditor> {

	public ReviewEvent(OrderEditor component) {
		super(component, false);
	}
}