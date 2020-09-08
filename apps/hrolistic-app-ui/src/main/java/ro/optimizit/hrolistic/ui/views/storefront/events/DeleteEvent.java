package ro.optimizit.hrolistic.ui.views.storefront.events;

import com.vaadin.flow.component.ComponentEvent;
import ro.optimizit.hrolistic.ui.views.orderedit.OrderItemEditor;

public class DeleteEvent extends ComponentEvent<OrderItemEditor> {
	public DeleteEvent(OrderItemEditor component) {
		super(component, false);
	}
}