package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

public class BlackstoneReplaceProcessor extends StructureProcessor {
   public static final MapCodec<BlackstoneReplaceProcessor> CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final BlackstoneReplaceProcessor INSTANCE = new BlackstoneReplaceProcessor();
   private final Map<Block, Block> replacements = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put(Blocks.COBBLESTONE, Blocks.BLACKSTONE);
      var0.put(Blocks.MOSSY_COBBLESTONE, Blocks.BLACKSTONE);
      var0.put(Blocks.STONE, Blocks.POLISHED_BLACKSTONE);
      var0.put(Blocks.STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
      var0.put(Blocks.MOSSY_STONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS);
      var0.put(Blocks.COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
      var0.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.BLACKSTONE_STAIRS);
      var0.put(Blocks.STONE_STAIRS, Blocks.POLISHED_BLACKSTONE_STAIRS);
      var0.put(Blocks.STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
      var0.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
      var0.put(Blocks.COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
      var0.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.BLACKSTONE_SLAB);
      var0.put(Blocks.SMOOTH_STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
      var0.put(Blocks.STONE_SLAB, Blocks.POLISHED_BLACKSTONE_SLAB);
      var0.put(Blocks.STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
      var0.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
      var0.put(Blocks.STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
      var0.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
      var0.put(Blocks.COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
      var0.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BLACKSTONE_WALL);
      var0.put(Blocks.CHISELED_STONE_BRICKS, Blocks.CHISELED_POLISHED_BLACKSTONE);
      var0.put(Blocks.CRACKED_STONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
      var0.put(Blocks.IRON_BARS, Blocks.CHAIN);
   });

   private BlackstoneReplaceProcessor() {
      super();
   }

   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      Block var7 = (Block)this.replacements.get(var5.state().getBlock());
      if (var7 == null) {
         return var5;
      } else {
         BlockState var8 = var5.state();
         BlockState var9 = var7.defaultBlockState();
         if (var8.hasProperty(StairBlock.FACING)) {
            var9 = (BlockState)var9.setValue(StairBlock.FACING, (Direction)var8.getValue(StairBlock.FACING));
         }

         if (var8.hasProperty(StairBlock.HALF)) {
            var9 = (BlockState)var9.setValue(StairBlock.HALF, (Half)var8.getValue(StairBlock.HALF));
         }

         if (var8.hasProperty(SlabBlock.TYPE)) {
            var9 = (BlockState)var9.setValue(SlabBlock.TYPE, (SlabType)var8.getValue(SlabBlock.TYPE));
         }

         return new StructureTemplate.StructureBlockInfo(var5.pos(), var9, var5.nbt());
      }
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.BLACKSTONE_REPLACE;
   }
}
