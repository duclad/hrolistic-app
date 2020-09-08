package ro.optimizit.hrolistic.ui.views.errors;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import ro.optimizit.hrolistic.ui.MainView;
import ro.optimizit.hrolistic.ui.exceptions.AccessDeniedException;
import ro.optimizit.hrolistic.ui.utils.BakeryConst;

@Tag("access-denied-view")
@JsModule("./src/views/errors/access-denied-view.js")
@ParentLayout(MainView.class)
@PageTitle(BakeryConst.TITLE_ACCESS_DENIED)
@Route
public class AccessDeniedView extends PolymerTemplate<TemplateModel> implements HasErrorParameter<AccessDeniedException> {

	@Override
	public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<AccessDeniedException> errorParameter) {
		return HttpServletResponse.SC_FORBIDDEN;
	}
}
