/**
 *
 */
package ro.optimizit.hrolistic.ui.utils.converters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ro.optimizit.hrolistic.test.FormattingTest;

public class CurrencyFormatterTest extends FormattingTest {

	@Test
	public void formattingShoudBeLocaleIndependent() {
		CurrencyFormatter formatter = new CurrencyFormatter();
		String result = formatter.encode(123456);
		assertEquals("$1,234.56", result);
	}
}
