package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.LevelData;
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
      return this.getDistanceToBorder(var1.x, var1.z);
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

   public void saveWorldBorderData(LevelData var1) {
      var1.setBorderSize(this.getSize());
      var1.setBorderX(this.getCenterX());
      var1.setBorderZ(this.getCenterZ());
      var1.setBorderSafeZone(this.getDamageSafeZone());
      var1.setBorderDamagePerBlock(this.getDamagePerBlock());
      var1.setBorderWarningBlocks(this.getWarningBlocks());
      var1.setBorderWarningTime(this.getWarningTime());
      var1.setBorderSizeLerpTarget(this.getLerpTarget());
      var1.setBorderSizeLerpTime(this.getLerpRemainingTime());
   }

   public void readBorderData(LevelData var1) {
      this.setCenter(var1.getBorderX(), var1.getBorderZ());
      this.setDamagePerBlock(var1.getBorderDamagePerBlock());
      this.setDamageSafeZone(var1.getBorderSafeZone());
      this.setWarningBlocks(var1.getBorderWarningBlocks());
      this.setWarningTime(var1.getBorderWarningTime());
      if (var1.getBorderSizeLerpTime() > 0L) {
         this.lerpSizeBetween(var1.getBorderSize(), var1.getBorderSizeLerpTarget(), var1.getBorderSizeLerpTime());
      } else {
         this.setSize(var1.getBorderSize());
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
