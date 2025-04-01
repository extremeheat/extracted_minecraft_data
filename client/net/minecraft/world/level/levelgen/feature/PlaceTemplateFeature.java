package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.configurations.PlaceTemplateConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class PlaceTemplateFeature extends Feature<PlaceTemplateConfiguration> {
   public PlaceTemplateFeature(Codec<PlaceTemplateConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<PlaceTemplateConfiguration> var1) {
      RandomSource var2 = var1.random();
      WorldGenLevel var3 = var1.level();
      BlockPos var4 = var1.origin();
      PlaceTemplateConfiguration var5 = (PlaceTemplateConfiguration)var1.config();
      Rotation var6 = (Rotation)var5.forcedRotation().orElseGet(() -> Rotation.getRandom(var2));
      ResourceLocation var7 = (ResourceLocation)Util.getRandom(var5.templates(), var2);
      StructureTemplateManager var8 = var3.getLevel().theGame().getStructureManager();
      StructureTemplate var9 = var8.getOrCreate(var7);
      ChunkPos var10 = new ChunkPos(var4);
      BoundingBox var11 = new BoundingBox(var10.getMinBlockX() - 16, var3.getMinY(), var10.getMinBlockZ() - 16, var10.getMaxBlockX() + 16, var3.getMaxY(), var10.getMaxBlockZ() + 16);
      StructurePlaceSettings var12 = (new StructurePlaceSettings()).setRotation(var6).setBoundingBox(var11).setRandom(var2).setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
      Vec3i var13 = var9.getSize(var6);
      BlockPos var14 = var4.offset(-var13.getX() / 2, 0, -var13.getZ() / 2);
      BlockPos var15 = var9.getZeroPositionWithTransform(var14, Mirror.NONE, var6);
      var9.placeInWorld(var3, var15, var15, var12, var2, 3);
      return true;
   }
}
