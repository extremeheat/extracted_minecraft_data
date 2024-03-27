package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class SinglePoolElement extends StructurePoolElement {
   private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC = Codec.of(
      SinglePoolElement::encodeTemplate, ResourceLocation.CODEC.map(Either::left)
   );
   public static final MapCodec<SinglePoolElement> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(templateCodec(), processorsCodec(), projectionCodec()).apply(var0, SinglePoolElement::new)
   );
   protected final Either<ResourceLocation, StructureTemplate> template;
   protected final Holder<StructureProcessorList> processors;

   private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> var0, DynamicOps<T> var1, T var2) {
      Optional var3 = var0.left();
      return var3.isEmpty()
         ? DataResult.error(() -> "Can not serialize a runtime pool element")
         : ResourceLocation.CODEC.encode((ResourceLocation)var3.get(), var1, var2);
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Holder<StructureProcessorList>> processorsCodec() {
      return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(var0 -> var0.processors);
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, StructureTemplate>> templateCodec() {
      return TEMPLATE_CODEC.fieldOf("location").forGetter(var0 -> var0.template);
   }

   protected SinglePoolElement(Either<ResourceLocation, StructureTemplate> var1, Holder<StructureProcessorList> var2, StructureTemplatePool.Projection var3) {
      super(var3);
      this.template = var1;
      this.processors = var2;
   }

   @Override
   public Vec3i getSize(StructureTemplateManager var1, Rotation var2) {
      StructureTemplate var3 = this.getTemplate(var1);
      return var3.getSize(var2);
   }

   private StructureTemplate getTemplate(StructureTemplateManager var1) {
      return (StructureTemplate)this.template.map(var1::getOrCreate, Function.identity());
   }

   public List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureTemplateManager var1, BlockPos var2, Rotation var3, boolean var4) {
      StructureTemplate var5 = this.getTemplate(var1);
      ObjectArrayList var6 = var5.filterBlocks(var2, new StructurePlaceSettings().setRotation(var3), Blocks.STRUCTURE_BLOCK, var4);
      ArrayList var7 = Lists.newArrayList();

      for(StructureTemplate.StructureBlockInfo var9 : var6) {
         CompoundTag var10 = var9.nbt();
         if (var10 != null) {
            StructureMode var11 = StructureMode.valueOf(var10.getString("mode"));
            if (var11 == StructureMode.DATA) {
               var7.add(var9);
            }
         }
      }

      return var7;
   }

   @Override
   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager var1, BlockPos var2, Rotation var3, RandomSource var4) {
      StructureTemplate var5 = this.getTemplate(var1);
      ObjectArrayList var6 = var5.filterBlocks(var2, new StructurePlaceSettings().setRotation(var3), Blocks.JIGSAW, true);
      Util.shuffle(var6, var4);
      sortBySelectionPriority(var6);
      return var6;
   }

   @VisibleForTesting
   static void sortBySelectionPriority(List<StructureTemplate.StructureBlockInfo> var0) {
      var0.sort(Comparator.comparingInt(var0x -> Optionull.mapOrDefault(var0x.nbt(), var0xx -> var0xx.getInt("selection_priority"), 0)).reversed());
   }

   @Override
   public BoundingBox getBoundingBox(StructureTemplateManager var1, BlockPos var2, Rotation var3) {
      StructureTemplate var4 = this.getTemplate(var1);
      return var4.getBoundingBox(new StructurePlaceSettings().setRotation(var3), var2);
   }

   @Override
   public boolean place(
      StructureTemplateManager var1,
      WorldGenLevel var2,
      StructureManager var3,
      ChunkGenerator var4,
      BlockPos var5,
      BlockPos var6,
      Rotation var7,
      BoundingBox var8,
      RandomSource var9,
      boolean var10
   ) {
      StructureTemplate var11 = this.getTemplate(var1);
      StructurePlaceSettings var12 = this.getSettings(var7, var8, var10);
      if (!var11.placeInWorld(var2, var5, var6, var12, var9, 18)) {
         return false;
      } else {
         for(StructureTemplate.StructureBlockInfo var15 : StructureTemplate.processBlockInfos(
            var2, var5, var6, var12, this.getDataMarkers(var1, var5, var7, false)
         )) {
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

      this.processors.value().list().forEach(var4::addProcessor);
      this.getProjection().getProcessors().forEach(var4::addProcessor);
      return var4;
   }

   @Override
   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.SINGLE;
   }

   @Override
   public String toString() {
      return "Single[" + this.template + "]";
   }
}
