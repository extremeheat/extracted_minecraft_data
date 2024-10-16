package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class InventoryChangeTrigger extends SimpleCriterionTrigger<InventoryChangeTrigger.TriggerInstance> {
   public InventoryChangeTrigger() {
      super();
   }

   @Override
   public Codec<InventoryChangeTrigger.TriggerInstance> codec() {
      return InventoryChangeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Inventory var2, ItemStack var3) {
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;

      for (int var7 = 0; var7 < var2.getContainerSize(); var7++) {
         ItemStack var8 = var2.getItem(var7);
         if (var8.isEmpty()) {
            var5++;
         } else {
            var6++;
            if (var8.getCount() >= var8.getMaxStackSize()) {
               var4++;
            }
         }
      }

      this.trigger(var1, var2, var3, var4, var5, var6);
   }

   private void trigger(ServerPlayer var1, Inventory var2, ItemStack var3, int var4, int var5, int var6) {
      this.trigger(var1, var5x -> var5x.matches(var2, var3, var4, var5, var6));
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
