package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class FossilFeature extends Feature<NoneFeatureConfiguration> {
   private static final ResourceLocation SPINE_1 = new ResourceLocation("fossil/spine_1");
   private static final ResourceLocation SPINE_2 = new ResourceLocation("fossil/spine_2");
   private static final ResourceLocation SPINE_3 = new ResourceLocation("fossil/spine_3");
   private static final ResourceLocation SPINE_4 = new ResourceLocation("fossil/spine_4");
   private static final ResourceLocation SPINE_1_COAL = new ResourceLocation("fossil/spine_1_coal");
   private static final ResourceLocation SPINE_2_COAL = new ResourceLocation("fossil/spine_2_coal");
   private static final ResourceLocation SPINE_3_COAL = new ResourceLocation("fossil/spine_3_coal");
   private static final ResourceLocation SPINE_4_COAL = new ResourceLocation("fossil/spine_4_coal");
   private static final ResourceLocation SKULL_1 = new ResourceLocation("fossil/skull_1");
   private static final ResourceLocation SKULL_2 = new ResourceLocation("fossil/skull_2");
   private static final ResourceLocation SKULL_3 = new ResourceLocation("fossil/skull_3");
   private static final ResourceLocation SKULL_4 = new ResourceLocation("fossil/skull_4");
   private static final ResourceLocation SKULL_1_COAL = new ResourceLocation("fossil/skull_1_coal");
   private static final ResourceLocation SKULL_2_COAL = new ResourceLocation("fossil/skull_2_coal");
   private static final ResourceLocation SKULL_3_COAL = new ResourceLocation("fossil/skull_3_coal");
   private static final ResourceLocation SKULL_4_COAL = new ResourceLocation("fossil/skull_4_coal");
   private static final ResourceLocation[] fossils;
   private static final ResourceLocation[] fossilsCoal;

   public FossilFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      Rotation var6 = Rotation.getRandom(var3);
      int var7 = var3.nextInt(fossils.length);
      StructureManager var8 = var1.getLevel().getServer().getStructureManager();
      StructureTemplate var9 = var8.getOrCreate(fossils[var7]);
      StructureTemplate var10 = var8.getOrCreate(fossilsCoal[var7]);
      ChunkPos var11 = new ChunkPos(var4);
      BoundingBox var12 = new BoundingBox(var11.getMinBlockX(), 0, var11.getMinBlockZ(), var11.getMaxBlockX(), 256, var11.getMaxBlockZ());
      StructurePlaceSettings var13 = (new StructurePlaceSettings()).setRotation(var6).setBoundingBox(var12).setRandom(var3).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      BlockPos var14 = var9.getSize(var6);
      int var15 = var3.nextInt(16 - var14.getX());
      int var16 = var3.nextInt(16 - var14.getZ());
      int var17 = 256;

      int var18;
      for(var18 = 0; var18 < var14.getX(); ++var18) {
         for(int var19 = 0; var19 < var14.getZ(); ++var19) {
            var17 = Math.min(var17, var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var4.getX() + var18 + var15, var4.getZ() + var19 + var16));
         }
      }

      var18 = Math.max(var17 - 15 - var3.nextInt(10), 10);
      BlockPos var22 = var9.getZeroPositionWithTransform(var4.offset(var15, var18, var16), Mirror.NONE, var6);
      BlockRotProcessor var20 = new BlockRotProcessor(0.9F);
      var13.clearProcessors().addProcessor(var20);
      var9.placeInWorld(var1, var22, var22, var13, var3, 4);
      var13.popProcessor(var20);
      BlockRotProcessor var21 = new BlockRotProcessor(0.1F);
      var13.clearProcessors().addProcessor(var21);
      var10.placeInWorld(var1, var22, var22, var13, var3, 4);
      return true;
   }

   static {
      fossils = new ResourceLocation[]{SPINE_1, SPINE_2, SPINE_3, SPINE_4, SKULL_1, SKULL_2, SKULL_3, SKULL_4};
      fossilsCoal = new ResourceLocation[]{SPINE_1_COAL, SPINE_2_COAL, SPINE_3_COAL, SPINE_4_COAL, SKULL_1_COAL, SKULL_2_COAL, SKULL_3_COAL, SKULL_4_COAL};
   }
}
