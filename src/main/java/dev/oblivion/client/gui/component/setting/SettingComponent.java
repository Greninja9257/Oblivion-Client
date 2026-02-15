package dev.oblivion.client.gui.component.setting;

import dev.oblivion.client.gui.component.Component;
import dev.oblivion.client.gui.theme.Theme;
import dev.oblivion.client.setting.Setting;

public abstract class SettingComponent extends Component {
    protected final Setting<?> setting;

    protected SettingComponent(Setting<?> setting, int x, int y, int width) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = Theme.SETTING_ROW_HEIGHT;
    }

    public Setting<?> getSetting() { return setting; }
    public boolean isTextInputFocused() { return false; }

    public static SettingComponent create(Setting<?> setting, int x, int y, int width) {
        if (setting instanceof dev.oblivion.client.setting.impl.BoolSetting bs) {
            return new BoolSettingComponent(bs, x, y, width);
        } else if (setting instanceof dev.oblivion.client.setting.impl.DoubleSetting ds) {
            return new SliderComponent(ds, x, y, width);
        } else if (setting instanceof dev.oblivion.client.setting.impl.IntSetting is) {
            return new IntSliderComponent(is, x, y, width);
        } else if (setting instanceof dev.oblivion.client.setting.impl.EnumSetting<?> es) {
            return new EnumSettingComponent(es, x, y, width);
        } else if (setting instanceof dev.oblivion.client.setting.impl.ColorSetting cs) {
            return new ColorPickerComponent(cs, x, y, width);
        } else if (setting instanceof dev.oblivion.client.setting.impl.KeybindSetting ks) {
            return new KeybindComponent(ks, x, y, width);
        } else if (setting instanceof dev.oblivion.client.setting.impl.StringSetting ss) {
            return new StringSettingComponent(ss, x, y, width);
        }
        return new GenericSettingComponent(setting, x, y, width);
    }
}
