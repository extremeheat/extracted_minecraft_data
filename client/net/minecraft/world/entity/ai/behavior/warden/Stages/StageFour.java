package net.minecraft.world.entity.ai.behavior.warden.Stages;

import com.google.common.collect.ImmutableMap;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class StageFour<E extends Warden> extends Behavior<E> {
   final Pose pose;
   public boolean finished = false;

   public StageFour(int var1, Pose var2) {
      super(ImmutableMap.of(), var1);
      this.pose = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return !var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 4;
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return !var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).isPresent() && (Integer)var2.getBrain().getMemory(MemoryModuleType.ACTING_STAGE).get() == 4;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.setPose(this.pose);
      var2.lookAt(EntityAnchorArgument.Anchor.EYES, var2.position().add(-1.0, 0.0, 0.0));
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      var2.lookAt(EntityAnchorArgument.Anchor.EYES, var2.position().add(-1.0, 0.0, 0.0));
      BlockPos var5 = new BlockPos(var1.getLevel().WARDEN_ARENA_POS.offset(0, 7, -11));

      for(int var6 = 0; var6 < 21; ++var6) {
         for(int var7 = 0; var7 < 7; ++var7) {
            BlockPos var8 = var5.offset(0, var7, var6);
            var1.setBlock(var8, Blocks.RED_CONCRETE_POWDER.defaultBlockState(), 2);
         }
      }

      var2.getBrain().setMemory(MemoryModuleType.ACTING_STAGE, 5);
      var1.getLevel().playSound((Entity)null, var1.getLevel().WARDEN_ARENA_POS, SoundEvents.VILLAGER_CROWD_CHEER, SoundSource.MASTER, 10.0F, 1.0F);
      if (var1.players().size() > 0 && !this.finished) {
         this.finished = true;

         for(int var9 = 0; var9 < 200; ++var9) {
            ((ServerPlayer)var1.players().get(0)).drop(Items.ROSE_BUSH.getDefaultInstance().copyWithCount(64), true, false, false);
         }

         ((ServerPlayer)var1.players().get(0)).drop(Items.DIAMOND.getDefaultInstance().copyWithCount(5), true, false, false);
         ((ServerPlayer)var1.players().get(0)).sendSystemMessage(Component.literal("There should have been rewards, and flowers should come from the crowd, but you know... lol"), true);
      }

   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Warden)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Warden)var2, var3);
   }
}
