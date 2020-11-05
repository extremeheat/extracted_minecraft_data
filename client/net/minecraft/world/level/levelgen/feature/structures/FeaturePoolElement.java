package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class FeaturePoolElement extends StructurePoolElement {
   public static final Codec<FeaturePoolElement> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ConfiguredFeature.CODEC.fieldOf("feature").forGetter((var0x) -> {
         return var0x.feature;
      }), projectionCodec()).apply(var0, FeaturePoolElement::new);
   });
   private final Supplier<ConfiguredFeature<?, ?>> feature;
   private final CompoundTag defaultJigsawNBT;

   protected FeaturePoolElement(Supplier<ConfiguredFeature<?, ?>> var1, StructureTemplatePool.Projection var2) {
      super(var2);
      this.feature = var1;
      this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
   }

   private CompoundTag fillDefaultJigsawNBT() {
      CompoundTag var1 = new CompoundTag();
      var1.putString("name", "minecraft:bottom");
      var1.putString("final_state", "minecraft:air");
      var1.putString("pool", "minecraft:empty");
      var1.putString("target", "minecraft:empty");
      var1.putString("joint", JigsawBlockEntity.JointType.ROLLABLE.getSerializedName());
      return var1;
   }

   public BlockPos getSize(StructureManager var1, Rotation var2) {
      return BlockPos.ZERO;
   }

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4) {
      ArrayList var5 = Lists.newArrayList();
      var5.add(new StructureTemplate.StructureBlockInfo(var2, (BlockState)Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)), this.defaultJigsawNBT));
      return var5;
   }

   public BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3) {
      BlockPos var4 = this.getSize(var1, var3);
      return new BoundingBox(var2.getX(), var2.getY(), var2.getZ(), var2.getX() + var4.getX(), var2.getY() + var4.getY(), var2.getZ() + var4.getZ());
   }

   public boolean place(StructureManager var1, WorldGenLevel var2, StructureFeatureManager var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, Rotation var7, BoundingBox var8, Random var9, boolean var10) {
      return ((ConfiguredFeature)this.feature.get()).place(var2, var4, var9, var5);
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.FEATURE;
   }

   public String toString() {
      return "Feature[" + Registry.FEATURE.getKey(((ConfiguredFeature)this.feature.get()).feature()) + "]";
   }
}
