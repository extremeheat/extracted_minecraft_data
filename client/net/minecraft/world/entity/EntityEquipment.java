package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.EnumMap;
import java.util.Map;
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
      var2.getItem().verifyComponentsAfterLoad(var2);
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

   public void tick(Entity var1) {
      for(Map.Entry var3 : this.items.entrySet()) {
         ItemStack var4 = (ItemStack)var3.getValue();
         if (!var4.isEmpty()) {
            var4.inventoryTick(var1.level(), var1, (EquipmentSlot)var3.getKey());
         }
      }

   }

   public void setAll(EntityEquipment var1) {
      this.items.clear();
      this.items.putAll(var1.items);
   }

   public void dropAll(LivingEntity var1) {
      for(ItemStack var3 : this.items.values()) {
         var1.drop(var3, true, false, true);
      }

      this.clear();
   }

   public void clear() {
      this.items.replaceAll((var0, var1) -> ItemStack.EMPTY);
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
