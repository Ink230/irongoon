package lod.irongoon.config.ui;

import lod.irongoon.config.IrongoonConfigProfile;
import lod.irongoon.config.IrongoonConfigProfiles;
import legend.core.lang.I18nText;
import legend.core.lang.RawText;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.InputBoxScreen;
import legend.game.inventory.screens.MessageBoxScreen;
import legend.game.inventory.screens.VerticalLayoutScreen;
import legend.game.inventory.screens.controls.Button;
import legend.game.inventory.screens.controls.Checkbox;
import legend.game.inventory.screens.controls.Dropdown;
import legend.game.inventory.screens.controls.Label;
import legend.game.inventory.screens.controls.Textbox;
import legend.game.types.MessageBoxResult;
import legend.game.types.MessageBoxType;

import java.nio.file.Path;
import java.util.Optional;

import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;

/** Campaign-local profile editor root. File writes and campaign staging are explicit session operations. */
public final class IrongoonConfigScreen extends VerticalLayoutScreen {
    private final IrongoonConfigEditorSession session;
    private Label dirtyFeedback;
    private Label stagedFeedback;
    private Label validationFeedback;
    private Label operationFeedback;

    public IrongoonConfigScreen(final IrongoonConfigEditorSession session) {
        this.session = session;

        final Textbox seed = new Textbox();
        seed.setMaxLength(15);
        seed.setText(session.draftSeed());
        seed.onChanged(session::updateSeed);
        this.addRow(new I18nText("irongoon.ui.config.root.campaign_seed"), seed);

        final Checkbox randomSeed = new Checkbox();
        randomSeed.setChecked((Boolean)session.draft().values().get("useRandomSeedOnNewCampaign"));
        randomSeed.onToggled(value -> session.update("useRandomSeedOnNewCampaign", value));
        this.addRow(new I18nText("irongoon.ui.config.root.random_seed_policy"), randomSeed);

        this.addProfileDropdown();
        this.addConfigureButton();
        this.addWorkflowButtons();
        this.addFeedback();
        this.addFolderControls();
        this.addHotkey(new I18nText("irongoon.ui.config.back"), INPUT_ACTION_MENU_BACK, this::back);
    }

    private void addProfileDropdown() {
        final Dropdown<IrongoonConfigProfile> profiles = new Dropdown<>((index, profile) -> profile.displayName());
        for(final IrongoonConfigProfile profile : this.session.availableProfiles()) profiles.addOption(profile);

        final Optional<IrongoonConfigProfile> selected = this.session.availableProfiles().stream()
            .filter(profile -> profile.filename().equalsIgnoreCase(this.session.sourceProfileId()))
            .findFirst();
        if(selected.isPresent()) {
            profiles.setSelected(selected.get());
        } else {
            profiles.setSelectedIndex(-1);
        }

        profiles.onSelection(index -> this.selectProfile(profiles.getSelectedOption()));
        this.addRow(new I18nText("irongoon.ui.config.root.selected_profile"), profiles);

        if(selected.isEmpty()) {
            this.addRow(new I18nText("irongoon.ui.config.root.profile_snapshot", this.session.sourceProfileId()), null);
        }
    }

    private void selectProfile(final IrongoonConfigProfile profile) {
        if(profile == null) return;
        if(!this.session.dirty()) {
            this.session.selectProfile(profile.filename());
            this.refresh();
            return;
        }

        this.getStack().pushScreen(new MessageBoxScreen(
            I18n.translate("irongoon.ui.config.root.discard_confirm"),
            MessageBoxType.CONFIRMATION,
            result -> {
                if(result == MessageBoxResult.YES) this.session.selectProfile(profile.filename());
                this.refresh();
            }
        ));
    }

    private void addConfigureButton() {
        final Button configure = new Button(new I18nText("irongoon.ui.config.root.configure_settings"));
        configure.onPressed(() -> this.getStack().pushScreen(new IrongoonConfigSectionsScreen(this.session)));
        this.addRow(new I18nText("irongoon.ui.config.root.configure_settings"), configure);
    }

    private void addWorkflowButtons() {
        final Button saveExisting = new Button(new I18nText("irongoon.ui.config.root.save_existing"));
        saveExisting.onPressed(this::saveExisting);
        this.addRow(new I18nText("irongoon.ui.config.root.save_existing"), saveExisting);

        final Button saveAs = new Button(new I18nText("irongoon.ui.config.root.save_as_new"));
        saveAs.onPressed(this::saveAs);
        this.addRow(new I18nText("irongoon.ui.config.root.save_as_new"), saveAs);

        final Button rename = new Button(new I18nText("irongoon.ui.config.root.rename"));
        rename.onPressed(this::rename);
        this.addRow(new I18nText("irongoon.ui.config.root.rename"), rename);

        final Button rescan = new Button(new I18nText("irongoon.ui.config.root.rescan_profiles"));
        rescan.onPressed(() -> {
            this.session.rescan();
            this.refresh();
        });
        this.addRow(new I18nText("irongoon.ui.config.root.rescan_profiles"), rescan);
    }

