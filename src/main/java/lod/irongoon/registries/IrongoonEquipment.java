package lod.irongoon.registries;

import legend.core.GameEngine;
import legend.game.characters.ElementSet;
import legend.game.inventory.Equipment;
import legend.game.inventory.EquipmentRegistryEvent;
import legend.game.inventory.ItemIcon;
import legend.game.types.EquipmentSlot;
import legend.lodmod.LodMod;
import lod.irongoon.Irongoon;
import org.legendofdragoon.modloader.registries.Registrar;
import org.legendofdragoon.modloader.registries.RegistryDelegate;

public final class IrongoonEquipment {
    private IrongoonEquipment() {};

    private static final Registrar<Equipment, EquipmentRegistryEvent> EQUIPMENT_REGISTRAR = new Registrar<>(GameEngine.REGISTRIES.equipment, Irongoon.MOD_ID);

    public static final RegistryDelegate<Equipment> SPEAR = EQUIPMENT_REGISTRAR.register("spear", () -> new Equipment(10, 0x0, EquipmentSlot.WEAPON, 0x80, 0x40, LodMod.NO_ELEMENT.get(), 0, new ElementSet(), new ElementSet(), 0x0, 0, 2, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, ItemIcon.SPEAR, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x0));
    public static final RegistryDelegate<Equipment> SHORT_BOW = EQUIPMENT_REGISTRAR.register("short_bow", () -> new Equipment(10, 0x0, EquipmentSlot.WEAPON, 0x80, 0x2, LodMod.NO_ELEMENT.get(), 0, new ElementSet(), new ElementSet(), 0x0, 0, 2, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, ItemIcon.BOW, 0, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0x0));
    public static final RegistryDelegate<Equipment> RAPIER = EQUIPMENT_REGISTRAR.register("rapier", () -> new Equipment(10, 0x0, EquipmentSlot.WEAPON, 0x80, 0x4, LodMod.NO_ELEMENT.get(), 0, new ElementSet(), new ElementSet(), 0x0, 0, 2, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, ItemIcon.SWORD, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x0));
    public static final RegistryDelegate<Equipment> MACE = EQUIPMENT_REGISTRAR.register("mace", () -> new Equipment(10, 0x0, EquipmentSlot.WEAPON, 0x80, 0x1, LodMod.NO_ELEMENT.get(), 0, new ElementSet(), new ElementSet(), 0x0, 0, 2, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, ItemIcon.MACE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x0));
    public static final RegistryDelegate<Equipment> IRON_KNUCKLE = EQUIPMENT_REGISTRAR.register("iron_knuckle", () -> new Equipment(10, 0x0, EquipmentSlot.WEAPON, 0x80, 0x10, LodMod.NO_ELEMENT.get(), 0, new ElementSet(), new ElementSet(), 0x0, 0, 2, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, ItemIcon.KNUCKLE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x0));
    public static final RegistryDelegate<Equipment> BROAD_SWORD = EQUIPMENT_REGISTRAR.register("broad_sword", () -> new Equipment(10, 0x0, EquipmentSlot.WEAPON, 0x80, 0x80, LodMod.NO_ELEMENT.get(), 0, new ElementSet(), new ElementSet(), 0x0, 0, 2, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, ItemIcon.SWORD, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x0));
    public static final RegistryDelegate<Equipment> AXE = EQUIPMENT_REGISTRAR.register("axe", () -> new Equipment(10, 0x0, EquipmentSlot.WEAPON, 0x80, 0x20, LodMod.NO_ELEMENT.get(), 0, new ElementSet(), new ElementSet(), 0x0, 0, 2, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, 0, 0, 0, 0, 0, ItemIcon.AXE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x0));

    public static void register(final EquipmentRegistryEvent event) {
        EQUIPMENT_REGISTRAR.registryEvent(event);
    }
}
