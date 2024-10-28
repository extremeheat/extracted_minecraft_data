package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class StructurePoolElement {
   public static final Codec<StructurePoolElement> CODEC;
   private static final Holder<StructureProcessorList> EMPTY;
   @Nullable
   private volatile StructureTemplatePool.Projection projection;

   protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructureTemplatePool.Projection> projectionCodec() {
      return StructureTemplatePool.Projection.CODEC.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
   }

   protected StructurePoolElement(StructureTemplatePool.Projection var1) {
      super();
      this.projection = var1;
   }

   public abstract Vec3i getSize(StructureTemplateManager var1, Rotation var2);

   public abstract List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager var1, BlockPos var2, Rotation var3, RandomSource var4);

   public abstract BoundingBox getBoundingBox(StructureTemplateManager var1, BlockPos var2, Rotation var3);

   public abstract boolean place(StructureTemplateManager var1, WorldGenLevel var2, StructureManager var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, Rotation var7, BoundingBox var8, RandomSource var9, LiquidSettings var10, boolean var11);

   public abstract StructurePoolElementType<?> getType();

   public void handleDataMarker(LevelAccessor var1, StructureTemplate.StructureBlockInfo var2, BlockPos var3, Rotation var4, RandomSource var5, BoundingBox var6) {
   }

   public StructurePoolElement setProjection(StructureTemplatePool.Projection var1) {
      this.projection = var1;
      return this;
   }

   public StructureTemplatePool.Projection getProjection() {
      StructureTemplatePool.Projection var1 = this.projection;
      if (var1 == null) {
         throw new IllegalStateException();
      } else {
         return var1;
      }
   }

   public int getGroundLevelDelta() {
      return 1;
   }

   public static Function<StructureTemplatePool.Projection, EmptyPoolElement> empty() {
      return (var0) -> {
         return EmptyPoolElement.INSTANCE;
      };
   }

   public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> legacy(String var0) {
      return (var1) -> {
         return new LegacySinglePoolElement(Either.left(ResourceLocation.parse(var0)), EMPTY, var1, Optional.empty());
      };
   }

   public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> legacy(String var0, Holder<StructureProcessorList> var1) {
      return (var2) -> {
         return new LegacySinglePoolElement(Either.left(ResourceLocation.parse(var0)), var1, var2, Optional.empty());
      };
   }

   public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String var0) {
      return (var1) -> {
         return new SinglePoolElement(Either.left(ResourceLocation.parse(var0)), EMPTY, var1, Optional.empty());
      };
   }

   public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String var0, Holder<StructureProcessorList> var1) {
      return (var2) -> {
         return new SinglePoolElement(Either.left(ResourceLocation.parse(var0)), var1, var2, Optional.empty());
      };
   }

   public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String var0, LiquidSettings var1) {
      return (var2) -> {
         return new SinglePoolElement(Either.left(ResourceLocation.parse(var0)), EMPTY, var2, Optional.of(var1));
      };
   }

   public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String var0, Holder<StructureProcessorList> var1, LiquidSettings var2) {
      return (var3) -> {
         return new SinglePoolElement(Either.left(ResourceLocation.parse(var0)), var1, var3, Optional.of(var2));
      };
   }

   public static Function<StructureTemplatePool.Projection, FeaturePoolElement> feature(Holder<PlacedFeature> var0) {
      return (var1) -> {
         return new FeaturePoolElement(var0, var1);
      };
   }

   public static Function<StructureTemplatePool.Projection, ListPoolElement> list(List<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>> var0) {
      return (var1) -> {
         return new ListPoolElement((List)var0.stream().map((var1x) -> {
            return (StructurePoolElement)var1x.apply(var1);
         }).collect(Collectors.toList()), var1);
      };
   }

   static {
      CODEC = BuiltInRegistries.STRUCTURE_POOL_ELEMENT.byNameCodec().dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
      EMPTY = Holder.direct(new StructureProcessorList(List.of()));
   }
}
