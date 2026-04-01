package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class CombineIntoCopperGolemBehavior extends CombineIntoBuildableEntityBehavior<CopperGolem> {
   private CombineIntoCopperGolemBehavior(final CombineIntoBuildableEntityBehavior.BlockMatcher bodyBlockMatcher, final CombineIntoBuildableEntityBehavior.BlockMatcher headBlockMatcher, final double minDistance, final EntityType<CopperGolem> entityType) {
      super(bodyBlockMatcher, headBlockMatcher, minDistance, entityType);
   }

   public void onCreate(final LivingBlock entity, final CopperGolem builtEntity, final ServerLevel level, final List<LivingBlock> bodyBlockEntities, final List<LivingBlock> headBlockEntities) {
      BlockState copper = ((LivingBlock)bodyBlockEntities.getFirst()).getBlockState();
      WeatherInfo weatherInfo = this.getWeatheringInfoFromCopper(copper);
      LivingBlock chest = LivingBlock.create(level, (BlockState)weatherInfo.chest);
      builtEntity.setWeatherState(weatherInfo.golemState);
      if (chest != null) {
         chest.snapTo(entity.position());
         level.addFreshEntity(chest);
         ServerPlayer owner = entity.getAttributablePlayer();
         if (owner != null) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(owner, chest);
         }
      }

   }

   private WeatherInfo getWeatheringInfoFromCopper(final BlockState state) {
      Block block = state.getBlock();
      if (!block.equals(Blocks.EXPOSED_COPPER) && !block.equals(Blocks.WAXED_EXPOSED_COPPER)) {
         if (!block.equals(Blocks.WEATHERED_COPPER) && !block.equals(Blocks.WAXED_WEATHERED_COPPER)) {
            return !block.equals(Blocks.OXIDIZED_COPPER) && !block.equals(Blocks.WAXED_OXIDIZED_COPPER) ? new WeatherInfo(Blocks.COPPER_CHEST.defaultBlockState(), WeatheringCopper.WeatherState.UNAFFECTED) : new WeatherInfo(Blocks.OXIDIZED_COPPER_CHEST.defaultBlockState(), WeatheringCopper.WeatherState.OXIDIZED);
         } else {
            return new WeatherInfo(Blocks.WEATHERED_COPPER_CHEST.defaultBlockState(), WeatheringCopper.WeatherState.WEATHERED);
         }
      } else {
         return new WeatherInfo(Blocks.EXPOSED_COPPER_CHEST.defaultBlockState(), WeatheringCopper.WeatherState.EXPOSED);
      }
   }

   public static LivingBlockBehaviorType combineIntoCopperGolem(final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineIntoCopperGolemBehavior(new CombineIntoBuildableEntityBehavior.BlockMatcher((state) -> state.is(BlockTags.COPPER), 1), new CombineIntoBuildableEntityBehavior.BlockMatcher((state) -> state.is(Blocks.CARVED_PUMPKIN), 1), minDistance, EntityType.COPPER_GOLEM)));
   }

   static record WeatherInfo(BlockState chest, WeatheringCopper.WeatherState golemState) {
      WeatherInfo {
         super();
      }
   }
}
