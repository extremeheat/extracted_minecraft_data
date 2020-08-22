package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.NopProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class SinglePoolElement extends StructurePoolElement {
   protected final ResourceLocation location;
   protected final ImmutableList processors;

   @Deprecated
   public SinglePoolElement(String var1, List var2) {
      this(var1, var2, StructureTemplatePool.Projection.RIGID);
   }

   public SinglePoolElement(String var1, List var2, StructureTemplatePool.Projection var3) {
      super(var3);
      this.location = new ResourceLocation(var1);
      this.processors = ImmutableList.copyOf(var2);
   }

   @Deprecated
   public SinglePoolElement(String var1) {
      this(var1, ImmutableList.of());
   }

   public SinglePoolElement(Dynamic var1) {
      super(var1);
      this.location = new ResourceLocation(var1.get("location").asString(""));
      this.processors = ImmutableList.copyOf(var1.get("processors").asList((var0) -> {
         return (StructureProcessor)Deserializer.deserialize(var0, Registry.STRUCTURE_PROCESSOR, "processor_type", NopProcessor.INSTANCE);
      }));
   }

   public List getDataMarkers(StructureManager var1, BlockPos var2, Rotation var3, boolean var4) {
      StructureTemplate var5 = var1.getOrCreate(this.location);
      List var6 = var5.filterBlocks(var2, (new StructurePlaceSettings()).setRotation(var3), Blocks.STRUCTURE_BLOCK, var4);
      ArrayList var7 = Lists.newArrayList();
      Iterator var8 = var6.iterator();

      while(var8.hasNext()) {
         StructureTemplate.StructureBlockInfo var9 = (StructureTemplate.StructureBlockInfo)var8.next();
         if (var9.nbt != null) {
            StructureMode var10 = StructureMode.valueOf(var9.nbt.getString("mode"));
            if (var10 == StructureMode.DATA) {
               var7.add(var9);
            }
         }
      }

      return var7;
   }

   public List getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4) {
      StructureTemplate var5 = var1.getOrCreate(this.location);
      List var6 = var5.filterBlocks(var2, (new StructurePlaceSettings()).setRotation(var3), Blocks.JIGSAW, true);
      Collections.shuffle(var6, var4);
      return var6;
   }

   public BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3) {
      StructureTemplate var4 = var1.getOrCreate(this.location);
      return var4.getBoundingBox((new StructurePlaceSettings()).setRotation(var3), var2);
   }

   public boolean place(StructureManager var1, LevelAccessor var2, ChunkGenerator var3, BlockPos var4, Rotation var5, BoundingBox var6, Random var7) {
      StructureTemplate var8 = var1.getOrCreate(this.location);
      StructurePlaceSettings var9 = this.getSettings(var5, var6);
      if (!var8.placeInWorld(var2, var4, var9, 18)) {
         return false;
      } else {
         List var10 = StructureTemplate.processBlockInfos(var2, var4, var9, this.getDataMarkers(var1, var4, var5, false));
         Iterator var11 = var10.iterator();

         while(var11.hasNext()) {
            StructureTemplate.StructureBlockInfo var12 = (StructureTemplate.StructureBlockInfo)var11.next();
            this.handleDataMarker(var2, var12, var4, var5, var7, var6);
         }

         return true;
      }
   }

   protected StructurePlaceSettings getSettings(Rotation var1, BoundingBox var2) {
      StructurePlaceSettings var3 = new StructurePlaceSettings();
      var3.setBoundingBox(var2);
      var3.setRotation(var1);
      var3.setKnownShape(true);
      var3.setIgnoreEntities(false);
      var3.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      var3.addProcessor(JigsawReplacementProcessor.INSTANCE);
      this.processors.forEach(var3::addProcessor);
      this.getProjection().getProcessors().forEach(var3::addProcessor);
      return var3;
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.SINGLE;
   }

   public Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("location"), var1.createString(this.location.toString()), var1.createString("processors"), var1.createList(this.processors.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      })))));
   }

   public String toString() {
      return "Single[" + this.location + "]";
   }
}
