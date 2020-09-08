/**
 *
 */
package ro.optimizit.hrolistic.ui.crud;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import ro.optimizit.hrolistic.app.security.CurrentUser;
import ro.optimizit.hrolistic.backend.data.entity.Order;
import ro.optimizit.hrolistic.backend.service.OrderService;
import ro.optimizit.hrolistic.ui.views.storefront.StorefrontView;

@Configuration
public class PresenterFactory {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<Order, StorefrontView> orderEntityPresenter(OrderService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}

}
