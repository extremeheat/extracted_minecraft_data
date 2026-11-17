package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class ConvertToMudDispenseBehavior implements DispenseBehavior {
   public static final ConvertToMudDispenseBehavior INSTANCE = new ConvertToMudDispenseBehavior();
   public static final MapCodec<ConvertToMudDispenseBehavior> MAP_CODEC;

   private ConvertToMudDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      PotionContents potion = (PotionContents)dispensed.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      if (!potion.is(Potions.WATER)) {
         return DispenseBehavior.DispenseResult.failure();
      } else {
         ServerLevel level = source.level();
         BlockPos pos = source.pos();
         BlockPos target = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         if (!level.getBlockState(target).is(BlockTags.CONVERTIBLE_TO_MUD)) {
            return DispenseBehavior.DispenseResult.failure();
         } else {
            if (!level.isClientSide()) {
               RandomSource random = level.getRandom();

               for(int i = 0; i < 5; ++i) {
                  level.sendParticles(ParticleTypes.SPLASH, (double)pos.getX() + random.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
               }
            }

            level.playSound((Entity)null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, pos);
            level.setBlockAndUpdate(target, Blocks.MUD.defaultBlockState());
            return DispenseBehavior.consumeWithRemainder(source, dispensed, new ItemStack(Items.GLASS_BOTTLE));
         }
      }
   }

   public MapCodec<ConvertToMudDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
