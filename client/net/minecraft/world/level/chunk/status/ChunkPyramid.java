package net.minecraft.world.level.chunk.status;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public record ChunkPyramid(ImmutableList<ChunkStep> steps) {
   public static final ChunkPyramid GENERATION_PYRAMID;
   public static final ChunkPyramid LOADING_PYRAMID;

   public ChunkPyramid(ImmutableList<ChunkStep> var1) {
      super();
      this.steps = var1;
   }

   public ChunkStep getStepTo(ChunkStatus var1) {
      return (ChunkStep)this.steps.get(var1.getIndex());
   }

   static {
      GENERATION_PYRAMID = (new Builder()).step(ChunkStatus.EMPTY, (var0) -> var0).step(ChunkStatus.STRUCTURE_STARTS, (var0) -> var0.setTask(ChunkStatusTasks::generateStructureStarts)).step(ChunkStatus.STRUCTURE_REFERENCES, (var0) -> var0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).setTask(ChunkStatusTasks::generateStructureReferences)).step(ChunkStatus.BIOMES, (var0) -> var0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).setTask(ChunkStatusTasks::generateBiomes)).step(ChunkStatus.NOISE, (var0) -> var0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.BIOMES, 1).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateNoise)).step(ChunkStatus.SURFACE, (var0) -> var0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.BIOMES, 1).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateSurface)).step(ChunkStatus.CARVERS, (var0) -> var0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateCarvers)).step(ChunkStatus.FEATURES, (var0) -> var0.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.CARVERS, 1).blockStateWriteRadius(1).setTask(ChunkStatusTasks::generateFeatures)).step(ChunkStatus.INITIALIZE_LIGHT, (var0) -> var0.setTask(ChunkStatusTasks::initializeLight)).step(ChunkStatus.LIGHT, (var0) -> var0.addRequirement(ChunkStatus.INITIALIZE_LIGHT, 1).setTask(ChunkStatusTasks::light)).step(ChunkStatus.SPAWN, (var0) -> var0.addRequirement(ChunkStatus.BIOMES, 1).setTask(ChunkStatusTasks::generateSpawn)).step(ChunkStatus.FULL, (var0) -> var0.setTask(ChunkStatusTasks::full)).build();
      LOADING_PYRAMID = (new Builder()).step(ChunkStatus.EMPTY, (var0) -> var0).step(ChunkStatus.STRUCTURE_STARTS, (var0) -> var0.setTask(ChunkStatusTasks::loadStructureStarts)).step(ChunkStatus.STRUCTURE_REFERENCES, (var0) -> var0).step(ChunkStatus.BIOMES, (var0) -> var0).step(ChunkStatus.NOISE, (var0) -> var0).step(ChunkStatus.SURFACE, (var0) -> var0).step(ChunkStatus.CARVERS, (var0) -> var0).step(ChunkStatus.FEATURES, (var0) -> var0).step(ChunkStatus.INITIALIZE_LIGHT, (var0) -> var0.setTask(ChunkStatusTasks::initializeLight)).step(ChunkStatus.LIGHT, (var0) -> var0.addRequirement(ChunkStatus.INITIALIZE_LIGHT, 1).setTask(ChunkStatusTasks::light)).step(ChunkStatus.SPAWN, (var0) -> var0).step(ChunkStatus.FULL, (var0) -> var0.setTask(ChunkStatusTasks::full)).build();
   }

   public static class Builder {
      private final List<ChunkStep> steps = new ArrayList();

      public Builder() {
         super();
      }

      public ChunkPyramid build() {
         return new ChunkPyramid(ImmutableList.copyOf(this.steps));
      }

      public Builder step(ChunkStatus var1, UnaryOperator<ChunkStep.Builder> var2) {
         ChunkStep.Builder var3;
         if (this.steps.isEmpty()) {
            var3 = new ChunkStep.Builder(var1);
         } else {
            var3 = new ChunkStep.Builder(var1, (ChunkStep)this.steps.getLast());
         }

         this.steps.add(((ChunkStep.Builder)var2.apply(var3)).build());
         return this;
      }
   }
}
