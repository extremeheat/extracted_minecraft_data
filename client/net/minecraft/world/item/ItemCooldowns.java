package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.component.UseCooldown;

public class ItemCooldowns {
   private final Map<ResourceLocation, ItemCooldowns.CooldownInstance> cooldowns = Maps.newHashMap();
   private int tickCount;

   public ItemCooldowns() {
      super();
   }

   public boolean isOnCooldown(ItemStack var1) {
      return this.getCooldownPercent(var1, 0.0F) > 0.0F;
   }

   public float getCooldownPercent(ItemStack var1, float var2) {
      ResourceLocation var3 = this.getCooldownGroup(var1);
      ItemCooldowns.CooldownInstance var4 = this.cooldowns.get(var3);
      if (var4 != null) {
         float var5 = (float)(var4.endTime - var4.startTime);
         float var6 = (float)var4.endTime - ((float)this.tickCount + var2);
         return Mth.clamp(var6 / var5, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void tick() {
      this.tickCount++;
      if (!this.cooldowns.isEmpty()) {
         Iterator var1 = this.cooldowns.entrySet().iterator();

         while (var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            if (((ItemCooldowns.CooldownInstance)var2.getValue()).endTime <= this.tickCount) {
               var1.remove();
               this.onCooldownEnded((ResourceLocation)var2.getKey());
            }
         }
      }
   }

   public ResourceLocation getCooldownGroup(ItemStack var1) {
      UseCooldown var2 = var1.get(DataComponents.USE_COOLDOWN);
      ResourceLocation var3 = BuiltInRegistries.ITEM.getKey(var1.getItem());
      return var2 == null ? var3 : var2.cooldownGroup().orElse(var3);
   }

   public void addCooldown(ItemStack var1, int var2) {
      this.addCooldown(this.getCooldownGroup(var1), var2);
   }

   public void addCooldown(ResourceLocation var1, int var2) {
      this.cooldowns.put(var1, new ItemCooldowns.CooldownInstance(this.tickCount, this.tickCount + var2));
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
