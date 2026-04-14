package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.gameevent.GameEvent;

public class HoneycombItem extends Item implements SignApplicator {
   public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() -> {
      ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();
      Stream.of(Blocks.COPPER_BLOCK, Blocks.CUT_COPPER, Blocks.CUT_COPPER_SLAB, Blocks.CUT_COPPER_STAIRS, Blocks.CHISELED_COPPER, Blocks.COPPER_DOOR, Blocks.COPPER_TRAPDOOR, Blocks.COPPER_BARS, Blocks.COPPER_GRATE, Blocks.COPPER_BULB, Blocks.COPPER_CHEST, Blocks.COPPER_GOLEM_STATUE, Blocks.LIGHTNING_ROD, Blocks.COPPER_LANTERN, Blocks.COPPER_CHAIN).forEach((collection) -> {
         Objects.requireNonNull(builder);
         collection.zipUnwaxedWaxed(builder::put);
      });
      return builder.build();
   });
   public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> ((BiMap)WAXABLES.get()).inverse());
   public static final ImmutableMap<Block, Pair<RecipeCategory, String>> WAXED_RECIPES;

   public HoneycombItem(final Item.Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(final UseOnContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      BlockState oldState = level.getBlockState(pos);
      return (InteractionResult)getWaxed(oldState).map((waxedState) -> {
         Player player = context.getPlayer();
         ItemStack itemInHand = context.getItemInHand();
         if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemInHand);
         }

         itemInHand.shrink(1);
         level.setBlock(pos, waxedState, 11);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxedState));
         level.levelEvent(player, 3003, pos, 0);
         if (oldState.getBlock() instanceof ChestBlock && oldState.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            BlockPos neighborPos = ChestBlock.getConnectedBlockPos(pos, oldState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, neighborPos, GameEvent.Context.of(player, level.getBlockState(neighborPos)));
            level.levelEvent(player, 3003, neighborPos, 0);
         }

         return InteractionResult.SUCCESS;
      }).orElse(InteractionResult.PASS);
   }

   public static Optional<BlockState> getWaxed(final BlockState oldState) {
      return Optional.ofNullable((Block)((BiMap)WAXABLES.get()).get(oldState.getBlock())).map((b) -> b.withPropertiesOf(oldState));
   }

   public boolean tryApplyToSign(final Level level, final SignBlockEntity sign, final boolean isFrontText, final ItemStack item, final Player player) {
      if (sign.setWaxed(true)) {
         level.levelEvent((Entity)null, 3003, sign.getBlockPos(), 0);
         return true;
      } else {
         return false;
      }
   }

   public boolean canApplyToSign(final SignText text, final ItemStack item, final Player player) {
      return true;
   }

   static {
      ImmutableMap.Builder<Block, Pair<RecipeCategory, String>> builder = ImmutableMap.builder();

      for(WaxedRecipeGroup data : List.of(new WaxedRecipeGroup(Blocks.COPPER_BULB, (block) -> Pair.of(RecipeCategory.REDSTONE, block.builtInRegistryHolder().key().identifier().getPath())), new WaxedRecipeGroup(Blocks.COPPER_DOOR, (var0) -> Pair.of(RecipeCategory.REDSTONE, "waxed_copper_door")), new WaxedRecipeGroup(Blocks.COPPER_TRAPDOOR, (var0) -> Pair.of(RecipeCategory.REDSTONE, "waxed_copper_trapdoor")), new WaxedRecipeGroup(Blocks.COPPER_GOLEM_STATUE, (var0) -> Pair.of(RecipeCategory.BUILDING_BLOCKS, "waxed_copper_golem_statue")), new WaxedRecipeGroup(Blocks.COPPER_CHEST, (var0) -> Pair.of(RecipeCategory.BUILDING_BLOCKS, "waxed_copper_chest")), new WaxedRecipeGroup(Blocks.LIGHTNING_ROD, (var0) -> Pair.of(RecipeCategory.BUILDING_BLOCKS, "waxed_lightning_rod")), new WaxedRecipeGroup(Blocks.COPPER_BARS, (var0) -> Pair.of(RecipeCategory.BUILDING_BLOCKS, "waxed_copper_bar")), new WaxedRecipeGroup(Blocks.COPPER_CHAIN, (var0) -> Pair.of(RecipeCategory.BUILDING_BLOCKS, "waxed_copper_chain")), new WaxedRecipeGroup(Blocks.COPPER_LANTERN, (var0) -> Pair.of(RecipeCategory.BUILDING_BLOCKS, "waxed_copper_lantern")), new WaxedRecipeGroup(Blocks.COPPER_BLOCK, (var0) -> Pair.of(RecipeCategory.BUILDING_BLOCKS, "waxed_copper_block")))) {
         data.block.waxed().forEach((block) -> builder.put(block, (Pair)data.recipeIdProvider.apply(block)));
      }

      WAXED_RECIPES = builder.build();
   }

   static record WaxedRecipeGroup(WeatheringCopperCollection<Block> block, Function<Block, Pair<RecipeCategory, String>> recipeIdProvider) {
      WaxedRecipeGroup {
         super();
      }
   }
}
