package lod.irongoon.config.ui;

import lod.irongoon.config.IrongoonConfigSchema.Section;
import legend.core.lang.I18nText;
import legend.game.inventory.screens.VerticalLayoutScreen;
import legend.game.inventory.screens.controls.Button;
import java.util.Locale;
import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;

public final class IrongoonConfigSectionsScreen extends VerticalLayoutScreen {
    public IrongoonConfigSectionsScreen(final IrongoonConfigEditorSession session) {
        for(final Section section : Section.values()) {
            final Button button = new Button(new I18nText("irongoon.ui.config.section." + section.name().toLowerCase(Locale.ROOT)));
            button.onPressed(() -> button.getScreen().getStack().pushScreen(new IrongoonConfigSectionScreen(session, section)));
            this.addRow(new I18nText("irongoon.ui.config.section." + section.name().toLowerCase(Locale.ROOT)), button);
        }
        this.addHotkey(new I18nText("irongoon.ui.config.back"), INPUT_ACTION_MENU_BACK, () -> this.getStack().popScreen());
    }
}
