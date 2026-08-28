package lod.irongoon.config.ui;

import legend.core.lang.I18nText;
import legend.core.lang.RawText;
import legend.game.inventory.screens.VerticalLayoutScreen;
import legend.game.inventory.screens.controls.Button;
import legend.game.inventory.screens.controls.Checkbox;
import legend.game.inventory.screens.controls.Textbox;
import legend.game.inventory.screens.controls.Label;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;

public final class IrongoonConfigScreen extends VerticalLayoutScreen {
    public IrongoonConfigScreen(final IrongoonConfigEditorSession session) {
        final Textbox seed = new Textbox();
        seed.setText(session.draftSeed());
        seed.onChanged(session::updateSeed);
        this.addRow(new I18nText("irongoon.ui.config.root.campaign_seed"), seed);
        final Checkbox randomSeed = new Checkbox();
        randomSeed.setChecked((Boolean) session.draft().values().get("useRandomSeedOnNewCampaign"));
        randomSeed.onToggled(value -> session.update("useRandomSeedOnNewCampaign", value));
        this.addRow(new I18nText("irongoon.ui.config.root.random_seed_policy"), randomSeed);
        this.addRow(new I18nText("irongoon.ui.config.root.selected_profile"), new Label(new RawText(session.sourceProfileId())));
        final Button configure = new Button(new I18nText("irongoon.ui.config.root.configure_settings"));
        configure.onPressed(() -> configure.getScreen().getStack().pushScreen(new IrongoonConfigSectionsScreen(session)));
        this.addRow(new I18nText("irongoon.ui.config.root.configure_settings"), configure);
        this.addRow(new I18nText("irongoon.ui.config.root.config_folder"), new Label(new RawText("mods/irongoon/configs")));
        this.addHotkey(new I18nText("irongoon.ui.config.back"), INPUT_ACTION_MENU_BACK, () -> this.getStack().popScreen());
    }
}
