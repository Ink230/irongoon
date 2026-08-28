package lod.irongoon.config.ui;

import lod.irongoon.config.IrongoonConfigSchema.Setting;
import legend.core.GameEngine;
import legend.core.lang.I18nText;
import legend.core.lang.RawText;
import legend.game.i18n.I18n;
import legend.game.inventory.screens.VerticalLayoutScreen;
import legend.game.inventory.screens.controls.Background;
import legend.game.inventory.screens.controls.Checkbox;
import legend.game.inventory.screens.controls.Dropdown;
import legend.lodmod.Legacy;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import static legend.game.modding.coremod.CoreMod.INPUT_ACTION_MENU_BACK;

/** Editor-local selectors for schema settings whose values are lists or registry ids. */
public final class IrongoonConfigListScreen extends VerticalLayoutScreen {
    private static final String SKIP = "skip";
    private static final int RANDOM_CHARACTER = -1;

    public IrongoonConfigListScreen(final IrongoonConfigEditorSession session, final Setting setting) {
        this.addControl(new Background());

        switch(setting.editorCategory()) {
            case BATTLE_STAGE -> this.addBattleStages(session, setting);
            case BATTLE_PARTY_CHARACTER -> this.addBattleParty(session, setting);
            case CHARACTER_ELEMENT, DRAGOON_ELEMENT -> this.addElementOverrides(session, setting);
            case SHOP_ITEM -> this.addRegistryCheckboxes(session, setting, this.itemChoices(this.strings(session, setting)));
            case SHOP_EQUIPMENT -> this.addRegistryCheckboxes(session, setting, this.equipmentChoices(this.strings(session, setting)));
            case SHOP_RECALLED -> this.addRegistryCheckboxes(session, setting, this.recalledChoices(this.strings(session, setting)));
            default -> throw new IllegalArgumentException("Unsupported Irongoon list editor " + setting.key());
        }

        this.addHotkey(new I18nText("irongoon.ui.config.back"), INPUT_ACTION_MENU_BACK, () -> this.getStack().popScreen());
    }

    private void addBattleStages(final IrongoonConfigEditorSession session, final Setting setting) {
        for(int stage = 0; stage <= 94; stage++) {
            final int selectedStage = stage;
            this.addRow(new RawText(Integer.toString(stage)), this.checkbox(this.integers(session, setting).contains(stage), checked ->
                session.update(setting.key(), this.updatedIntegers(this.integers(session, setting), selectedStage, checked))));
        }
    }

    private void addBattleParty(final IrongoonConfigEditorSession session, final Setting setting) {
        if(setting.key().equals("battlePartyPool")) {
            for(int index = 0; index < Legacy.CHAR_IDS.length; index++) {
                final int characterIndex = index;
                this.addRow(this.characterName(index), this.checkbox(this.integers(session, setting).contains(index), checked ->
                    session.update(setting.key(), this.updatedIntegers(this.integers(session, setting), characterIndex, checked))));
            }
            return;
        }

        final List<Integer> selected = this.integers(session, setting);
        final int partySize = (Integer)session.draft().values().get("battlePartySize");
        for(int slot = 0; slot < partySize; slot++) {
            final int selectedSlot = slot;
            final int current = slot < selected.size() ? selected.get(slot) : RANDOM_CHARACTER;
            final Dropdown<Integer> dropdown = new Dropdown<>((index, character) -> this.characterOptionName(character));
            dropdown.addOption(RANDOM_CHARACTER);
            for(int characterIndex = 0; characterIndex < Legacy.CHAR_IDS.length; characterIndex++) dropdown.addOption(characterIndex);
            dropdown.setSelected(current);
            final int[] accepted = {current};
            dropdown.onSelection(index -> {
                final int choice = dropdown.getSelectedOption();
                final List<Integer> updated = this.updatedPartyOverride(this.integers(session, setting), selectedSlot, choice, partySize);
                if(!(Boolean)session.draft().values().get("battlePartyDuplicates") && this.hasDuplicateCharacters(updated)) {
                    dropdown.setSelected(accepted[0]);
                    return;
                }
                session.update(setting.key(), updated);
                accepted[0] = choice;
            });
            this.addRow(new RawText(new I18nText("irongoon.ui.config.slot", slot + 1).get()), dropdown);
        }
    }

