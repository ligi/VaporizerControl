import org.junit.Test;
import org.ligi.vaporizercontrol.model.CRAFTY_UUIDS;
import static org.assertj.core.api.Assertions.assertThat;

public class TheCraftyUUIDs {

    @Test
    public void allUUIDsShouldHaveSameLength() {
        assertThat(CRAFTY_UUIDS.DATA_SERVICE_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.META_DATA_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.MISC_DATA_UUID.length()).isEqualTo(36);

        assertThat(CRAFTY_UUIDS.BATTERY_CHARACTERISTIC_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.TEMPERATURE_BOOST_CHARACTERISTIC_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.TEMPERATURE_BOOST_CHARACTERISTIC_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.TEMPERATURE_SETPOINT_CHARACTERISTIC_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.LED_CHARACTERISTIC_UUID.length()).isEqualTo(36);

        assertThat(CRAFTY_UUIDS.VERSION_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.HOURS_OF_OP_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.MODEL_UUID.length()).isEqualTo(36);
        assertThat(CRAFTY_UUIDS.SERIAL_UUID.length()).isEqualTo(36);

    }

    @Test
    public void testSampleUUID() {
        assertThat(CRAFTY_UUIDS.DATA_SERVICE_UUID).isEqualTo("00000001-4c45-4b43-4942-265a524f5453");
    }

}
