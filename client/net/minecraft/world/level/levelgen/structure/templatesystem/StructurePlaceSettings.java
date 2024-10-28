package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructurePlaceSettings {
   private Mirror mirror;
   private Rotation rotation;
   private BlockPos rotationPivot;
   private boolean ignoreEntities;
   @Nullable
   private BoundingBox boundingBox;
   private LiquidSettings liquidSettings;
   @Nullable
   private RandomSource random;
   private int palette;
   private final List<StructureProcessor> processors;
   private boolean knownShape;
   private boolean finalizeEntities;

   public StructurePlaceSettings() {
      super();
      this.mirror = Mirror.NONE;
      this.rotation = Rotation.NONE;
      this.rotationPivot = BlockPos.ZERO;
      this.liquidSettings = LiquidSettings.APPLY_WATERLOGGING;
      this.processors = Lists.newArrayList();
   }

   public StructurePlaceSettings copy() {
      StructurePlaceSettings var1 = new StructurePlaceSettings();
      var1.mirror = this.mirror;
      var1.rotation = this.rotation;
      var1.rotationPivot = this.rotationPivot;
      var1.ignoreEntities = this.ignoreEntities;
      var1.boundingBox = this.boundingBox;
      var1.liquidSettings = this.liquidSettings;
      var1.random = this.random;
      var1.palette = this.palette;
      var1.processors.addAll(this.processors);
      var1.knownShape = this.knownShape;
      var1.finalizeEntities = this.finalizeEntities;
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

   public StructurePlaceSettings setBoundingBox(BoundingBox var1) {
      this.boundingBox = var1;
      return this;
   }

   public StructurePlaceSettings setRandom(@Nullable RandomSource var1) {
      this.random = var1;
      return this;
   }

   public StructurePlaceSettings setLiquidSettings(LiquidSettings var1) {
      this.liquidSettings = var1;
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

   public RandomSource getRandom(@Nullable BlockPos var1) {
      if (this.random != null) {
         return this.random;
      } else {
         return var1 == null ? RandomSource.create(Util.getMillis()) : RandomSource.create(Mth.getSeed(var1));
      }
   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   @Nullable
   public BoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public boolean getKnownShape() {
      return this.knownShape;
   }

   public List<StructureProcessor> getProcessors() {
      return this.processors;
   }

   public boolean shouldApplyWaterlogging() {
      return this.liquidSettings == LiquidSettings.APPLY_WATERLOGGING;
   }

   public StructureTemplate.Palette getRandomPalette(List<StructureTemplate.Palette> var1, @Nullable BlockPos var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         throw new IllegalStateException("No palettes");
      } else {
         return (StructureTemplate.Palette)var1.get(this.getRandom(var2).nextInt(var3));
      }
   }

   public StructurePlaceSettings setFinalizeEntities(boolean var1) {
      this.finalizeEntities = var1;
      return this;
   }

   public boolean shouldFinalizeEntities() {
      return this.finalizeEntities;
   }
}
