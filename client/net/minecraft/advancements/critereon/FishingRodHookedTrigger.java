package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.Collection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger<FishingRodHookedTrigger.TriggerInstance> {
   public FishingRodHookedTrigger() {
      super();
   }

   @Override
   public Codec<FishingRodHookedTrigger.TriggerInstance> codec() {
      return FishingRodHookedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection<ItemStack> var4) {
      LootContext var5 = EntityPredicate.createContext(var1, (Entity)(var3.getHookedIn() != null ? var3.getHookedIn() : var3));
      this.trigger(var1, var3x -> var3x.matches(var2, var5, var4));
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
