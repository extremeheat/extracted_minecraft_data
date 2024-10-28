package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class SinglePoolElement extends StructurePoolElement {
   private static final Codec<Either<ResourceLocation, StructureTemplate>> TEMPLATE_CODEC;
   public static final MapCodec<SinglePoolElement> CODEC;
   protected final Either<ResourceLocation, StructureTemplate> template;
   protected final Holder<StructureProcessorList> processors;
   protected final Optional<LiquidSettings> overrideLiquidSettings;

   private static <T> DataResult<T> encodeTemplate(Either<ResourceLocation, StructureTemplate> var0, DynamicOps<T> var1, T var2) {
      Optional var3 = var0.left();
      return var3.isEmpty() ? DataResult.error(() -> {
         return "Can not serialize a runtime pool element";
      }) : ResourceLocation.CODEC.encode((ResourceLocation)var3.get(), var1, var2);
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Holder<StructureProcessorList>> processorsCodec() {
      return StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter((var0) -> {
         return var0.processors;
      });
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Optional<LiquidSettings>> overrideLiquidSettingsCodec() {
      return LiquidSettings.CODEC.optionalFieldOf("override_liquid_settings").forGetter((var0) -> {
         return var0.overrideLiquidSettings;
      });
   }

   protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<ResourceLocation, StructureTemplate>> templateCodec() {
      return TEMPLATE_CODEC.fieldOf("location").forGetter((var0) -> {
         return var0.template;
      });
   }

   protected SinglePoolElement(Either<ResourceLocation, StructureTemplate> var1, Holder<StructureProcessorList> var2, StructureTemplatePool.Projection var3, Optional<LiquidSettings> var4) {
      super(var3);
      this.template = var1;
      this.processors = var2;
      this.overrideLiquidSettings = var4;
   }

   public Vec3i getSize(StructureTemplateManager var1, Rotation var2) {
      StructureTemplate var3 = this.getTemplate(var1);
      return var3.getSize(var2);
   }

   private StructureTemplate getTemplate(StructureTemplateManager var1) {
      Either var10000 = this.template;
      Objects.requireNonNull(var1);
      return (StructureTemplate)var10000.map(var1::getOrCreate, Function.identity());
   }

   public List<StructureTemplate.StructureBlockInfo> getDataMarkers(StructureTemplateManager var1, BlockPos var2, Rotation var3, boolean var4) {
      StructureTemplate var5 = this.getTemplate(var1);
      ObjectArrayList var6 = var5.filterBlocks(var2, (new StructurePlaceSettings()).setRotation(var3), Blocks.STRUCTURE_BLOCK, var4);
      ArrayList var7 = Lists.newArrayList();
      Iterator var8 = var6.iterator();

      while(var8.hasNext()) {
         StructureTemplate.StructureBlockInfo var9 = (StructureTemplate.StructureBlockInfo)var8.next();
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

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager var1, BlockPos var2, Rotation var3, RandomSource var4) {
      StructureTemplate var5 = this.getTemplate(var1);
      ObjectArrayList var6 = var5.filterBlocks(var2, (new StructurePlaceSettings()).setRotation(var3), Blocks.JIGSAW, true);
      Util.shuffle(var6, var4);
      sortBySelectionPriority(var6);
      return var6;
   }

   @VisibleForTesting
   static void sortBySelectionPriority(List<StructureTemplate.StructureBlockInfo> var0) {
      var0.sort(Comparator.comparingInt((var0x) -> {
         return (Integer)Optionull.mapOrDefault(var0x.nbt(), (var0) -> {
            return var0.getInt("selection_priority");
         }, 0);
      }).reversed());
   }

   public BoundingBox getBoundingBox(StructureTemplateManager var1, BlockPos var2, Rotation var3) {
      StructureTemplate var4 = this.getTemplate(var1);
      return var4.getBoundingBox((new StructurePlaceSettings()).setRotation(var3), var2);
   }

   public boolean place(StructureTemplateManager var1, WorldGenLevel var2, StructureManager var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, Rotation var7, BoundingBox var8, RandomSource var9, LiquidSettings var10, boolean var11) {
      StructureTemplate var12 = this.getTemplate(var1);
      StructurePlaceSettings var13 = this.getSettings(var7, var8, var10, var11);
      if (!var12.placeInWorld(var2, var5, var6, var13, var9, 18)) {
         return false;
      } else {
         List var14 = StructureTemplate.processBlockInfos(var2, var5, var6, var13, this.getDataMarkers(var1, var5, var7, false));
         Iterator var15 = var14.iterator();

         while(var15.hasNext()) {
            StructureTemplate.StructureBlockInfo var16 = (StructureTemplate.StructureBlockInfo)var15.next();
            this.handleDataMarker(var2, var16, var5, var7, var9, var8);
         }

         return true;
      }
   }

   protected StructurePlaceSettings getSettings(Rotation var1, BoundingBox var2, LiquidSettings var3, boolean var4) {
      StructurePlaceSettings var5 = new StructurePlaceSettings();
      var5.setBoundingBox(var2);
      var5.setRotation(var1);
      var5.setKnownShape(true);
      var5.setIgnoreEntities(false);
      var5.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      var5.setFinalizeEntities(true);
      var5.setLiquidSettings((LiquidSettings)this.overrideLiquidSettings.orElse(var3));
      if (!var4) {
         var5.addProcessor(JigsawReplacementProcessor.INSTANCE);
      }

      List var10000 = ((StructureProcessorList)this.processors.value()).list();
      Objects.requireNonNull(var5);
      var10000.forEach(var5::addProcessor);
      ImmutableList var6 = this.getProjection().getProcessors();
      Objects.requireNonNull(var5);
      var6.forEach(var5::addProcessor);
      return var5;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.SINGLE;
   }

   public String toString() {
      return "Single[" + String.valueOf(this.template) + "]";
   }

   static {
      TEMPLATE_CODEC = Codec.of(SinglePoolElement::encodeTemplate, ResourceLocation.CODEC.map(Either::left));
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(templateCodec(), processorsCodec(), projectionCodec(), overrideLiquidSettingsCodec()).apply(var0, SinglePoolElement::new);
      });
   }
}
