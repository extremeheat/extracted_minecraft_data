package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class FillBottleDispenseBehavior implements DispenseBehavior {
   public static final FillBottleDispenseBehavior INSTANCE = new FillBottleDispenseBehavior();
   public static final MapCodec<FillBottleDispenseBehavior> MAP_CODEC;

   private FillBottleDispenseBehavior() {
      super();
   }

   private DispenseBehavior.DispenseResult takeLiquid(final BlockSource source, final ItemStack dispensed, final ItemStack filledItemStack) {
      ServerLevel level = source.level();
      level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, source.pos());
      return DispenseBehavior.consumeWithRemainder(source, dispensed, filledItemStack);
   }

   public void playSuccessSound(final BlockSource source) {
      Vec3 pos = source.center().relative((Direction)source.state().getValue(DispenserBlock.FACING), 0.5);
      source.level().playSound((Entity)null, pos.x(), pos.y(), pos.z(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      ServerLevel level = source.level();
      BlockPos target = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
      BlockState state = level.getBlockState(target);
      if (state.is(BlockTags.BEEHIVES, (s) -> s.getBlock() instanceof BeehiveBlock) && (Integer)state.getValueOrElse(BeehiveBlock.HONEY_LEVEL, 0) >= 5) {
         ((BeehiveBlock)state.getBlock()).releaseBeesAndResetHoneyLevel(level, state, target, (Player)null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
         return this.takeLiquid(source, dispensed, new ItemStack(Items.HONEY_BOTTLE));
      } else {
         return (DispenseBehavior.DispenseResult)(level.getFluidState(target).is(FluidTags.WATER) ? this.takeLiquid(source, dispensed, PotionContents.createItemStack(Items.POTION, Potions.WATER)) : DispenseBehavior.DispenseResult.failure());
      }
   }

   public MapCodec<FillBottleDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
