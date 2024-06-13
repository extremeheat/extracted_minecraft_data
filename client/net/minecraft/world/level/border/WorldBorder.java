package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicLike;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldBorder {
   public static final double MAX_SIZE = 5.9999968E7;
   public static final double MAX_CENTER_COORDINATE = 2.9999984E7;
   private final List<BorderChangeListener> listeners = Lists.newArrayList();
   private double damagePerBlock = 0.2;
   private double damageSafeZone = 5.0;
   private int warningTime = 15;
   private int warningBlocks = 5;
   private double centerX;
   private double centerZ;
   int absoluteMaxSize = 29999984;
   private WorldBorder.BorderExtent extent = new WorldBorder.StaticBorderExtent(5.9999968E7);
   public static final WorldBorder.Settings DEFAULT_SETTINGS = new WorldBorder.Settings(0.0, 0.0, 0.2, 5.0, 5, 15, 5.9999968E7, 0L, 0.0);

   public WorldBorder() {
      super();
   }

   public boolean isWithinBounds(BlockPos var1) {
      return (double)(var1.getX() + 1) > this.getMinX()
         && (double)var1.getX() < this.getMaxX()
         && (double)(var1.getZ() + 1) > this.getMinZ()
         && (double)var1.getZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(ChunkPos var1) {
      return (double)var1.getMaxBlockX() > this.getMinX()
         && (double)var1.getMinBlockX() < this.getMaxX()
         && (double)var1.getMaxBlockZ() > this.getMinZ()
         && (double)var1.getMinBlockZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(double var1, double var3) {
      return var1 > this.getMinX() && var1 < this.getMaxX() && var3 > this.getMinZ() && var3 < this.getMaxZ();
   }

   public boolean isWithinBounds(double var1, double var3, double var5) {
      return var1 > this.getMinX() - var5 && var1 < this.getMaxX() + var5 && var3 > this.getMinZ() - var5 && var3 < this.getMaxZ() + var5;
   }

   public boolean isWithinBounds(AABB var1) {
      return var1.maxX > this.getMinX() && var1.minX < this.getMaxX() && var1.maxZ > this.getMinZ() && var1.minZ < this.getMaxZ();
   }

   public BlockPos clampToBounds(double var1, double var3, double var5) {
      return BlockPos.containing(Mth.clamp(var1, this.getMinX(), this.getMaxX()), var3, Mth.clamp(var5, this.getMinZ(), this.getMaxZ()));
   }

   public double getDistanceToBorder(Entity var1) {
      return this.getDistanceToBorder(var1.getX(), var1.getZ());
   }

   public VoxelShape getCollisionShape() {
      return this.extent.getCollisionShape();
   }

   public double getDistanceToBorder(double var1, double var3) {
      double var5 = var3 - this.getMinZ();
      double var7 = this.getMaxZ() - var3;
      double var9 = var1 - this.getMinX();
      double var11 = this.getMaxX() - var1;
      double var13 = Math.min(var9, var11);
      var13 = Math.min(var13, var5);
      return Math.min(var13, var7);
   }

   public boolean isInsideCloseToBorder(Entity var1, AABB var2) {
      double var3 = Math.max(Mth.absMax(var2.getXsize(), var2.getZsize()), 1.0);
      return this.getDistanceToBorder(var1) < var3 * 2.0 && this.isWithinBounds(var1.getX(), var1.getZ(), var3);
   }

   public BorderStatus getStatus() {
      return this.extent.getStatus();
   }

   public double getMinX() {
      return this.extent.getMinX();
   }

   public double getMinZ() {
      return this.extent.getMinZ();
   }

   public double getMaxX() {
      return this.extent.getMaxX();
   }

   public double getMaxZ() {
      return this.extent.getMaxZ();
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double var1, double var3) {
      this.centerX = var1;
      this.centerZ = var3;
      this.extent.onCenterChange();

      for (BorderChangeListener var6 : this.getListeners()) {
         var6.onBorderCenterSet(this, var1, var3);
      }
   }

   public double getSize() {
      return this.extent.getSize();
   }

   public long getLerpRemainingTime() {
      return this.extent.getLerpRemainingTime();
   }

   public double getLerpTarget() {
      return this.extent.getLerpTarget();
   }

   public void setSize(double var1) {
      this.extent = new WorldBorder.StaticBorderExtent(var1);

      for (BorderChangeListener var4 : this.getListeners()) {
         var4.onBorderSizeSet(this, var1);
      }
   }

   public void lerpSizeBetween(double var1, double var3, long var5) {
      this.extent = (WorldBorder.BorderExtent)(var1 == var3 ? new WorldBorder.StaticBorderExtent(var3) : new WorldBorder.MovingBorderExtent(var1, var3, var5));

      for (BorderChangeListener var8 : this.getListeners()) {
         var8.onBorderSizeLerping(this, var1, var3, var5);
      }
   }

   protected List<BorderChangeListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(BorderChangeListener var1) {
      this.listeners.add(var1);
   }

   public void removeListener(BorderChangeListener var1) {
      this.listeners.remove(var1);
   }

   public void setAbsoluteMaxSize(int var1) {
      this.absoluteMaxSize = var1;
      this.extent.onAbsoluteMaxSizeChange();
   }

   public int getAbsoluteMaxSize() {
      return this.absoluteMaxSize;
   }

   public double getDamageSafeZone() {
      return this.damageSafeZone;
   }

   public void setDamageSafeZone(double var1) {
      this.damageSafeZone = var1;

      for (BorderChangeListener var4 : this.getListeners()) {
         var4.onBorderSetDamageSafeZOne(this, var1);
      }
   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double var1) {
      this.damagePerBlock = var1;

      for (BorderChangeListener var4 : this.getListeners()) {
         var4.onBorderSetDamagePerBlock(this, var1);
      }
   }

   public double getLerpSpeed() {
      return this.extent.getLerpSpeed();
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int var1) {
      this.warningTime = var1;

      for (BorderChangeListener var3 : this.getListeners()) {
         var3.onBorderSetWarningTime(this, var1);
      }
   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }

   public void setWarningBlocks(int var1) {
      this.warningBlocks = var1;

      for (BorderChangeListener var3 : this.getListeners()) {
         var3.onBorderSetWarningBlocks(this, var1);
      }
   }

   public void tick() {
      this.extent = this.extent.update();
   }

   public WorldBorder.Settings createSettings() {
      return new WorldBorder.Settings(this);
   }

   public void applySettings(WorldBorder.Settings var1) {
      this.setCenter(var1.getCenterX(), var1.getCenterZ());
      this.setDamagePerBlock(var1.getDamagePerBlock());
      this.setDamageSafeZone(var1.getSafeZone());
      this.setWarningBlocks(var1.getWarningBlocks());
      this.setWarningTime(var1.getWarningTime());
      if (var1.getSizeLerpTime() > 0L) {
         this.lerpSizeBetween(var1.getSize(), var1.getSizeLerpTarget(), var1.getSizeLerpTime());
      } else {
         this.setSize(var1.getSize());
      }
   }

   interface BorderExtent {
      double getMinX();

      double getMaxX();

      double getMinZ();

      double getMaxZ();

      double getSize();

      double getLerpSpeed();

      long getLerpRemainingTime();

      double getLerpTarget();

      BorderStatus getStatus();

      void onAbsoluteMaxSizeChange();

      void onCenterChange();

      WorldBorder.BorderExtent update();

      VoxelShape getCollisionShape();
   }

   class MovingBorderExtent implements WorldBorder.BorderExtent {
      private final double from;
      private final double to;
      private final long lerpEnd;
      private final long lerpBegin;
      private final double lerpDuration;

      MovingBorderExtent(double var2, double var4, long var6) {
         super();
         this.from = var2;
         this.to = var4;
         this.lerpDuration = (double)var6;
         this.lerpBegin = Util.getMillis();
         this.lerpEnd = this.lerpBegin + var6;
      }

      @Override
      public double getMinX() {
         return Mth.clamp(
            WorldBorder.this.getCenterX() - this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
      }

      @Override
      public double getMinZ() {
         return Mth.clamp(
            WorldBorder.this.getCenterZ() - this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
      }

      @Override
      public double getMaxX() {
         return Mth.clamp(
            WorldBorder.this.getCenterX() + this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
      }

      @Override
      public double getMaxZ() {
         return Mth.clamp(
            WorldBorder.this.getCenterZ() + this.getSize() / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
      }

      @Override
      public double getSize() {
         double var1 = (double)(Util.getMillis() - this.lerpBegin) / this.lerpDuration;
         return var1 < 1.0 ? Mth.lerp(var1, this.from, this.to) : this.to;
      }

      @Override
      public double getLerpSpeed() {
         return Math.abs(this.from - this.to) / (double)(this.lerpEnd - this.lerpBegin);
      }

      @Override
      public long getLerpRemainingTime() {
         return this.lerpEnd - Util.getMillis();
      }

      @Override
      public double getLerpTarget() {
         return this.to;
      }

      @Override
      public BorderStatus getStatus() {
         return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
      }

      @Override
      public void onCenterChange() {
      }

      @Override
      public void onAbsoluteMaxSizeChange() {
      }

      @Override
      public WorldBorder.BorderExtent update() {
         return (WorldBorder.BorderExtent)(this.getLerpRemainingTime() <= 0L ? WorldBorder.this.new StaticBorderExtent(this.to) : this);
      }

      @Override
      public VoxelShape getCollisionShape() {
         return Shapes.join(
            Shapes.INFINITY,
            Shapes.box(Math.floor(this.getMinX()), -1.0 / 0.0, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), 1.0 / 0.0, Math.ceil(this.getMaxZ())),
            BooleanOp.ONLY_FIRST
         );
      }
   }

   public static class Settings {
      private final double centerX;
      private final double centerZ;
      private final double damagePerBlock;
      private final double safeZone;
      private final int warningBlocks;
      private final int warningTime;
      private final double size;
      private final long sizeLerpTime;
      private final double sizeLerpTarget;

      Settings(double var1, double var3, double var5, double var7, int var9, int var10, double var11, long var13, double var15) {
         super();
         this.centerX = var1;
         this.centerZ = var3;
         this.damagePerBlock = var5;
         this.safeZone = var7;
         this.warningBlocks = var9;
         this.warningTime = var10;
         this.size = var11;
         this.sizeLerpTime = var13;
         this.sizeLerpTarget = var15;
      }

      Settings(WorldBorder var1) {
         super();
         this.centerX = var1.getCenterX();
         this.centerZ = var1.getCenterZ();
         this.damagePerBlock = var1.getDamagePerBlock();
         this.safeZone = var1.getDamageSafeZone();
         this.warningBlocks = var1.getWarningBlocks();
         this.warningTime = var1.getWarningTime();
         this.size = var1.getSize();
         this.sizeLerpTime = var1.getLerpRemainingTime();
         this.sizeLerpTarget = var1.getLerpTarget();
      }

      public double getCenterX() {
         return this.centerX;
      }

      public double getCenterZ() {
         return this.centerZ;
      }

      public double getDamagePerBlock() {
         return this.damagePerBlock;
      }

      public double getSafeZone() {
         return this.safeZone;
      }

      public int getWarningBlocks() {
         return this.warningBlocks;
      }

      public int getWarningTime() {
         return this.warningTime;
      }

      public double getSize() {
         return this.size;
      }

      public long getSizeLerpTime() {
         return this.sizeLerpTime;
      }

      public double getSizeLerpTarget() {
         return this.sizeLerpTarget;
      }

      public static WorldBorder.Settings read(DynamicLike<?> var0, WorldBorder.Settings var1) {
         double var2 = Mth.clamp(var0.get("BorderCenterX").asDouble(var1.centerX), -2.9999984E7, 2.9999984E7);
         double var4 = Mth.clamp(var0.get("BorderCenterZ").asDouble(var1.centerZ), -2.9999984E7, 2.9999984E7);
         double var6 = var0.get("BorderSize").asDouble(var1.size);
         long var8 = var0.get("BorderSizeLerpTime").asLong(var1.sizeLerpTime);
         double var10 = var0.get("BorderSizeLerpTarget").asDouble(var1.sizeLerpTarget);
         double var12 = var0.get("BorderSafeZone").asDouble(var1.safeZone);
         double var14 = var0.get("BorderDamagePerBlock").asDouble(var1.damagePerBlock);
         int var16 = var0.get("BorderWarningBlocks").asInt(var1.warningBlocks);
         int var17 = var0.get("BorderWarningTime").asInt(var1.warningTime);
         return new WorldBorder.Settings(var2, var4, var14, var12, var16, var17, var6, var8, var10);
      }

      public void write(CompoundTag var1) {
         var1.putDouble("BorderCenterX", this.centerX);
         var1.putDouble("BorderCenterZ", this.centerZ);
         var1.putDouble("BorderSize", this.size);
         var1.putLong("BorderSizeLerpTime", this.sizeLerpTime);
         var1.putDouble("BorderSafeZone", this.safeZone);
         var1.putDouble("BorderDamagePerBlock", this.damagePerBlock);
         var1.putDouble("BorderSizeLerpTarget", this.sizeLerpTarget);
         var1.putDouble("BorderWarningBlocks", (double)this.warningBlocks);
         var1.putDouble("BorderWarningTime", (double)this.warningTime);
      }
   }

   class StaticBorderExtent implements WorldBorder.BorderExtent {
      private final double size;
      private double minX;
      private double minZ;
      private double maxX;
      private double maxZ;
      private VoxelShape shape;

      public StaticBorderExtent(double var2) {
         super();
         this.size = var2;
         this.updateBox();
      }

      @Override
      public double getMinX() {
         return this.minX;
      }

      @Override
      public double getMaxX() {
         return this.maxX;
      }

      @Override
      public double getMinZ() {
         return this.minZ;
      }

      @Override
      public double getMaxZ() {
         return this.maxZ;
      }

      @Override
      public double getSize() {
         return this.size;
      }

      @Override
      public BorderStatus getStatus() {
         return BorderStatus.STATIONARY;
      }

      @Override
      public double getLerpSpeed() {
         return 0.0;
      }

      @Override
      public long getLerpRemainingTime() {
         return 0L;
      }

      @Override
      public double getLerpTarget() {
         return this.size;
      }

      private void updateBox() {
         this.minX = Mth.clamp(
            WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
         this.minZ = Mth.clamp(
            WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
         this.maxX = Mth.clamp(
            WorldBorder.this.getCenterX() + this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
         this.maxZ = Mth.clamp(
            WorldBorder.this.getCenterZ() + this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize
         );
         this.shape = Shapes.join(
            Shapes.INFINITY,
            Shapes.box(Math.floor(this.getMinX()), -1.0 / 0.0, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), 1.0 / 0.0, Math.ceil(this.getMaxZ())),
            BooleanOp.ONLY_FIRST
         );
      }

      @Override
      public void onAbsoluteMaxSizeChange() {
         this.updateBox();
      }

      @Override
      public void onCenterChange() {
         this.updateBox();
      }

      @Override
      public WorldBorder.BorderExtent update() {
         return this;
      }

      @Override
      public VoxelShape getCollisionShape() {
         return this.shape;
      }
   }
}
