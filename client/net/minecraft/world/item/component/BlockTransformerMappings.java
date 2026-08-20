package net.minecraft.world.item.component;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.Directional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BlockTransformerMappings {
   public static final BlockTransformer SHOVEL;
   private static final BlockTransformer.BlockTransformData AXE_STRIPPABLES;
   public static final BlockTransformer AXE;
   private static final BlockTransformer.BlockTransformData HOE_DEFAULT;
   private static final BlockTransformer.BlockTransformData HOE_ROOTED_DIRT;
   public static BlockTransformer HOE;

   public BlockTransformerMappings() {
      super();
   }

   private static List<BlockTransformer.BlockTransformData> axe(final Set<Map.Entry<Block, Block>> blocks, final Holder<SoundEvent> sound, final BlockTransformer.TransformParticle particle) {
      RuleBasedStateProvider.Builder rules = RuleBasedStateProvider.builder();
      RuleBasedStateProvider.Builder chestRules = RuleBasedStateProvider.builder();
      RuleBasedStateProvider.Builder doorRules = RuleBasedStateProvider.builder();

      for(Map.Entry<Block, Block> entry : blocks) {
         BlockPredicate predicate = BlockPredicate.matchesBlocks((Block)entry.getKey());
         CopyPropertiesProvider provider = new CopyPropertiesProvider((Block)entry.getValue());
         Block var10000 = (Block)entry.getKey();
         Objects.requireNonNull(var10000);
         Block var10 = var10000;
         byte var11 = 0;
         //$FF: var11->value
         //0->net/minecraft/world/level/block/CopperChestBlock
         //1->net/minecraft/world/level/block/DoorBlock
         switch (var10.typeSwitch<invokedynamic>(var10, var11)) {
            case 0:
               chestRules.ifTrueThenProvide(predicate, (BlockStateProvider)provider);
               break;
            case 1:
               doorRules.ifTrueThenProvide(predicate, (BlockStateProvider)provider);
               break;
            default:
               rules.ifTrueThenProvide(predicate, (BlockStateProvider)provider);
         }
      }

      return List.of(BlockTransformer.BlockTransformData.builder(rules.build()).sound(sound).particle(particle).build(), BlockTransformer.BlockTransformData.builder(chestRules.build()).sound(sound).particle(particle).transformType(BlockTransformer.TransformType.COPPER_CHEST).updateFromNeighbors(false).build(), BlockTransformer.BlockTransformData.builder(doorRules.build()).sound(sound).particle(particle).updateFromNeighbors(false).build());
   }

   static {
      SHOVEL = new BlockTransformer(List.of(BlockTransformer.BlockTransformData.builder(BlockPredicate.allOf(BlockPredicate.matchesTag(BlockTags.TURNS_INTO_DIRT_PATH), BlockPredicate.matchesTag((Directional)Direction.UP, BlockTags.AIR)), Blocks.DIRT_PATH).sound(SoundEvents.SHOVEL_FLATTEN).disallowedFaces(List.of(Direction.DOWN)).build()));
      AXE_STRIPPABLES = BlockTransformer.BlockTransformData.builder(RuleBasedStateProvider.builder().ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.OAK_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_OAK_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.OAK_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_OAK_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.DARK_OAK_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_DARK_OAK_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.DARK_OAK_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_DARK_OAK_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.PALE_OAK_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_PALE_OAK_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.PALE_OAK_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_PALE_OAK_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.POPLAR_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_POPLAR_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.POPLAR_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_POPLAR_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.ACACIA_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_ACACIA_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.ACACIA_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_ACACIA_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.CHERRY_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_CHERRY_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.CHERRY_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_CHERRY_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.BIRCH_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_BIRCH_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.BIRCH_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_BIRCH_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.JUNGLE_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_JUNGLE_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.JUNGLE_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_JUNGLE_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.SPRUCE_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_SPRUCE_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.SPRUCE_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_SPRUCE_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.WARPED_STEM), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_WARPED_STEM))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.WARPED_HYPHAE), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_WARPED_HYPHAE))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.CRIMSON_STEM), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_CRIMSON_STEM))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.CRIMSON_HYPHAE), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_CRIMSON_HYPHAE))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.MANGROVE_WOOD), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_MANGROVE_WOOD))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.MANGROVE_LOG), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_MANGROVE_LOG))).ifTrueThenProvide(BlockPredicate.matchesBlocks(Blocks.BAMBOO_BLOCK), (BlockStateProvider)(new CopyPropertiesProvider(Blocks.STRIPPED_BAMBOO_BLOCK))).build()).sound(SoundEvents.AXE_STRIP).build();
      AXE = new BlockTransformer(ImmutableList.builder().addAll(List.of(AXE_STRIPPABLES)).addAll(axe(((BiMap)WeatheringCopper.PREVIOUS_BY_BLOCK.get()).entrySet(), SoundEvents.AXE_SCRAPE, BlockTransformer.TransformParticle.SCRAPE)).addAll(axe(((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).entrySet(), SoundEvents.AXE_WAX_OFF, BlockTransformer.TransformParticle.WAX_OFF)).build());
      HOE_DEFAULT = BlockTransformer.BlockTransformData.builder(RuleBasedStateProvider.builder().ifTrueThenProvide(BlockPredicate.allOf(BlockPredicate.matchesTag(BlockTags.TURNS_INTO_FARMLAND), BlockPredicate.matchesTag((Directional)Direction.UP, BlockTags.AIR)), Blocks.FARMLAND).ifTrueThenProvide(BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.COARSE_DIRT), BlockPredicate.matchesTag((Directional)Direction.UP, BlockTags.AIR)), Blocks.DIRT).build()).sound(SoundEvents.HOE_TILL).disallowedFaces(List.of(Direction.DOWN)).build();
      HOE_ROOTED_DIRT = BlockTransformer.BlockTransformData.builder(BlockPredicate.matchesBlocks(Blocks.ROOTED_DIRT), Blocks.DIRT).sound(SoundEvents.HOE_TILL).loot(BuiltInLootTables.TILL_ROOTED_DIRT).dropStrategy(BlockTransformer.DropStrategy.CLICKED_FACE).build();
      HOE = new BlockTransformer(ImmutableList.builder().add(HOE_DEFAULT).add(HOE_ROOTED_DIRT).build());
   }
}
