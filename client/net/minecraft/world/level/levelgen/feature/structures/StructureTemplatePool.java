package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

public class StructureTemplatePool {
   public static final StructureTemplatePool EMPTY;
   public static final StructureTemplatePool INVALID;
   private final ResourceLocation name;
   private final ImmutableList<Pair<StructurePoolElement, Integer>> rawTemplates;
   private final List<StructurePoolElement> templates;
   private final ResourceLocation fallback;
   private final StructureTemplatePool.Projection projection;
   private int maxSize = -2147483648;

   public StructureTemplatePool(ResourceLocation var1, ResourceLocation var2, List<Pair<StructurePoolElement, Integer>> var3, StructureTemplatePool.Projection var4) {
      super();
      this.name = var1;
      this.rawTemplates = ImmutableList.copyOf(var3);
      this.templates = Lists.newArrayList();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         Pair var6 = (Pair)var5.next();

         for(Integer var7 = 0; var7 < (Integer)var6.getSecond(); var7 = var7 + 1) {
            this.templates.add(((StructurePoolElement)var6.getFirst()).setProjection(var4));
         }
      }

      this.fallback = var2;
      this.projection = var4;
   }

   public int getMaxSize(StructureManager var1) {
      if (this.maxSize == -2147483648) {
         this.maxSize = this.templates.stream().mapToInt((var1x) -> {
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
      return ImmutableList.copyOf(ObjectArrays.shuffle(this.templates.toArray(new StructurePoolElement[0]), var1));
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public int size() {
      return this.templates.size();
   }

   static {
      EMPTY = new StructureTemplatePool(new ResourceLocation("empty"), new ResourceLocation("empty"), ImmutableList.of(), StructureTemplatePool.Projection.RIGID);
      INVALID = new StructureTemplatePool(new ResourceLocation("invalid"), new ResourceLocation("invalid"), ImmutableList.of(), StructureTemplatePool.Projection.RIGID);
   }

   public static enum Projection {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

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
   }
}
