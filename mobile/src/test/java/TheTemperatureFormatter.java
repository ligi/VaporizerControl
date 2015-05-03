import org.junit.Before;
import org.junit.Test;
import org.ligi.vaporizercontrol.Settings;
import org.ligi.vaporizercontrol.util.TemperatureFormatter;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TheTemperatureFormatter {

    @Mock
    Settings settings;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(settings.getTemperatureFormat()).thenReturn(Settings.TEMPERATURE_CELSIUS);
        when(settings.isDisplayUnitWanted()).thenReturn(false);
        when(settings.isPreciseWanted()).thenReturn(false);
    }

    @Test
    public void testThatDegreesNoUnitNotPreciseWorks() {
        assertThat(TemperatureFormatter.Companion.getFormattedTemp(settings, 231, true)).isEqualTo("23");
    }

    @Test
    public void testThatDegreesUnitNotPreciseWorks() {
        when(settings.isDisplayUnitWanted()).thenReturn(true);
        assertThat(TemperatureFormatter.Companion.getFormattedTemp(settings, 231, true)).isEqualTo("23 °C");
    }

    @Test
    public void testThatDegreesUnitPreciseWorks() {
        when(settings.isDisplayUnitWanted()).thenReturn(true);
        when(settings.isPreciseWanted()).thenReturn(true);
        assertThat(TemperatureFormatter.Companion.getFormattedTemp(settings, 235, true)).isEqualTo("23.5 °C");
    }


    @Test
    public void testThatFahrenheitWorks() {
        when(settings.getTemperatureFormat()).thenReturn(Settings.TEMPERATURE_FAHRENHEIT);
        assertThat(TemperatureFormatter.Companion.getFormattedTemp(settings, 231, true)).isEqualTo("74");
    }

    @Test
    public void testThatKelvinWorks() {
        when(settings.getTemperatureFormat()).thenReturn(Settings.TEMPERATURE_KELVIN);
        assertThat(TemperatureFormatter.Companion.getFormattedTemp(settings, 231, true)).isEqualTo("296");
    }

    @Test
    public void testThatQuestionMarkWorks() {
        assertThat(TemperatureFormatter.Companion.getFormattedTemp(settings, null, true)).isEqualTo("?");
    }
}
