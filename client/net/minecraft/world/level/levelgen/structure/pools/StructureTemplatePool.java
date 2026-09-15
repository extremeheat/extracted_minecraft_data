package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

public class StructureTemplatePool {
   private static final int SIZE_UNSET = -2147483648;
   private static final MutableObject<Codec<Holder<StructureTemplatePool>>> CODEC_REFERENCE = new MutableObject();
   public static final Codec<StructureTemplatePool> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.lazyInitialized(CODEC_REFERENCE).fieldOf("fallback").forGetter(StructureTemplatePool::getFallback), Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter((p) -> p.rawTemplates)).apply(i, StructureTemplatePool::new));
   public static final Codec<Holder<StructureTemplatePool>> CODEC;
   private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
   private final ObjectArrayList<StructurePoolElement> templates;
   private final Holder<StructureTemplatePool> fallback;
   private int maxSize = -2147483648;

   public StructureTemplatePool(final Holder<StructureTemplatePool> fallback, final List<Pair<StructurePoolElement, Integer>> templates) {
      super();
      this.rawTemplates = templates;
      this.templates = new ObjectArrayList();

      for(Pair<StructurePoolElement, Integer> templateDef : templates) {
         StructurePoolElement element = (StructurePoolElement)templateDef.getFirst();

         for(int i = 0; i < (Integer)templateDef.getSecond(); ++i) {
            this.templates.add(element);
         }
      }

      this.fallback = fallback;
   }

   public StructureTemplatePool(final Holder<StructureTemplatePool> fallback, final List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> templates, final Projection projection) {
      super();
      this.rawTemplates = Lists.newArrayList();
      this.templates = new ObjectArrayList();

      for(Pair<Function<Projection, ? extends StructurePoolElement>, Integer> templateDef : templates) {
         StructurePoolElement element = (StructurePoolElement)((Function)templateDef.getFirst()).apply(projection);
         this.rawTemplates.add(Pair.of(element, (Integer)templateDef.getSecond()));

         for(int i = 0; i < (Integer)templateDef.getSecond(); ++i) {
            this.templates.add(element);
         }
      }

      this.fallback = fallback;
   }

   public int getMaxSize(final StructureTemplateManager manager) {
      if (this.maxSize == -2147483648) {
         this.maxSize = this.templates.stream().filter((t) -> t != EmptyPoolElement.INSTANCE).mapToInt((t) -> t.getBoundingBox(manager, BlockPos.ZERO, Rotation.NONE).getYSpan()).max().orElse(0);
      }

      return this.maxSize;
   }

   @VisibleForTesting
   public List<Pair<StructurePoolElement, Integer>> getTemplates() {
      return this.rawTemplates;
   }

   public Holder<StructureTemplatePool> getFallback() {
      return this.fallback;
   }

   public StructurePoolElement getRandomTemplate(final RandomSource random) {
      return (StructurePoolElement)(this.templates.isEmpty() ? EmptyPoolElement.INSTANCE : (StructurePoolElement)this.templates.get(random.nextInt(this.templates.size())));
   }

   public List<StructurePoolElement> getShuffledTemplates(final RandomSource random) {
      return Util.shuffledCopy(this.templates, random);
   }

   public int size() {
      return this.templates.size();
   }

   static {
      Codec var10000 = RegistryCodecs.holder(Registries.TEMPLATE_POOL, DIRECT_CODEC);
      MutableObject var10001 = CODEC_REFERENCE;
      Objects.requireNonNull(var10001);
      CODEC = (Codec)Util.make(var10000, var10001::setValue);
   }

   public static enum Projection implements StringRepresentable {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      public static final StringRepresentable.EnumCodec<Projection> CODEC = StringRepresentable.<Projection>fromEnum(Projection::values);
      private final String name;
      private final ImmutableList<StructureProcessor> processors;

      private Projection(final String name, final ImmutableList<StructureProcessor> processors) {
         this.name = name;
         this.processors = processors;
      }

      public String getName() {
         return this.name;
      }

      public static Projection byName(final String name) {
         return CODEC.byName(name);
      }

      public ImmutableList<StructureProcessor> getProcessors() {
         return this.processors;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Projection[] $values() {
         return new Projection[]{TERRAIN_MATCHING, RIGID};
      }
   }
}
