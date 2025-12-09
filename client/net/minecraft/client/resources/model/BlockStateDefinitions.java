package net.minecraft.client.resources.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BlockStateDefinitions {
   private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = createItemFrameFakeState();
   private static final StateDefinition<Block, BlockState> GLOW_ITEM_FRAME_FAKE_DEFINITION = createItemFrameFakeState();
   private static final Identifier GLOW_ITEM_FRAME_LOCATION = Identifier.withDefaultNamespace("glow_item_frame");
   private static final Identifier ITEM_FRAME_LOCATION = Identifier.withDefaultNamespace("item_frame");
   private static final Map<Identifier, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS;

   public BlockStateDefinitions() {
      super();
   }

   private static StateDefinition<Block, BlockState> createItemFrameFakeState() {
      return (new StateDefinition.Builder<Block, BlockState>(Blocks.AIR)).add(BlockStateProperties.MAP).create(Block::defaultBlockState, BlockState::new);
   }

   public static BlockState getItemFrameFakeState(boolean var0, boolean var1) {
      return (BlockState)((BlockState)(var0 ? GLOW_ITEM_FRAME_FAKE_DEFINITION : ITEM_FRAME_FAKE_DEFINITION).any()).setValue(BlockStateProperties.MAP, var1);
   }

   static Function<Identifier, StateDefinition<Block, BlockState>> definitionLocationToBlockStateMapper() {
      HashMap var0 = new HashMap(STATIC_DEFINITIONS);

      for(Block var2 : BuiltInRegistries.BLOCK) {
         var0.put(var2.builtInRegistryHolder().key().identifier(), var2.getStateDefinition());
      }

      Objects.requireNonNull(var0);
      return var0::get;
   }

   static {
      STATIC_DEFINITIONS = Map.of(ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, GLOW_ITEM_FRAME_LOCATION, GLOW_ITEM_FRAME_FAKE_DEFINITION);
   }
}
