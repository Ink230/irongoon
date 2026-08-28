package lod.irongoon.config.ui;

import lod.irongoon.config.IrongoonConfigSchema;
import lod.irongoon.config.IrongoonConfigSchema.Lifecycle;
import lod.irongoon.config.IrongoonConfigSchema.Section;
import lod.irongoon.config.IrongoonConfigSchema.Setting;
import legend.core.lang.I18nText;
import legend.core.lang.RawText;
import legend.core.lang.TextComponent;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.Control;
import legend.game.inventory.screens.TooltipScreen;
import legend.game.inventory.screens.VerticalLayoutScreen;
import legend.game.inventory.screens.controls.Button;
import legend.game.inventory.screens.controls.Checkbox;
import legend.game.inventory.screens.controls.Dropdown;
import legend.game.inventory.screens.controls.Label;
import legend.game.inventory.screens.controls.NumberSpinner;
import legend.game.inventory.screens.controls.Textbox;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_HELP;

/** Metadata-driven scalar and selector launcher for one Irongoon configuration section. */
public final class IrongoonConfigSectionScreen extends VerticalLayoutScreen {
    private final IrongoonConfigEditorSession session;
    private final Map<Label, Setting> helpEntries = new HashMap<>();
    private final Map<String, NumberSpinner<Integer>> spinners = new HashMap<>();
    private boolean synchronizingSpinners;

    public IrongoonConfigSectionScreen(final IrongoonConfigEditorSession session, final Section section) {
        this.session = session;

        for(final Setting setting : IrongoonConfigSchema.settings()) {
            if(setting.section() != section) continue;

            final Label row = this.addRow(this.label(setting), this.control(setting));
            this.helpEntries.put(row, setting);
        }

        this.addHotkey(new I18nText("irongoon.ui.config.help"), INPUT_ACTION_MENU_HELP, this::showHelp);
        this.addHotkey(new I18nText("irongoon.ui.config.back"), INPUT_ACTION_MENU_BACK, () -> this.getStack().popScreen());
    }

    private TextComponent label(final Setting setting) {
        final String key = "irongoon.config." + setting.key() + ".label";
        if(setting.lifecycle() != Lifecycle.NEW_CAMPAIGN_ONLY && setting.lifecycle() != Lifecycle.INACTIVE) {
            return new I18nText(key);
        }

        final String lifecycleKey = "irongoon.ui.config.lifecycle." + setting.lifecycle().name();
        return new RawText(I18n.translate(key) + " [" + I18n.translate(lifecycleKey) + ']');
    }

    private Control control(final Setting setting) {
        final Object value = this.session.draft().values().get(setting.key());
        return switch(setting.control()) {
            case CHECKBOX -> this.checkbox(setting, (Boolean)value);
            case DROPDOWN -> this.dropdown(setting, (String)value);
            case TEXTBOX -> this.textbox(setting, (String)value);
            case NUMBER_SPINNER -> this.spinner(setting, (Integer)value);
            case INTEGER_LIST, STRING_LIST -> this.listHook(setting);
        };
    }

    private Checkbox checkbox(final Setting setting, final boolean value) {
        final Checkbox control = new Checkbox();
        final boolean[] reverting = {false};
        control.setChecked(value);
        control.onToggled(checked -> {
            if(reverting[0]) return;
            if(this.session.update(setting.key(), checked)) return;

            reverting[0] = true;
            control.setChecked((Boolean)this.session.draft().values().get(setting.key()));
            reverting[0] = false;
        });
        return control;
    }

    private Dropdown<String> dropdown(final Setting setting, final String value) {
        final Dropdown<String> control = new Dropdown<>((index, option) -> new I18nText("irongoon.config." + setting.key() + '.' + option).get());
        for(final String option : setting.choices()) control.addOption(option);
        control.setSelected(value);
        control.onSelection(index -> {
            if(!this.session.update(setting.key(), control.getSelectedOption())) {
                control.setSelected((String)this.session.draft().values().get(setting.key()));
            }
        });
        return control;
    }

    private Textbox textbox(final Setting setting, final String value) {
        final Textbox control = new Textbox();
        control.setMaxLength(15);
        control.setText(value);
        control.onChanged(text -> {
            if(!this.session.update(setting.key(), text)) {
                control.setText((String)this.session.draft().values().get(setting.key()));
            }
        });
        return control;
    }

    private NumberSpinner<Integer> spinner(final Setting setting, final int value) {
        final NumberSpinner<Integer> control = NumberSpinner.intSpinner(value, setting.minimum(), setting.maximum());
        this.spinners.put(setting.key(), control);
        control.onChange(number -> this.updateSpinner(setting, control, number));
        return control;
    }

    private void updateSpinner(final Setting setting, final NumberSpinner<Integer> control, final int number) {
        if(this.synchronizingSpinners) return;

        final Map<String, Object> updates = new LinkedHashMap<>();
        updates.put(setting.key(), number);

        final String pairedKey = setting.pairedKey();
        if(pairedKey != null) {
            final int pairedValue = (Integer)this.session.draft().values().get(pairedKey);
            if(setting.key().contains("Lower") && number > pairedValue || setting.key().contains("Upper") && number < pairedValue) {
                updates.put(pairedKey, number);
            }
        }

        if(!this.session.update(updates)) {
            this.synchronizeSpinner(control, (Integer)this.session.draft().values().get(setting.key()));
            return;
        }

        if(pairedKey != null && updates.containsKey(pairedKey)) {
            final NumberSpinner<Integer> paired = this.spinners.get(pairedKey);
            if(paired != null) this.synchronizeSpinner(paired, number);
        }
    }

    private void synchronizeSpinner(final NumberSpinner<Integer> control, final int value) {
        this.synchronizingSpinners = true;
        control.setNumber(value);
        this.synchronizingSpinners = false;
    }

    private Button listHook(final Setting setting) {
        final Button control = new Button(new I18nText("irongoon.ui.config.edit_list"));
        control.onPressed(() -> this.getStack().pushScreen(new IrongoonConfigListScreen(this.session, setting)));
        return control;
    }

    private void showHelp() {
        final Label row = this.getHighlightedRow();
        final Setting setting = this.helpEntries.get(row);
        if(setting == null) return;

        this.getStack().pushScreen(new TooltipScreen(
            new I18nText("irongoon.config." + setting.key() + ".help"),
            row.calculateTotalX() + row.getWidth() / 2,
            row.calculateTotalY() + row.getHeight() / 2
        ));
    }
}