    private void addFeedback() {
        this.dirtyFeedback = this.addRow(RawText.BLANK, null);
        this.stagedFeedback = this.addRow(RawText.BLANK, null);
        this.validationFeedback = this.addRow(RawText.BLANK, null);
        this.operationFeedback = this.addRow(RawText.BLANK, null);
        this.updateFeedback();
    }

    private void updateFeedback() {
        this.dirtyFeedback.setText(new I18nText(this.session.dirty()
            ? "irongoon.ui.config.root.dirty"
            : "irongoon.ui.config.root.clean"));
        this.stagedFeedback.setText(this.session.stagedForReload()
            ? new I18nText("irongoon.ui.config.root.staged")
            : RawText.BLANK);
        this.validationFeedback.setText(this.session.validationError() == null
            ? RawText.BLANK
            : new RawText(I18n.translate("irongoon.ui.config.root.validation_error") + ": " + this.session.validationError()));
        this.operationFeedback.setText(this.session.operationError() == null
            ? RawText.BLANK
            : new RawText(I18n.translate("irongoon.ui.config.root.operation_error") + ": " + this.session.operationError()));
    }

    private void addFolderControls() {
        final Path directory = IrongoonConfigProfiles.getInstance().configsDirectory();
        this.addRow(new RawText(I18n.translate("irongoon.ui.config.root.config_folder") + ": " + directory), null);

        final Button openFolder = new Button(new I18nText("irongoon.ui.config.root.open_folder"));
        final boolean canOpen = IrongoonConfigFolder.canOpen();
        openFolder.setDisabled(!canOpen);
        openFolder.onPressed(() -> {
            try {
                IrongoonConfigFolder.open(directory);
            } catch(final RuntimeException exception) {
                this.session.setOperationError(exception.getMessage());
            }
        });
        this.addRow(new I18nText("irongoon.ui.config.root.open_folder"), openFolder);
        if(!canOpen) this.addRow(new I18nText("irongoon.ui.config.root.folder_unavailable"), null);
    }

    private void saveExisting() {
        if(this.session.selectedProfileKind() == IrongoonConfigProfile.Kind.PROFILE) {
            this.getStack().pushScreen(new MessageBoxScreen(
                I18n.translate("irongoon.ui.config.root.overwrite_confirm"),
                MessageBoxType.CONFIRMATION,
                result -> {
                    if(result == MessageBoxResult.YES) this.session.saveExisting();
                    this.refresh();
                }
            ));
            return;
        }

        this.session.saveExisting();
        this.refresh();
    }

    private void saveAs() {
        this.getStack().pushScreen(new InputBoxScreen(
            new I18nText("irongoon.ui.config.root.save_as_prompt"),
            "",
            (result, name) -> {
                if(result == MessageBoxResult.YES) {
                    this.session.saveAs(name);
                    this.refresh();
                }
            }
        ));
    }

    private void rename() {
        if(this.session.selectedProfileKind() == IrongoonConfigProfile.Kind.BLUEPRINT) {
            this.session.rename("");
            this.refresh();
            return;
        }

        this.getStack().pushScreen(new InputBoxScreen(
            new I18nText("irongoon.ui.config.root.rename_prompt"),
            this.session.selectedProfileDisplayName(),
            (result, name) -> {
                if(result == MessageBoxResult.YES) {
                    this.session.rename(name);
                    this.refresh();
                }
            }
        ));
    }

    private void back() {
        if(!this.session.dirty()) {
            this.getStack().popScreen();
            return;
        }

        this.getStack().pushScreen(new MessageBoxScreen(
            I18n.translate("irongoon.ui.config.root.discard_confirm"),
            MessageBoxType.CONFIRMATION,
            result -> {
                if(result == MessageBoxResult.YES) this.getStack().popScreen();
            }
        ));
    }

    private void refresh() {
        this.deferAction(() -> {
            this.getStack().popScreen();
            this.getStack().pushScreen(new IrongoonConfigScreen(this.session));
        });
    }

    @Override
    protected void render() {
        this.updateFeedback();
    }
}
