package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class FeaturePoolElement extends StructurePoolElement {
   private final ConfiguredFeature feature;
   private final CompoundTag defaultJigsawNBT;

   @Deprecated
   public FeaturePoolElement(ConfiguredFeature var1) {
      this(var1, StructureTemplatePool.Projection.RIGID);
   }

   public FeaturePoolElement(ConfiguredFeature var1, StructureTemplatePool.Projection var2) {
      super(var2);
      this.feature = var1;
      this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
   }

   public FeaturePoolElement(Dynamic var1) {
      super(var1);
      this.feature = ConfiguredFeature.deserialize(var1.get("feature").orElseEmptyMap());
      this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
   }

   public CompoundTag fillDefaultJigsawNBT() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("target_pool", "minecraft:empty");
      var1.putString("attachement_type", "minecraft:bottom");
      var1.putString("final_state", "minecraft:air");
      return var1;
   }

   public BlockPos getSize(StructureManager var1, Rotation var2) {
      return BlockPos.ZERO;
   }

   public List getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4) {
      ArrayList var5 = Lists.newArrayList();
      var5.add(new StructureTemplate.StructureBlockInfo(var2, (BlockState)Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.FACING, Direction.DOWN), this.defaultJigsawNBT));
      return var5;
   }

   public BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3) {
      BlockPos var4 = this.getSize(var1, var3);
      return new BoundingBox(var2.getX(), var2.getY(), var2.getZ(), var2.getX() + var4.getX(), var2.getY() + var4.getY(), var2.getZ() + var4.getZ());
   }

   public boolean place(StructureManager var1, LevelAccessor var2, ChunkGenerator var3, BlockPos var4, Rotation var5, BoundingBox var6, Random var7) {
      return this.feature.place(var2, var3, var7, var4);
   }

   public Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("feature"), this.feature.serialize(var1).getValue())));
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.FEATURE;
   }

   public String toString() {
      return "Feature[" + Registry.FEATURE.getKey(this.feature.feature) + "]";
   }
}
