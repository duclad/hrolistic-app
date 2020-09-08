package ro.optimizit.hrolistic.ui.utils.converters;

import com.vaadin.flow.templatemodel.ModelEncoder;
import ro.optimizit.hrolistic.ui.dataproviders.DataProviderUtil;
import ro.optimizit.hrolistic.ui.utils.FormattingUtils;

public class CurrencyFormatter implements ModelEncoder<Integer, String> {

	@Override
	public String encode(Integer modelValue) {
		return DataProviderUtil.convertIfNotNull(modelValue, FormattingUtils::formatAsCurrency);
	}

	@Override
	public Integer decode(String presentationValue) {
		throw new UnsupportedOperationException();
	}
}