    private void addElementOverrides(final IrongoonConfigEditorSession session, final Setting setting) {
        final List<String> selected = this.strings(session, setting);
        final List<Choice> choices = this.elementChoices(selected);
        for(int characterIndex = 0; characterIndex < Legacy.CHAR_IDS.length; characterIndex++) {
            final int selectedCharacter = characterIndex;
            final String current = characterIndex < selected.size() ? this.resolveElementId(selected.get(characterIndex), choices) : SKIP;
            final Dropdown<String> dropdown = new Dropdown<>((index, id) -> this.choiceLabel(choices, id));
            for(final Choice choice : choices) dropdown.addOption(choice.id());
            dropdown.setSelected(current);
            dropdown.onSelection(index -> session.update(setting.key(), this.updatedStrings(this.strings(session, setting), selectedCharacter, dropdown.getSelectedOption(), Legacy.CHAR_IDS.length)));
            this.addRow(this.characterName(characterIndex), dropdown);
        }
    }

    private void addRegistryCheckboxes(final IrongoonConfigEditorSession session, final Setting setting, final List<Choice> choices) {
        for(final Choice choice : choices) {
            this.addRow(new RawText(choice.label()), this.checkbox(this.strings(session, setting).contains(choice.id()), checked ->
                session.update(setting.key(), this.updatedStrings(this.strings(session, setting), choice.id(), checked))));
        }
    }

    private Checkbox checkbox(final boolean checked, final BooleanConsumer onToggled) {
        final Checkbox control = new Checkbox();
        control.setChecked(checked);
        control.onToggled(onToggled);
        return control;
    }

    @SuppressWarnings("unchecked")
    private List<Integer> integers(final IrongoonConfigEditorSession session, final Setting setting) {
        return (List<Integer>)session.draft().values().get(setting.key());
    }

    @SuppressWarnings("unchecked")
    private List<String> strings(final IrongoonConfigEditorSession session, final Setting setting) {
        return (List<String>)session.draft().values().get(setting.key());
    }

    private List<Integer> updatedIntegers(final List<Integer> current, final int value, final boolean checked) {
        final LinkedHashSet<Integer> updated = new LinkedHashSet<>(current);
        if(checked) updated.add(value);
        else updated.remove(value);
        return List.copyOf(updated);
    }

    private List<Integer> updatedPartyOverride(final List<Integer> current, final int slot, final int value, final int partySize) {
        final List<Integer> updated = new ArrayList<>(current.subList(0, Math.min(current.size(), partySize)));
        while(updated.size() < partySize) updated.add(RANDOM_CHARACTER);
        updated.set(slot, value);
        return List.copyOf(updated);
    }

    private boolean hasDuplicateCharacters(final List<Integer> values) {
        final LinkedHashSet<Integer> unique = new LinkedHashSet<>();
        for(final int value : values) if(value != RANDOM_CHARACTER && !unique.add(value)) return true;
        return false;
    }

    private List<String> updatedStrings(final List<String> current, final String value, final boolean checked) {
        final LinkedHashSet<String> updated = new LinkedHashSet<>(current);
        if(checked) updated.add(value);
        else updated.remove(value);
        return List.copyOf(updated);
    }

    private List<String> updatedStrings(final List<String> current, final int index, final String value, final int size) {
        final List<String> updated = new ArrayList<>(current);
        while(updated.size() < size) updated.add(SKIP);
        updated.set(index, value);
        return List.copyOf(updated);
    }

    private RawText characterName(final int characterIndex) {
        return new RawText(I18n.translate(GameEngine.REGISTRIES.characterTemplates.getEntry(Legacy.CHAR_IDS[characterIndex])));
    }

