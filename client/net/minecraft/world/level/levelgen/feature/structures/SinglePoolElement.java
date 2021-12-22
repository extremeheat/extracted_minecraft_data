package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class SinglePoolElement extends StructurePoolElement {
   private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC;
   public static final Codec<SinglePoolElement> CODEC;
   protected final Either<ResourceLocation, StructureTemplate> template;
   protected final Supplier<StructureProcessorList> processors;

   private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> var0, DynamicOps<T> var1, T var2) {
      Optional var3 = var0.left();
      return !var3.isPresent() ? DataResult.error("Can not serialize a runtime pool element") : ResourceLocation.CODEC.encode((ResourceLocation)var3.get(), var1, var2);
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Supplier<StructureProcessorList>> processorsCodec() {
      return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter((var0) -> {
         return var0.processors;
      });
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, StructureTemplate>> templateCodec() {
      return TEMPLATE_CODEC.fieldOf("location").forGetter((var0) -> {
         return var0.template;
      });
   }

   protected SinglePoolElement(Either<ResourceLocation, StructureTemplate> var1, Supplier<StructureProcessorList> var2, StructureTemplatePool.Projection var3) {
      super(var3);
      this.template = var1;
      this.processors = var2;
   }

   public SinglePoolElement(StructureTemplate var1) {
      this(Either.right(var1), () -> {
         return ProcessorLists.EMPTY;
      }, StructureTemplatePool.Projection.RIGID);
   }

   public Vec3i getSize(StructureManager var1, Rotation var2) {
      StructureTemplate var3 = this.getTemplate(var1);
      return var3.getSize(var2);
   }

   private StructureTemplate getTemplate(StructureManager var1) {
      Either var10000 = this.template;
      Objects.requireNonNull(var1);
      return (StructureTemplate)var10000.map(var1::getOrCreate, Function.identity());
   }

   public List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureManager var1, BlockPos var2, Rotation var3, boolean var4) {
      StructureTemplate var5 = this.getTemplate(var1);
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

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4) {
      StructureTemplate var5 = this.getTemplate(var1);
      List var6 = var5.filterBlocks(var2, (new StructurePlaceSettings()).setRotation(var3), Blocks.JIGSAW, true);
      Collections.shuffle(var6, var4);
      return var6;
   }

   public BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3) {
      StructureTemplate var4 = this.getTemplate(var1);
      return var4.getBoundingBox((new StructurePlaceSettings()).setRotation(var3), var2);
   }

   public boolean place(StructureManager var1, WorldGenLevel var2, StructureFeatureManager var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, Rotation var7, BoundingBox var8, Random var9, boolean var10) {
      StructureTemplate var11 = this.getTemplate(var1);
      StructurePlaceSettings var12 = this.getSettings(var7, var8, var10);
      if (!var11.placeInWorld(var2, var5, var6, var12, var9, 18)) {
         return false;
      } else {
         List var13 = StructureTemplate.processBlockInfos(var2, var5, var6, var12, this.getDataMarkers(var1, var5, var7, false));
         Iterator var14 = var13.iterator();

         while(var14.hasNext()) {
            StructureTemplate.StructureBlockInfo var15 = (StructureTemplate.StructureBlockInfo)var14.next();
            this.handleDataMarker(var2, var15, var5, var7, var9, var8);
         }

         return true;
      }
   }

   protected StructurePlaceSettings getSettings(Rotation var1, BoundingBox var2, boolean var3) {
      StructurePlaceSettings var4 = new StructurePlaceSettings();
      var4.setBoundingBox(var2);
      var4.setRotation(var1);
      var4.setKnownShape(true);
      var4.setIgnoreEntities(false);
      var4.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      var4.setFinalizeEntities(true);
      if (!var3) {
         var4.addProcessor(JigsawReplacementProcessor.INSTANCE);
      }

      List var10000 = ((StructureProcessorList)this.processors.get()).list();
      Objects.requireNonNull(var4);
      var10000.forEach(var4::addProcessor);
      ImmutableList var5 = this.getProjection().getProcessors();
      Objects.requireNonNull(var4);
      var5.forEach(var4::addProcessor);
      return var4;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.SINGLE;
   }

   public String toString() {
      return "Single[" + this.template + "]";
   }

   static {
      TEMPLATE_CODEC = Codec.of(SinglePoolElement::encodeTemplate, ResourceLocation.CODEC.map(Either::left));
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(templateCodec(), processorsCodec(), projectionCodec()).apply(var0, SinglePoolElement::new);
      });
   }
}
