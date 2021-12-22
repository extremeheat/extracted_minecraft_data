package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureTemplatePool {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int SIZE_UNSET = -2147483648;
   public static final Codec<StructureTemplatePool> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ResourceLocation.CODEC.fieldOf("name").forGetter(StructureTemplatePool::getName), ResourceLocation.CODEC.fieldOf("fallback").forGetter(StructureTemplatePool::getFallback), Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter((var0x) -> {
         return var0x.rawTemplates;
      })).apply(var0, StructureTemplatePool::new);
   });
   public static final Codec<Supplier<StructureTemplatePool>> CODEC;
   private final ResourceLocation name;
   private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
   private final List<StructurePoolElement> templates;
   private final ResourceLocation fallback;
   private int maxSize = -2147483648;

   public StructureTemplatePool(ResourceLocation var1, ResourceLocation var2, List<Pair<StructurePoolElement, Integer>> var3) {
      super();
      this.name = var1;
      this.rawTemplates = var3;
      this.templates = Lists.newArrayList();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         Pair var5 = (Pair)var4.next();
         StructurePoolElement var6 = (StructurePoolElement)var5.getFirst();

         for(int var7 = 0; var7 < (Integer)var5.getSecond(); ++var7) {
            this.templates.add(var6);
         }
      }

      this.fallback = var2;
   }

   public StructureTemplatePool(ResourceLocation var1, ResourceLocation var2, List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> var3, StructureTemplatePool.Projection var4) {
      super();
      this.name = var1;
      this.rawTemplates = Lists.newArrayList();
      this.templates = Lists.newArrayList();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         Pair var6 = (Pair)var5.next();
         StructurePoolElement var7 = (StructurePoolElement)((Function)var6.getFirst()).apply(var4);
         this.rawTemplates.add(Pair.of(var7, (Integer)var6.getSecond()));

         for(int var8 = 0; var8 < (Integer)var6.getSecond(); ++var8) {
            this.templates.add(var7);
         }
      }

      this.fallback = var2;
   }

   public int getMaxSize(StructureManager var1) {
      if (this.maxSize == -2147483648) {
         this.maxSize = this.templates.stream().filter((var0) -> {
            return var0 != EmptyPoolElement.INSTANCE;
         }).mapToInt((var1x) -> {
            return var1x.getBoundingBox(var1, BlockPos.ZERO, Rotation.NONE).getYSpan();
         }).max().orElse(0);
      }

      return this.maxSize;
   }

   public ResourceLocation getFallback() {
      return this.fallback;
   }

   public StructurePoolElement getRandomTemplate(Random var1) {
      return (StructurePoolElement)this.templates.get(var1.nextInt(this.templates.size()));
   }

   public List<StructurePoolElement> getShuffledTemplates(Random var1) {
      return ImmutableList.copyOf((StructurePoolElement[])ObjectArrays.shuffle((StructurePoolElement[])this.templates.toArray(new StructurePoolElement[0]), var1));
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public int size() {
      return this.templates.size();
   }

   static {
      CODEC = RegistryFileCodec.create(Registry.TEMPLATE_POOL_REGISTRY, DIRECT_CODEC);
   }

   public static enum Projection implements StringRepresentable {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      public static final Codec<StructureTemplatePool.Projection> CODEC = StringRepresentable.fromEnum(StructureTemplatePool.Projection::values, StructureTemplatePool.Projection::byName);
      private static final Map<String, StructureTemplatePool.Projection> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(StructureTemplatePool.Projection::getName, (var0) -> {
         return var0;
      }));
      private final String name;
      private final ImmutableList<StructureProcessor> processors;

      private Projection(String var3, ImmutableList<StructureProcessor> var4) {
         this.name = var3;
         this.processors = var4;
      }

      public String getName() {
         return this.name;
      }

      public static StructureTemplatePool.Projection byName(String var0) {
         return (StructureTemplatePool.Projection)BY_NAME.get(var0);
      }

      public ImmutableList<StructureProcessor> getProcessors() {
         return this.processors;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static StructureTemplatePool.Projection[] $values() {
         return new StructureTemplatePool.Projection[]{TERRAIN_MATCHING, RIGID};
      }
   }
}
