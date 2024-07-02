package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AnyBlockInteractionTrigger extends SimpleCriterionTrigger<AnyBlockInteractionTrigger.TriggerInstance> {
   public AnyBlockInteractionTrigger() {
      super();
   }

   @Override
   public Codec<AnyBlockInteractionTrigger.TriggerInstance> codec() {
      return AnyBlockInteractionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      ServerLevel var4 = var1.serverLevel();
      BlockState var5 = var4.getBlockState(var2);
      LootParams var6 = new LootParams.Builder(var4)
         .withParameter(LootContextParams.ORIGIN, var2.getCenter())
         .withParameter(LootContextParams.THIS_ENTITY, var1)
         .withParameter(LootContextParams.BLOCK_STATE, var5)
         .withParameter(LootContextParams.TOOL, var3)
         .create(LootContextParamSets.ADVANCEMENT_LOCATION);
      LootContext var7 = new LootContext.Builder(var6).create(Optional.empty());
      this.trigger(var1, var1x -> var1x.matches(var7));
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
