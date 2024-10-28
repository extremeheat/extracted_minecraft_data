package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.component.UseCooldown;

public class ItemCooldowns {
   private final Map<ResourceLocation, CooldownInstance> cooldowns = Maps.newHashMap();
   private int tickCount;

   public ItemCooldowns() {
      super();
   }

   public boolean isOnCooldown(ItemStack var1) {
      return this.getCooldownPercent(var1, 0.0F) > 0.0F;
   }

   public float getCooldownPercent(ItemStack var1, float var2) {
      ResourceLocation var3 = this.getCooldownGroup(var1);
      CooldownInstance var4 = (CooldownInstance)this.cooldowns.get(var3);
      if (var4 != null) {
         float var5 = (float)(var4.endTime - var4.startTime);
         float var6 = (float)var4.endTime - ((float)this.tickCount + var2);
         return Mth.clamp(var6 / var5, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void tick() {
      ++this.tickCount;
      if (!this.cooldowns.isEmpty()) {
         Iterator var1 = this.cooldowns.entrySet().iterator();

         while(var1.hasNext()) {
            Map.Entry var2 = (Map.Entry)var1.next();
            if (((CooldownInstance)var2.getValue()).endTime <= this.tickCount) {
               var1.remove();
               this.onCooldownEnded((ResourceLocation)var2.getKey());
            }
         }
      }

   }

   public ResourceLocation getCooldownGroup(ItemStack var1) {
      UseCooldown var2 = (UseCooldown)var1.get(DataComponents.USE_COOLDOWN);
      ResourceLocation var3 = BuiltInRegistries.ITEM.getKey(var1.getItem());
      return var2 == null ? var3 : (ResourceLocation)var2.cooldownGroup().orElse(var3);
   }

   public void addCooldown(ItemStack var1, int var2) {
      this.addCooldown(this.getCooldownGroup(var1), var2);
   }

   public void addCooldown(ResourceLocation var1, int var2) {
      this.cooldowns.put(var1, new CooldownInstance(this.tickCount, this.tickCount + var2));
      this.onCooldownStarted(var1, var2);
   }

   public void removeCooldown(ResourceLocation var1) {
      this.cooldowns.remove(var1);
      this.onCooldownEnded(var1);
   }

   protected void onCooldownStarted(ResourceLocation var1, int var2) {
   }

   protected void onCooldownEnded(ResourceLocation var1) {
   }

   private static record CooldownInstance(int startTime, int endTime) {
      final int startTime;
      final int endTime;

      CooldownInstance(int var1, int var2) {
         super();
         this.startTime = var1;
         this.endTime = var2;
      }

      public int startTime() {
         return this.startTime;
      }

      public int endTime() {
         return this.endTime;
      }
   }
}
