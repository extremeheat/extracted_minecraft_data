package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;

public record SlotsPredicate(Map<SlotRange, ItemPredicate> slots) {
   public static final Codec<SlotsPredicate> CODEC;

   public SlotsPredicate(Map<SlotRange, ItemPredicate> var1) {
      super();
      this.slots = var1;
   }

   public boolean matches(Entity var1) {
      Iterator var2 = this.slots.entrySet().iterator();

      Map.Entry var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Map.Entry)var2.next();
      } while(matchSlots(var1, (ItemPredicate)var3.getValue(), ((SlotRange)var3.getKey()).slots()));

      return false;
   }

   private static boolean matchSlots(Entity var0, ItemPredicate var1, IntList var2) {
      for(int var3 = 0; var3 < var2.size(); ++var3) {
         int var4 = var2.getInt(var3);
         SlotAccess var5 = var0.getSlot(var4);
         if (var1.test(var5.get())) {
            return true;
         }
      }

      return false;
   }

   public Map<SlotRange, ItemPredicate> slots() {
      return this.slots;
   }

   static {
      CODEC = Codec.unboundedMap(SlotRanges.CODEC, ItemPredicate.CODEC).xmap(SlotsPredicate::new, SlotsPredicate::slots);
   }
}
