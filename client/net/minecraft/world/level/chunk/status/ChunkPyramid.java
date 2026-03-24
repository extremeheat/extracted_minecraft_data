package net.minecraft.world.level.chunk.status;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public record ChunkPyramid(ImmutableList<ChunkStep> steps) {
   public static final ChunkPyramid GENERATION_PYRAMID;
   public static final ChunkPyramid LOADING_PYRAMID;

   public ChunkPyramid {
      super();
   }

   public ChunkStep getStepTo(final ChunkStatus status) {
      return (ChunkStep)this.steps.get(status.getIndex());
   }

   static {
      GENERATION_PYRAMID = (new Builder()).step(ChunkStatus.EMPTY, (s) -> s).step(ChunkStatus.STRUCTURE_STARTS, (s) -> s.setTask(ChunkStatusTasks::generateStructureStarts)).step(ChunkStatus.STRUCTURE_REFERENCES, (s) -> s.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).setTask(ChunkStatusTasks::generateStructureReferences)).step(ChunkStatus.BIOMES, (s) -> s.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).setTask(ChunkStatusTasks::generateBiomes)).step(ChunkStatus.NOISE, (s) -> s.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.BIOMES, 1).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateNoise)).step(ChunkStatus.SURFACE, (s) -> s.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.BIOMES, 1).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateSurface)).step(ChunkStatus.CARVERS, (s) -> s.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).blockStateWriteRadius(0).setTask(ChunkStatusTasks::generateCarvers)).step(ChunkStatus.FEATURES, (s) -> s.addRequirement(ChunkStatus.STRUCTURE_STARTS, 8).addRequirement(ChunkStatus.CARVERS, 1).blockStateWriteRadius(1).setTask(ChunkStatusTasks::generateFeatures)).step(ChunkStatus.INITIALIZE_LIGHT, (s) -> s.setTask(ChunkStatusTasks::initializeLight)).step(ChunkStatus.LIGHT, (s) -> s.addRequirement(ChunkStatus.INITIALIZE_LIGHT, 1).setTask(ChunkStatusTasks::light)).step(ChunkStatus.SPAWN, (s) -> s.addRequirement(ChunkStatus.BIOMES, 1).setTask(ChunkStatusTasks::generateSpawn)).step(ChunkStatus.FULL, (s) -> s.setTask(ChunkStatusTasks::full)).build();
      LOADING_PYRAMID = (new Builder()).step(ChunkStatus.EMPTY, (s) -> s).step(ChunkStatus.STRUCTURE_STARTS, (s) -> s.setTask(ChunkStatusTasks::loadStructureStarts)).step(ChunkStatus.STRUCTURE_REFERENCES, (s) -> s).step(ChunkStatus.BIOMES, (s) -> s).step(ChunkStatus.NOISE, (s) -> s).step(ChunkStatus.SURFACE, (s) -> s).step(ChunkStatus.CARVERS, (s) -> s).step(ChunkStatus.FEATURES, (s) -> s).step(ChunkStatus.INITIALIZE_LIGHT, (s) -> s.setTask(ChunkStatusTasks::initializeLight)).step(ChunkStatus.LIGHT, (s) -> s.addRequirement(ChunkStatus.INITIALIZE_LIGHT, 1).setTask(ChunkStatusTasks::light)).step(ChunkStatus.SPAWN, (s) -> s).step(ChunkStatus.FULL, (s) -> s.setTask(ChunkStatusTasks::full)).build();
   }

   public static class Builder {
      private final List<ChunkStep> steps = new ArrayList();

      public Builder() {
         super();
      }

      public ChunkPyramid build() {
         return new ChunkPyramid(ImmutableList.copyOf(this.steps));
      }

      public Builder step(final ChunkStatus status, final UnaryOperator<ChunkStep.Builder> operator) {
         ChunkStep.Builder stepBuilder;
         if (this.steps.isEmpty()) {
            stepBuilder = new ChunkStep.Builder(status);
         } else {
            stepBuilder = new ChunkStep.Builder(status, (ChunkStep)this.steps.getLast());
         }

         this.steps.add(((ChunkStep.Builder)operator.apply(stepBuilder)).build());
         return this;
      }
   }
}
