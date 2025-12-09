package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldBorder extends SavedData {
   public static final double MAX_SIZE = 5.9999968E7;
   public static final double MAX_CENTER_COORDINATE = 2.9999984E7;
   public static final Codec<WorldBorder> CODEC;
   public static final SavedDataType<WorldBorder> TYPE;
   private final Settings settings;
   private boolean initialized;
   private final List<BorderChangeListener> listeners;
   double damagePerBlock;
   double safeZone;
   int warningTime;
   int warningBlocks;
   double centerX;
   double centerZ;
   int absoluteMaxSize;
   BorderExtent extent;

   public WorldBorder() {
      this(WorldBorder.Settings.DEFAULT);
   }

   public WorldBorder(Settings var1) {
      super();
      this.listeners = Lists.newArrayList();
      this.damagePerBlock = 0.2;
      this.safeZone = 5.0;
      this.warningTime = 15;
      this.warningBlocks = 5;
      this.absoluteMaxSize = 29999984;
      this.extent = new StaticBorderExtent(5.9999968E7);
      this.settings = var1;
   }

   public boolean isWithinBounds(BlockPos var1) {
      return this.isWithinBounds((double)var1.getX(), (double)var1.getZ());
   }

   public boolean isWithinBounds(Vec3 var1) {
      return this.isWithinBounds(var1.x, var1.z);
   }

   public boolean isWithinBounds(ChunkPos var1) {
      return this.isWithinBounds((double)var1.getMinBlockX(), (double)var1.getMinBlockZ()) && this.isWithinBounds((double)var1.getMaxBlockX(), (double)var1.getMaxBlockZ());
   }

   public boolean isWithinBounds(AABB var1) {
      return this.isWithinBounds(var1.minX, var1.minZ, var1.maxX - 9.999999747378752E-6, var1.maxZ - 9.999999747378752E-6);
   }

   private boolean isWithinBounds(double var1, double var3, double var5, double var7) {
      return this.isWithinBounds(var1, var3) && this.isWithinBounds(var5, var7);
   }

   public boolean isWithinBounds(double var1, double var3) {
      return this.isWithinBounds(var1, var3, 0.0);
   }

   public boolean isWithinBounds(double var1, double var3, double var5) {
      return var1 >= this.getMinX() - var5 && var1 < this.getMaxX() + var5 && var3 >= this.getMinZ() - var5 && var3 < this.getMaxZ() + var5;
   }

   public BlockPos clampToBounds(BlockPos var1) {
      return this.clampToBounds((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
   }

   public BlockPos clampToBounds(Vec3 var1) {
      return this.clampToBounds(var1.x(), var1.y(), var1.z());
   }

   public BlockPos clampToBounds(double var1, double var3, double var5) {
      return BlockPos.containing(this.clampVec3ToBound(var1, var3, var5));
   }

   public Vec3 clampVec3ToBound(Vec3 var1) {
      return this.clampVec3ToBound(var1.x, var1.y, var1.z);
   }

   public Vec3 clampVec3ToBound(double var1, double var3, double var5) {
      return new Vec3(Mth.clamp(var1, this.getMinX(), this.getMaxX() - 9.999999747378752E-6), var3, Mth.clamp(var5, this.getMinZ(), this.getMaxZ() - 9.999999747378752E-6));
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
      return this.getMinX(0.0F);
   }

   public double getMinX(float var1) {
      return this.extent.getMinX(var1);
   }

   public double getMinZ() {
      return this.getMinZ(0.0F);
   }

   public double getMinZ(float var1) {
      return this.extent.getMinZ(var1);
   }

   public double getMaxX() {
      return this.getMaxX(0.0F);
   }

   public double getMaxX(float var1) {
      return this.extent.getMaxX(var1);
   }

   public double getMaxZ() {
      return this.getMaxZ(0.0F);
   }

   public double getMaxZ(float var1) {
      return this.extent.getMaxZ(var1);
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
      this.setDirty();

      for(BorderChangeListener var6 : this.getListeners()) {
         var6.onSetCenter(this, var1, var3);
      }

   }

   public double getSize() {
      return this.extent.getSize();
   }

   public long getLerpTime() {
      return this.extent.getLerpTime();
   }

   public double getLerpTarget() {
      return this.extent.getLerpTarget();
   }

   public void setSize(double var1) {
      this.extent = new StaticBorderExtent(var1);
      this.setDirty();

      for(BorderChangeListener var4 : this.getListeners()) {
         var4.onSetSize(this, var1);
      }

   }

   public void lerpSizeBetween(double var1, double var3, long var5, long var7) {
      this.extent = (BorderExtent)(var1 == var3 ? new StaticBorderExtent(var3) : new MovingBorderExtent(var1, var3, var5, var7));
      this.setDirty();

      for(BorderChangeListener var10 : this.getListeners()) {
         var10.onLerpSize(this, var1, var3, var5, var7);
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

   public double getSafeZone() {
      return this.safeZone;
   }

   public void setSafeZone(double var1) {
      this.safeZone = var1;
      this.setDirty();

      for(BorderChangeListener var4 : this.getListeners()) {
         var4.onSetSafeZone(this, var1);
      }

   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double var1) {
      this.damagePerBlock = var1;
      this.setDirty();

      for(BorderChangeListener var4 : this.getListeners()) {
         var4.onSetDamagePerBlock(this, var1);
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
      this.setDirty();

      for(BorderChangeListener var3 : this.getListeners()) {
         var3.onSetWarningTime(this, var1);
      }

   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }

   public void setWarningBlocks(int var1) {
      this.warningBlocks = var1;
      this.setDirty();

      for(BorderChangeListener var3 : this.getListeners()) {
         var3.onSetWarningBlocks(this, var1);
      }

   }

   public void tick() {
      this.extent = this.extent.update();
   }

   public void applyInitialSettings(long var1) {
      if (!this.initialized) {
         this.setCenter(this.settings.centerX(), this.settings.centerZ());
         this.setDamagePerBlock(this.settings.damagePerBlock());
         this.setSafeZone(this.settings.safeZone());
         this.setWarningBlocks(this.settings.warningBlocks());
         this.setWarningTime(this.settings.warningTime());
         if (this.settings.lerpTime() > 0L) {
            this.lerpSizeBetween(this.settings.size(), this.settings.lerpTarget(), this.settings.lerpTime(), var1);
         } else {
            this.setSize(this.settings.size());
         }

         this.initialized = true;
      }

   }

   static {
      CODEC = WorldBorder.Settings.CODEC.xmap(WorldBorder::new, Settings::new);
      TYPE = new SavedDataType<WorldBorder>("world_border", WorldBorder::new, CODEC, DataFixTypes.SAVED_DATA_WORLD_BORDER);
   }

   class MovingBorderExtent implements BorderExtent {
      private final double from;
      private final double to;
      private final long lerpEnd;
      private final long lerpBegin;
      private final double lerpDuration;
      private long lerpProgress;
      private double size;
      private double previousSize;

      MovingBorderExtent(final double var2, final double var4, final long var6, final long var8) {
         super();
         this.from = var2;
         this.to = var4;
         this.lerpDuration = (double)var6;
         this.lerpProgress = var6;
         this.lerpBegin = var8;
         this.lerpEnd = this.lerpBegin + var6;
         double var10 = this.calculateSize();
         this.size = var10;
         this.previousSize = var10;
      }

      public double getMinX(float var1) {
         return Mth.clamp(WorldBorder.this.getCenterX() - Mth.lerp((double)var1, this.getPreviousSize(), this.getSize()) / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMinZ(float var1) {
         return Mth.clamp(WorldBorder.this.getCenterZ() - Mth.lerp((double)var1, this.getPreviousSize(), this.getSize()) / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMaxX(float var1) {
         return Mth.clamp(WorldBorder.this.getCenterX() + Mth.lerp((double)var1, this.getPreviousSize(), this.getSize()) / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMaxZ(float var1) {
         return Mth.clamp(WorldBorder.this.getCenterZ() + Mth.lerp((double)var1, this.getPreviousSize(), this.getSize()) / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getSize() {
         return this.size;
      }

      public double getPreviousSize() {
         return this.previousSize;
      }

      private double calculateSize() {
         double var1 = (this.lerpDuration - (double)this.lerpProgress) / this.lerpDuration;
         return var1 < 1.0 ? Mth.lerp(var1, this.from, this.to) : this.to;
      }

      public double getLerpSpeed() {
         return Math.abs(this.from - this.to) / (double)(this.lerpEnd - this.lerpBegin);
      }

      public long getLerpTime() {
         return this.lerpProgress;
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

      public BorderExtent update() {
         --this.lerpProgress;
         this.previousSize = this.size;
         this.size = this.calculateSize();
         if (this.lerpProgress <= 0L) {
            WorldBorder.this.setDirty();
            return WorldBorder.this.new StaticBorderExtent(this.to);
         } else {
            return this;
         }
      }

      public VoxelShape getCollisionShape() {
         return Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX(0.0F)), -1.0 / 0.0, Math.floor(this.getMinZ(0.0F)), Math.ceil(this.getMaxX(0.0F)), 1.0 / 0.0, Math.ceil(this.getMaxZ(0.0F))), BooleanOp.ONLY_FIRST);
      }
   }

   class StaticBorderExtent implements BorderExtent {
      private final double size;
      private double minX;
      private double minZ;
      private double maxX;
      private double maxZ;
      private VoxelShape shape;

      public StaticBorderExtent(final double var2) {
         super();
         this.size = var2;
         this.updateBox();
      }

      public double getMinX(float var1) {
         return this.minX;
      }

      public double getMaxX(float var1) {
         return this.maxX;
      }

      public double getMinZ(float var1) {
         return this.minZ;
      }

      public double getMaxZ(float var1) {
         return this.maxZ;
      }

      public double getSize() {
         return this.size;
      }

      public BorderStatus getStatus() {
         return BorderStatus.STATIONARY;
      }

      public double getLerpSpeed() {
         return 0.0;
      }

      public long getLerpTime() {
         return 0L;
      }

      public double getLerpTarget() {
         return this.size;
      }

      private void updateBox() {
         this.minX = Mth.clamp(WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
         this.minZ = Mth.clamp(WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
         this.maxX = Mth.clamp(WorldBorder.this.getCenterX() + this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
         this.maxZ = Mth.clamp(WorldBorder.this.getCenterZ() + this.size / 2.0, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
         this.shape = Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX(0.0F)), -1.0 / 0.0, Math.floor(this.getMinZ(0.0F)), Math.ceil(this.getMaxX(0.0F)), 1.0 / 0.0, Math.ceil(this.getMaxZ(0.0F))), BooleanOp.ONLY_FIRST);
      }

      public void onAbsoluteMaxSizeChange() {
         this.updateBox();
      }

      public void onCenterChange() {
         this.updateBox();
      }

      public BorderExtent update() {
         return this;
      }

      public VoxelShape getCollisionShape() {
         return this.shape;
      }
   }

   public static record Settings(double centerX, double centerZ, double damagePerBlock, double safeZone, int warningBlocks, int warningTime, double size, long lerpTime, double lerpTarget) {
      public static final Settings DEFAULT = new Settings(0.0, 0.0, 0.2, 5.0, 5, 300, 5.9999968E7, 0L, 0.0);
      public static final Codec<Settings> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.doubleRange(-2.9999984E7, 2.9999984E7).fieldOf("center_x").forGetter(Settings::centerX), Codec.doubleRange(-2.9999984E7, 2.9999984E7).fieldOf("center_z").forGetter(Settings::centerZ), Codec.DOUBLE.fieldOf("damage_per_block").forGetter(Settings::damagePerBlock), Codec.DOUBLE.fieldOf("safe_zone").forGetter(Settings::safeZone), Codec.INT.fieldOf("warning_blocks").forGetter(Settings::warningBlocks), Codec.INT.fieldOf("warning_time").forGetter(Settings::warningTime), Codec.DOUBLE.fieldOf("size").forGetter(Settings::size), Codec.LONG.fieldOf("lerp_time").forGetter(Settings::lerpTime), Codec.DOUBLE.fieldOf("lerp_target").forGetter(Settings::lerpTarget)).apply(var0, Settings::new));

      public Settings(WorldBorder var1) {
         this(var1.centerX, var1.centerZ, var1.damagePerBlock, var1.safeZone, var1.warningBlocks, var1.warningTime, var1.extent.getSize(), var1.extent.getLerpTime(), var1.extent.getLerpTarget());
      }

      public Settings(double var1, double var3, double var5, double var7, int var9, int var10, double var11, long var13, double var15) {
         super();
         this.centerX = var1;
         this.centerZ = var3;
         this.damagePerBlock = var5;
         this.safeZone = var7;
         this.warningBlocks = var9;
         this.warningTime = var10;
         this.size = var11;
         this.lerpTime = var13;
         this.lerpTarget = var15;
      }
   }

   interface BorderExtent {
      double getMinX(float var1);

      double getMaxX(float var1);

      double getMinZ(float var1);

      double getMaxZ(float var1);

      double getSize();

      double getLerpSpeed();

      long getLerpTime();

      double getLerpTarget();

      BorderStatus getStatus();

      void onAbsoluteMaxSizeChange();

      void onCenterChange();

      BorderExtent update();

      VoxelShape getCollisionShape();
   }
}
