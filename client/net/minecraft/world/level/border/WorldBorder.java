package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicLike;
import java.util.Iterator;
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
   private final List<BorderChangeListener> listeners = Lists.newArrayList();
   private double damagePerBlock = 0.2D;
   private double damageSafeZone = 5.0D;
   private int warningTime = 15;
   private int warningBlocks = 5;
   private double centerX;
   private double centerZ;
   private int absoluteMaxSize = 29999984;
   private WorldBorder.BorderExtent extent = new WorldBorder.StaticBorderExtent(6.0E7D);
   public static final WorldBorder.Settings DEFAULT_SETTINGS = new WorldBorder.Settings(0.0D, 0.0D, 0.2D, 5.0D, 5, 15, 6.0E7D, 0L, 0.0D);

   public WorldBorder() {
      super();
   }

   public boolean isWithinBounds(BlockPos var1) {
      return (double)(var1.getX() + 1) > this.getMinX() && (double)var1.getX() < this.getMaxX() && (double)(var1.getZ() + 1) > this.getMinZ() && (double)var1.getZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(ChunkPos var1) {
      return (double)var1.getMaxBlockX() > this.getMinX() && (double)var1.getMinBlockX() < this.getMaxX() && (double)var1.getMaxBlockZ() > this.getMinZ() && (double)var1.getMinBlockZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(AABB var1) {
      return var1.maxX > this.getMinX() && var1.minX < this.getMaxX() && var1.maxZ > this.getMinZ() && var1.minZ < this.getMaxZ();
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
      Iterator var5 = this.getListeners().iterator();

      while(var5.hasNext()) {
         BorderChangeListener var6 = (BorderChangeListener)var5.next();
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
      Iterator var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
         BorderChangeListener var4 = (BorderChangeListener)var3.next();
         var4.onBorderSizeSet(this, var1);
      }

   }

   public void lerpSizeBetween(double var1, double var3, long var5) {
      this.extent = (WorldBorder.BorderExtent)(var1 == var3 ? new WorldBorder.StaticBorderExtent(var3) : new WorldBorder.MovingBorderExtent(var1, var3, var5));
      Iterator var7 = this.getListeners().iterator();

      while(var7.hasNext()) {
         BorderChangeListener var8 = (BorderChangeListener)var7.next();
         var8.onBorderSizeLerping(this, var1, var3, var5);
      }

   }

   protected List<BorderChangeListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(BorderChangeListener var1) {
      this.listeners.add(var1);
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
      Iterator var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
         BorderChangeListener var4 = (BorderChangeListener)var3.next();
         var4.onBorderSetDamageSafeZOne(this, var1);
      }

   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double var1) {
      this.damagePerBlock = var1;
      Iterator var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
         BorderChangeListener var4 = (BorderChangeListener)var3.next();
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
      Iterator var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
         BorderChangeListener var3 = (BorderChangeListener)var2.next();
         var3.onBorderSetWarningTime(this, var1);
      }

   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }

   public void setWarningBlocks(int var1) {
      this.warningBlocks = var1;
      Iterator var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
         BorderChangeListener var3 = (BorderChangeListener)var2.next();
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

      private Settings(double var1, double var3, double var5, double var7, int var9, int var10, double var11, long var13, double var15) {
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

      private Settings(WorldBorder var1) {
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
         double var2 = var0.get("BorderCenterX").asDouble(var1.centerX);
         double var4 = var0.get("BorderCenterZ").asDouble(var1.centerZ);
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

      // $FF: synthetic method
      Settings(WorldBorder var1, Object var2) {
         this(var1);
      }

      // $FF: synthetic method
      Settings(double var1, double var3, double var5, double var7, int var9, int var10, double var11, long var13, double var15, Object var17) {
         this(var1, var3, var5, var7, var9, var10, var11, var13, var15);
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

      public double getMinX() {
         return this.minX;
      }

      public double getMaxX() {
         return this.maxX;
      }

      public double getMinZ() {
         return this.minZ;
      }

      public double getMaxZ() {
         return this.maxZ;
      }

      public double getSize() {
         return this.size;
      }

      public BorderStatus getStatus() {
         return BorderStatus.STATIONARY;
      }

      public double getLerpSpeed() {
         return 0.0D;
      }

      public long getLerpRemainingTime() {
         return 0L;
      }

      public double getLerpTarget() {
         return this.size;
      }

      private void updateBox() {
         this.minX = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
         this.minZ = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
         this.maxX = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
         this.maxZ = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
         this.shape = Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX()), -1.0D / 0.0, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), 1.0D / 0.0, Math.ceil(this.getMaxZ())), BooleanOp.ONLY_FIRST);
      }

      public void onAbsoluteMaxSizeChange() {
         this.updateBox();
      }

      public void onCenterChange() {
         this.updateBox();
      }

      public WorldBorder.BorderExtent update() {
         return this;
      }

      public VoxelShape getCollisionShape() {
         return this.shape;
      }
   }

   class MovingBorderExtent implements WorldBorder.BorderExtent {
      private final double from;
      private final double to;
      private final long lerpEnd;
      private final long lerpBegin;
      private final double lerpDuration;

      private MovingBorderExtent(double var2, double var4, long var6) {
         super();
         this.from = var2;
         this.to = var4;
         this.lerpDuration = (double)var6;
         this.lerpBegin = Util.getMillis();
         this.lerpEnd = this.lerpBegin + var6;
      }

      public double getMinX() {
         return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
      }

      public double getMinZ() {
         return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
      }

      public double getMaxX() {
         return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMaxZ() {
         return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getSize() {
         double var1 = (double)(Util.getMillis() - this.lerpBegin) / this.lerpDuration;
         return var1 < 1.0D ? Mth.lerp(var1, this.from, this.to) : this.to;
      }

      public double getLerpSpeed() {
         return Math.abs(this.from - this.to) / (double)(this.lerpEnd - this.lerpBegin);
      }

      public long getLerpRemainingTime() {
         return this.lerpEnd - Util.getMillis();
      }

      public double getLerpTarget() {
         return this.to;
      }

      public BorderStatus getStatus() {
         return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
      }

      public void onCenterChange() {
      }

      public void onAbsoluteMaxSizeChange() {
      }

      public WorldBorder.BorderExtent update() {
         return (WorldBorder.BorderExtent)(this.getLerpRemainingTime() <= 0L ? WorldBorder.this.new StaticBorderExtent(this.to) : this);
      }

      public VoxelShape getCollisionShape() {
         return Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX()), -1.0D / 0.0, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), 1.0D / 0.0, Math.ceil(this.getMaxZ())), BooleanOp.ONLY_FIRST);
      }

      // $FF: synthetic method
      MovingBorderExtent(double var2, double var4, long var6, Object var8) {
         this(var2, var4, var6);
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
}
