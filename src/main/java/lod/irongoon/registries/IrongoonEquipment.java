package lod.irongoon.registries;

import legend.core.GameEngine;
import legend.game.characters.ElementSet;
import legend.game.inventory.Equipment;
import legend.game.inventory.EquipmentTypes;
import legend.game.inventory.EquipmentRegistryEvent;
import legend.game.inventory.GatherEquipmentTypesEvent;
import legend.game.inventory.ItemIcon;
import legend.game.types.EquipmentSlot;
import legend.lodmod.LodMod;
import lod.irongoon.Irongoon;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

public final class IrongoonEquipment {
    private IrongoonEquipment() {};

    private static final Registrar<Equipment, EquipmentRegistryEvent> EQUIPMENT_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.equipment, Irongoon.MOD_ID);

    public static final RegistryDelegate<Equipment> SPEAR = EQUIPMENT_REGISTRAR.register("spear", () -> makeWeapon(ItemIcon.SPEAR, 0));
    public static final RegistryDelegate<Equipment> SHORT_BOW = EQUIPMENT_REGISTRAR.register("short_bow", () -> makeWeapon(ItemIcon.BOW, 20));
    public static final RegistryDelegate<Equipment> RAPIER = EQUIPMENT_REGISTRAR.register("rapier", () -> makeWeapon(ItemIcon.SWORD, 0));
    public static final RegistryDelegate<Equipment> MACE = EQUIPMENT_REGISTRAR.register("mace", () -> makeWeapon(ItemIcon.MACE, 0));
    public static final RegistryDelegate<Equipment> IRON_KNUCKLE = EQUIPMENT_REGISTRAR.register("iron_knuckle", () -> makeWeapon(ItemIcon.KNUCKLE, 0));
    public static final RegistryDelegate<Equipment> BROAD_SWORD = EQUIPMENT_REGISTRAR.register("broad_sword", () -> makeWeapon(ItemIcon.SWORD, 0));
    public static final RegistryDelegate<Equipment> AXE = EQUIPMENT_REGISTRAR.register("axe", () -> makeWeapon(ItemIcon.AXE, 0));

    private static Equipment makeWeapon(final ItemIcon icon, final int attackHit) {
        return new Equipment(10, 0, EquipmentSlot.WEAPON, LodMod.NO_ELEMENT.get(), new ElementSet(), new ElementSet(), 0,
                0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, icon,
                0, 2, 0, 0, 0, attackHit, 0, 0, 0, 0, 0);
    }

    public static void register(final EquipmentRegistryEvent event) {
        EQUIPMENT_REGISTRAR.registryEvent(event);
    }

    public static void registerEquipmentTypes(final GatherEquipmentTypesEvent event) {
        event.add(SPEAR.get(), EquipmentTypes.POLEARM);
        event.add(SHORT_BOW.get(), EquipmentTypes.BOW);
        event.add(RAPIER.get(), EquipmentTypes.SHORTSWORD);
        event.add(MACE.get(), EquipmentTypes.HAMMER);
        event.add(IRON_KNUCKLE.get(), EquipmentTypes.HAND);
        event.add(BROAD_SWORD.get(), EquipmentTypes.LONGSWORD);
        event.add(AXE.get(), EquipmentTypes.AXE);
    }
}
