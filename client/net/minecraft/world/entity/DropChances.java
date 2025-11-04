package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;

public record DropChances(Map<EquipmentSlot, Float> byEquipment) {
   public static final float DEFAULT_EQUIPMENT_DROP_CHANCE = 0.085F;
   public static final float PRESERVE_ITEM_DROP_CHANCE_THRESHOLD = 1.0F;
   public static final int PRESERVE_ITEM_DROP_CHANCE = 2;
   public static final DropChances DEFAULT = new DropChances(Util.makeEnumMap(EquipmentSlot.class, (var0) -> 0.085F));
   public static final Codec<DropChances> CODEC;

   public DropChances(Map<EquipmentSlot, Float> var1) {
      super();
      this.byEquipment = var1;
   }

   private static Map<EquipmentSlot, Float> filterDefaultValues(Map<EquipmentSlot, Float> var0) {
      HashMap var1 = new HashMap(var0);
      var1.values().removeIf((var0x) -> var0x == 0.085F);
      return var1;
   }

   private static Map<EquipmentSlot, Float> toEnumMap(Map<EquipmentSlot, Float> var0) {
      return Util.<EquipmentSlot, Float>makeEnumMap(EquipmentSlot.class, (var1) -> (Float)var0.getOrDefault(var1, 0.085F));
   }

   public DropChances withGuaranteedDrop(EquipmentSlot var1) {
      return this.withEquipmentChance(var1, 2.0F);
   }

   public DropChances withEquipmentChance(EquipmentSlot var1, float var2) {
      if (var2 < 0.0F) {
         throw new IllegalArgumentException("Tried to set invalid equipment chance " + var2 + " for " + String.valueOf(var1));
      } else {
         return this.byEquipment(var1) == var2 ? this : new DropChances(Util.makeEnumMap(EquipmentSlot.class, (var3) -> var3 == var1 ? var2 : this.byEquipment(var3)));
      }
   }

   public float byEquipment(EquipmentSlot var1) {
      return (Float)this.byEquipment.getOrDefault(var1, 0.085F);
   }

   public boolean isPreserved(EquipmentSlot var1) {
      return this.byEquipment(var1) > 1.0F;
   }

   static {
      CODEC = Codec.unboundedMap(EquipmentSlot.CODEC, ExtraCodecs.NON_NEGATIVE_FLOAT).xmap(DropChances::toEnumMap, DropChances::filterDefaultValues).xmap(DropChances::new, DropChances::byEquipment);
   }
}