    private String characterOptionName(final int characterIndex) {
        return characterIndex == RANDOM_CHARACTER ? new I18nText("irongoon.ui.config.random_or_skip").get() : this.characterName(characterIndex).get();
    }

    private List<Choice> elementChoices(final List<String> configured) {
        final List<Choice> choices = new ArrayList<>();
        choices.add(new Choice(SKIP, new I18nText("irongoon.ui.config.skip").get()));
        for(final RegistryId id : GameEngine.REGISTRIES.elements) {
            choices.add(new Choice(id.toString(), I18n.translate(GameEngine.REGISTRIES.elements.getEntry(id))));
        }
        for(final String id : configured) {
            if(!this.hasElementChoice(choices, id)) this.addUnavailableChoice(choices, id);
        }
        return this.sortRegistryChoices(choices);
    }

    private List<Choice> itemChoices(final List<String> configured) {
        return this.registryChoices(GameEngine.REGISTRIES.items::getEntry, GameEngine.REGISTRIES.items, configured);
    }

    private List<Choice> equipmentChoices(final List<String> configured) {
        return this.registryChoices(GameEngine.REGISTRIES.equipment::getEntry, GameEngine.REGISTRIES.equipment, configured);
    }

    private List<Choice> recalledChoices(final List<String> configured) {
        final List<Choice> choices = new ArrayList<>(this.itemChoices(List.of()));
        choices.addAll(this.equipmentChoices(List.of()));
        for(final String id : configured) {
            if(choices.stream().noneMatch(choice -> choice.id().equals(id))) this.addUnavailableChoice(choices, id);
        }
        return this.sortRegistryChoices(choices);
    }

    private List<Choice> registryChoices(final Function<RegistryId, ?> entry, final Iterable<RegistryId> ids, final List<String> configured) {
        final List<Choice> choices = new ArrayList<>();
        for(final RegistryId id : ids) choices.add(new Choice(id.toString(), I18n.translate((org.legendofdragoon.modloader.registries.RegistryDelegate<?>)entry.apply(id))));
        for(final String id : configured) {
            if(choices.stream().noneMatch(choice -> choice.id().equals(id))) this.addUnavailableChoice(choices, id);
        }
        return this.sortRegistryChoices(choices);
    }

    private void addUnavailableChoice(final List<Choice> choices, final String id) {
        choices.add(new Choice(id, I18n.translate("irongoon.ui.config.unavailable", id)));
    }

    private boolean hasElementChoice(final List<Choice> choices, final String configured) {
        if(choices.stream().anyMatch(choice -> choice.id().equals(configured))) return true;

        final String alias = configured.trim().toLowerCase(Locale.ROOT);
        return choices.stream()
            .map(Choice::id)
            .filter(id -> id.contains(":"))
            .anyMatch(id -> id.substring(id.indexOf(':') + 1).equalsIgnoreCase(alias));
    }

    private List<Choice> sortRegistryChoices(final List<Choice> choices) {
        choices.sort(Comparator.comparing(Choice::label, String.CASE_INSENSITIVE_ORDER).thenComparing(Choice::id));
        return List.copyOf(choices);
    }

    private String resolveElementId(final String configured, final List<Choice> choices) {
        if(choices.stream().anyMatch(choice -> choice.id().equals(configured))) return configured;
        final String alias = configured.trim().toLowerCase(Locale.ROOT);
        return choices.stream()
            .map(Choice::id)
            .filter(id -> id.contains(":"))
            .filter(id -> id.substring(id.indexOf(':') + 1).equalsIgnoreCase(alias))
            .findFirst()
            .orElse(configured);
    }

    private String choiceLabel(final List<Choice> choices, final String id) {
        return choices.stream().filter(choice -> choice.id().equals(id)).map(Choice::label).findFirst().orElse(id);
    }

    private record Choice(String id, String label) { }
}
