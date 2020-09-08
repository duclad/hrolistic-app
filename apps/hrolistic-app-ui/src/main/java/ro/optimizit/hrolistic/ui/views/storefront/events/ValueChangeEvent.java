package ro.optimizit.hrolistic.ui.views.storefront.events;

import com.vaadin.flow.component.ComponentEvent;
import ro.optimizit.hrolistic.ui.views.orderedit.OrderItemsEditor;

public class ValueChangeEvent extends ComponentEvent<OrderItemsEditor> {

	public ValueChangeEvent(OrderItemsEditor component) {
		super(component, false);
	}
}