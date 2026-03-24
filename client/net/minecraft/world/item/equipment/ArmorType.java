package net.minecraft.world.item.equipment;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;

public enum ArmorType implements StringRepresentable {
   HELMET(EquipmentSlot.HEAD, 11, "helmet"),
   CHESTPLATE(EquipmentSlot.CHEST, 16, "chestplate"),
   LEGGINGS(EquipmentSlot.LEGS, 15, "leggings"),
   BOOTS(EquipmentSlot.FEET, 13, "boots"),
   BODY(EquipmentSlot.BODY, 16, "body");

   public static final Codec<ArmorType> CODEC = StringRepresentable.<ArmorType>fromValues(ArmorType::values);
   private final EquipmentSlot slot;
   private final String name;
   private final int unitDurability;

   private ArmorType(final EquipmentSlot slot, final int unitDurability, final String name) {
      this.slot = slot;
      this.name = name;
      this.unitDurability = unitDurability;
   }

   public int getDurability(final int multiplier) {
      return this.unitDurability * multiplier;
   }

   public EquipmentSlot getSlot() {
      return this.slot;
   }

   public String getName() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static ArmorType[] $values() {
      return new ArmorType[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS, BODY};
   }
}
