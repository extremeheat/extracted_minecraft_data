package net.minecraft.core.dispenser;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior extends OptionalDispenseItemBehavior {
   public ShearsDispenseItemBehavior() {
      super();
   }

   protected ItemStack execute(BlockSource var1, ItemStack var2) {
      ServerLevel var3 = var1.getLevel();
      if (!var3.isClientSide()) {
         BlockPos var4 = var1.getPos().relative((Direction)var1.getBlockState().getValue(DispenserBlock.FACING));
         this.setSuccess(tryShearBeehive((ServerLevel)var3, var4) || tryShearLivingEntity((ServerLevel)var3, var4));
         if (this.isSuccess() && var2.hurt(1, var3.getRandom(), (ServerPlayer)null)) {
            var2.setCount(0);
         }
      }

      return var2;
   }

   private static boolean tryShearBeehive(ServerLevel var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.is(BlockTags.BEEHIVES)) {
         int var3 = (Integer)var2.getValue(BeehiveBlock.HONEY_LEVEL);
         if (var3 >= 5) {
            var0.playSound((Player)null, var1, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            BeehiveBlock.dropHoneycomb(var0, var1);
            ((BeehiveBlock)var2.getBlock()).releaseBeesAndResetHoneyLevel(var0, var2, var1, (Player)null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
            return true;
         }
      }

      return false;
   }

   private static boolean tryShearLivingEntity(ServerLevel var0, BlockPos var1) {
      List var2 = var0.getEntitiesOfClass(LivingEntity.class, new AABB(var1), EntitySelector.NO_SPECTATORS);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         LivingEntity var4 = (LivingEntity)var3.next();
         if (var4 instanceof Shearable) {
            Shearable var5 = (Shearable)var4;
            if (var5.readyForShearing()) {
               var5.shear(SoundSource.BLOCKS);
               return true;
            }
         }
      }

      return false;
   }
}
