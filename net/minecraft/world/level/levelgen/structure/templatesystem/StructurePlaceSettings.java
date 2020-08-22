package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructurePlaceSettings {
   private Mirror mirror;
   private Rotation rotation;
   private BlockPos rotationPivot;
   private boolean ignoreEntities;
   @Nullable
   private ChunkPos chunkPos;
   @Nullable
   private BoundingBox boundingBox;
   private boolean keepLiquids;
   @Nullable
   private Random random;
   @Nullable
   private int palette;
   private final List processors;
   private boolean knownShape;

   public StructurePlaceSettings() {
      this.mirror = Mirror.NONE;
      this.rotation = Rotation.NONE;
      this.rotationPivot = BlockPos.ZERO;
      this.keepLiquids = true;
      this.processors = Lists.newArrayList();
   }

   public StructurePlaceSettings copy() {
      StructurePlaceSettings var1 = new StructurePlaceSettings();
      var1.mirror = this.mirror;
      var1.rotation = this.rotation;
      var1.rotationPivot = this.rotationPivot;
      var1.ignoreEntities = this.ignoreEntities;
      var1.chunkPos = this.chunkPos;
      var1.boundingBox = this.boundingBox;
      var1.keepLiquids = this.keepLiquids;
      var1.random = this.random;
      var1.palette = this.palette;
      var1.processors.addAll(this.processors);
      var1.knownShape = this.knownShape;
      return var1;
   }

   public StructurePlaceSettings setMirror(Mirror var1) {
      this.mirror = var1;
      return this;
   }

   public StructurePlaceSettings setRotation(Rotation var1) {
      this.rotation = var1;
      return this;
   }

   public StructurePlaceSettings setRotationPivot(BlockPos var1) {
      this.rotationPivot = var1;
      return this;
   }

   public StructurePlaceSettings setIgnoreEntities(boolean var1) {
      this.ignoreEntities = var1;
      return this;
   }

   public StructurePlaceSettings setChunkPos(ChunkPos var1) {
      this.chunkPos = var1;
      return this;
   }

   public StructurePlaceSettings setBoundingBox(BoundingBox var1) {
      this.boundingBox = var1;
      return this;
   }

   public StructurePlaceSettings setRandom(@Nullable Random var1) {
      this.random = var1;
      return this;
   }

   public StructurePlaceSettings setKnownShape(boolean var1) {
      this.knownShape = var1;
      return this;
   }

   public StructurePlaceSettings clearProcessors() {
      this.processors.clear();
      return this;
   }

   public StructurePlaceSettings addProcessor(StructureProcessor var1) {
      this.processors.add(var1);
      return this;
   }

   public StructurePlaceSettings popProcessor(StructureProcessor var1) {
      this.processors.remove(var1);
      return this;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public BlockPos getRotationPivot() {
      return this.rotationPivot;
   }

   public Random getRandom(@Nullable BlockPos var1) {
      if (this.random != null) {
         return this.random;
      } else {
         return var1 == null ? new Random(Util.getMillis()) : new Random(Mth.getSeed(var1));
      }
   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   @Nullable
   public BoundingBox getBoundingBox() {
      if (this.boundingBox == null && this.chunkPos != null) {
         this.updateBoundingBoxFromChunkPos();
      }

      return this.boundingBox;
   }

   public boolean getKnownShape() {
      return this.knownShape;
   }

   public List getProcessors() {
      return this.processors;
   }

   void updateBoundingBoxFromChunkPos() {
      if (this.chunkPos != null) {
         this.boundingBox = this.calculateBoundingBox(this.chunkPos);
      }

   }

   public boolean shouldKeepLiquids() {
      return this.keepLiquids;
   }

   public List getRandomPalette(List var1, @Nullable BlockPos var2) {
      int var3 = var1.size();
      return var3 > 0 ? (List)var1.get(this.getRandom(var2).nextInt(var3)) : Collections.emptyList();
   }

   @Nullable
   private BoundingBox calculateBoundingBox(@Nullable ChunkPos var1) {
      if (var1 == null) {
         return this.boundingBox;
      } else {
         int var2 = var1.x * 16;
         int var3 = var1.z * 16;
         return new BoundingBox(var2, 0, var3, var2 + 16 - 1, 255, var3 + 16 - 1);
      }
   }
}
