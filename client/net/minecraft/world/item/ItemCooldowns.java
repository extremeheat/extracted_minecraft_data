package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.Mth;

public class ItemCooldowns {
   private final Map<Item, ItemCooldowns.CooldownInstance> cooldowns = Maps.newHashMap();
   private int tickCount;

   public ItemCooldowns() {
      super();
   }

   public boolean isOnCooldown(Item var1) {
      return this.getCooldownPercent(var1, 0.0F) > 0.0F;
   }

   public float getCooldownPercent(Item var1, float var2) {
      ItemCooldowns.CooldownInstance var3 = (ItemCooldowns.CooldownInstance)this.cooldowns.get(var1);
      if (var3 != null) {
         float var4 = (float)(var3.endTime - var3.startTime);
         float var5 = (float)var3.endTime - ((float)this.tickCount + var2);
         return Mth.clamp(var5 / var4, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void tick() {
      ++this.tickCount;
      if (!this.cooldowns.isEmpty()) {
         Iterator var1 = this.cooldowns.entrySet().iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            if (((ItemCooldowns.CooldownInstance)var2.getValue()).endTime <= this.tickCount) {
               var1.remove();
               this.onCooldownEnded((Item)var2.getKey());
            }
         }
      }

   }

   public void addCooldown(Item var1, int var2) {
      this.cooldowns.put(var1, new ItemCooldowns.CooldownInstance(this.tickCount, this.tickCount + var2));
      this.onCooldownStarted(var1, var2);
   }

   public void removeCooldown(Item var1) {
      this.cooldowns.remove(var1);
      this.onCooldownEnded(var1);
   }

   protected void onCooldownStarted(Item var1, int var2) {
   }

   protected void onCooldownEnded(Item var1) {
   }

   private static class CooldownInstance {
      final int startTime;
      final int endTime;

      CooldownInstance(int var1, int var2) {
         super();
         this.startTime = var1;
         this.endTime = var2;
      }
   }
}
