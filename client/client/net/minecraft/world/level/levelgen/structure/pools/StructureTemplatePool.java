package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

public class StructureTemplatePool {
   private static final int SIZE_UNSET = -2147483648;
   private static final MutableObject<Codec<Holder<StructureTemplatePool>>> CODEC_REFERENCE = new MutableObject();
   public static final Codec<StructureTemplatePool> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.lazyInitialized(CODEC_REFERENCE::getValue).fieldOf("fallback").forGetter(StructureTemplatePool::getFallback),
               Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight"))
                  .codec()
                  .listOf()
                  .fieldOf("elements")
                  .forGetter(var0x -> var0x.rawTemplates)
            )
            .apply(var0, StructureTemplatePool::new)
   );
   public static final Codec<Holder<StructureTemplatePool>> CODEC = Util.make(
      RegistryFileCodec.create(Registries.TEMPLATE_POOL, DIRECT_CODEC), CODEC_REFERENCE::setValue
   );
   private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
   private final ObjectArrayList<StructurePoolElement> templates;
   private final Holder<StructureTemplatePool> fallback;
   private int maxSize = -2147483648;

   public StructureTemplatePool(Holder<StructureTemplatePool> var1, List<Pair<StructurePoolElement, Integer>> var2) {
      super();
      this.rawTemplates = var2;
      this.templates = new ObjectArrayList();

      for (Pair var4 : var2) {
         StructurePoolElement var5 = (StructurePoolElement)var4.getFirst();

         for (int var6 = 0; var6 < var4.getSecond(); var6++) {
            this.templates.add(var5);
         }
      }

      this.fallback = var1;
   }

   public StructureTemplatePool(
      Holder<StructureTemplatePool> var1,
      List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> var2,
      StructureTemplatePool.Projection var3
   ) {
      super();
      this.rawTemplates = Lists.newArrayList();
      this.templates = new ObjectArrayList();

      for (Pair var5 : var2) {
         StructurePoolElement var6 = (StructurePoolElement)((Function)var5.getFirst()).apply(var3);
         this.rawTemplates.add(Pair.of(var6, (Integer)var5.getSecond()));

         for (int var7 = 0; var7 < var5.getSecond(); var7++) {
            this.templates.add(var6);
         }
      }

      this.fallback = var1;
   }

   public int getMaxSize(StructureTemplateManager var1) {
      if (this.maxSize == -2147483648) {
         this.maxSize = this.templates
            .stream()
            .filter(var0 -> var0 != EmptyPoolElement.INSTANCE)
            .mapToInt(var1x -> var1x.getBoundingBox(var1, BlockPos.ZERO, Rotation.NONE).getYSpan())
            .max()
            .orElse(0);
      }

      return this.maxSize;
   }

   public Holder<StructureTemplatePool> getFallback() {
      return this.fallback;
   }

   public StructurePoolElement getRandomTemplate(RandomSource var1) {
      return (StructurePoolElement)this.templates.get(var1.nextInt(this.templates.size()));
   }

   public List<StructurePoolElement> getShuffledTemplates(RandomSource var1) {
      return Util.shuffledCopy(this.templates, var1);
   }

   public int size() {
      return this.templates.size();
   }

   public static enum Projection implements StringRepresentable {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      public static final StringRepresentable.EnumCodec<StructureTemplatePool.Projection> CODEC = StringRepresentable.fromEnum(
         StructureTemplatePool.Projection::values
      );
      private final String name;
      private final ImmutableList<StructureProcessor> processors;

      private Projection(final String param3, final ImmutableList<StructureProcessor> param4) {
         this.name = nullxx;
         this.processors = nullxxx;
      }

      public String getName() {
         return this.name;
      }

      public static StructureTemplatePool.Projection byName(String var0) {
         return CODEC.byName(var0);
      }

      public ImmutableList<StructureProcessor> getProcessors() {
         return this.processors;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
