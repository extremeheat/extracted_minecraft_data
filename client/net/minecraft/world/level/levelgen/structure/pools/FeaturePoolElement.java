package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class FeaturePoolElement extends StructurePoolElement {
   public static final MapCodec<FeaturePoolElement> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(PlacedFeature.CODEC.fieldOf("feature").forGetter((var0x) -> {
         return var0x.feature;
      }), projectionCodec()).apply(var0, FeaturePoolElement::new);
   });
   private final Holder<PlacedFeature> feature;
   private final CompoundTag defaultJigsawNBT;

   protected FeaturePoolElement(Holder<PlacedFeature> var1, StructureTemplatePool.Projection var2) {
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

   public Vec3i getSize(StructureTemplateManager var1, Rotation var2) {
      return Vec3i.ZERO;
   }

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager var1, BlockPos var2, Rotation var3, RandomSource var4) {
      ArrayList var5 = Lists.newArrayList();
      var5.add(new StructureTemplate.StructureBlockInfo(var2, (BlockState)Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)), this.defaultJigsawNBT));
      return var5;
   }

   public BoundingBox getBoundingBox(StructureTemplateManager var1, BlockPos var2, Rotation var3) {
      Vec3i var4 = this.getSize(var1, var3);
      return new BoundingBox(var2.getX(), var2.getY(), var2.getZ(), var2.getX() + var4.getX(), var2.getY() + var4.getY(), var2.getZ() + var4.getZ());
   }

   public boolean place(StructureTemplateManager var1, WorldGenLevel var2, StructureManager var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, Rotation var7, BoundingBox var8, RandomSource var9, LiquidSettings var10, boolean var11) {
      return ((PlacedFeature)this.feature.value()).place(var2, var4, var9, var5);
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.FEATURE;
   }

   public String toString() {
      return "Feature[" + String.valueOf(this.feature) + "]";
   }
}
