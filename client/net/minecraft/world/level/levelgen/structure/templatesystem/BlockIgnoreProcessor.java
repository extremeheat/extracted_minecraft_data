package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockIgnoreProcessor extends StructureProcessor {
   public static final BlockIgnoreProcessor STRUCTURE_BLOCK;
   public static final BlockIgnoreProcessor AIR;
   public static final BlockIgnoreProcessor STRUCTURE_AND_AIR;
   private final ImmutableList<Block> toIgnore;

   public BlockIgnoreProcessor(List<Block> var1) {
      super();
      this.toIgnore = ImmutableList.copyOf(var1);
   }

   public BlockIgnoreProcessor(Dynamic<?> var1) {
      this(var1.get("blocks").asList((var0) -> {
         return BlockState.deserialize(var0).getBlock();
      }));
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings var5) {
      return this.toIgnore.contains(var4.state.getBlock()) ? null : var4;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.BLOCK_IGNORE;
   }

   protected <T> Dynamic<T> getDynamic(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("blocks"), var1.createList(this.toIgnore.stream().map((var1x) -> {
         return BlockState.serialize(var1, var1x.defaultBlockState()).getValue();
      })))));
   }

   static {
      STRUCTURE_BLOCK = new BlockIgnoreProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
      AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR));
      STRUCTURE_AND_AIR = new BlockIgnoreProcessor(ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
   }
}
