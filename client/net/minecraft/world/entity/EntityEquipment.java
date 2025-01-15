package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.EnumMap;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;

public class EntityEquipment {
   public static final Codec<EntityEquipment> CODEC;
   private final EnumMap<EquipmentSlot, ItemStack> items;

   private EntityEquipment(EnumMap<EquipmentSlot, ItemStack> var1) {
      super();
      this.items = var1;
   }

   public EntityEquipment() {
      this(new EnumMap(EquipmentSlot.class));
   }

   public ItemStack set(EquipmentSlot var1, ItemStack var2) {
      return (ItemStack)Objects.requireNonNullElse((ItemStack)this.items.put(var1, var2), ItemStack.EMPTY);
   }

   public ItemStack get(EquipmentSlot var1) {
      return (ItemStack)this.items.getOrDefault(var1, ItemStack.EMPTY);
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.items.values()) {
         if (!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   static {
      CODEC = Codec.unboundedMap(EquipmentSlot.CODEC, ItemStack.CODEC).xmap((var0) -> {
         EnumMap var1 = new EnumMap(EquipmentSlot.class);
         var1.putAll(var0);
         return new EntityEquipment(var1);
      }, (var0) -> {
         EnumMap var1 = new EnumMap(var0.items);
         var1.values().removeIf(ItemStack::isEmpty);
         return var1;
      });
   }
}
