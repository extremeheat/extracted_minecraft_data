package net.minecraft.world.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Brightness;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public abstract class Display extends Entity {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final float INITIAL_UPDATE_PROGRESS = 1.0F / 0.0F;
   public static final int NO_BRIGHTNESS_OVERRIDE = -1;
   private static final EntityDataAccessor<Integer> DATA_INTERPOLATION_START_DELTA_TICKS_ID = SynchedEntityData.defineId(
      Display.class, EntityDataSerializers.INT
   );
   private static final EntityDataAccessor<Integer> DATA_INTERPOLATION_DURATION_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Vector3f> DATA_TRANSLATION_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Vector3f> DATA_SCALE_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Quaternionf> DATA_LEFT_ROTATION_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.QUATERNION);
   private static final EntityDataAccessor<Quaternionf> DATA_RIGHT_ROTATION_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.QUATERNION);
   private static final EntityDataAccessor<Byte> DATA_BILLBOARD_RENDER_CONSTRAINTS_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Integer> DATA_BRIGHTNESS_OVERRIDE_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_VIEW_RANGE_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_SHADOW_RADIUS_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_SHADOW_STRENGTH_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_WIDTH_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_HEIGHT_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_GLOW_COLOR_OVERRIDE_ID = SynchedEntityData.defineId(Display.class, EntityDataSerializers.INT);
   private static final float INITIAL_SHADOW_RADIUS = 0.0F;
   private static final float INITIAL_SHADOW_STRENGTH = 1.0F;
   private static final int NO_GLOW_COLOR_OVERRIDE = -1;
   public static final String TAG_INTERPOLATION_DURATION = "interpolation_duration";
   public static final String TAG_START_INTERPOLATION = "start_interpolation";
   public static final String TAG_TRANSFORMATION = "transformation";
   public static final String TAG_BILLBOARD = "billboard";
   public static final String TAG_BRIGHTNESS = "brightness";
   public static final String TAG_VIEW_RANGE = "view_range";
   public static final String TAG_SHADOW_RADIUS = "shadow_radius";
   public static final String TAG_SHADOW_STRENGTH = "shadow_strength";
   public static final String TAG_WIDTH = "width";
   public static final String TAG_HEIGHT = "height";
   public static final String TAG_GLOW_COLOR_OVERRIDE = "glow_color_override";
   private final Display.GenericInterpolator<Transformation> transformation = new Display.GenericInterpolator<Transformation>(Transformation.identity()) {
      protected Transformation interpolate(float var1, Transformation var2, Transformation var3) {
         return var2.slerp(var3, var1);
      }
   };
   private final Display.FloatInterpolator shadowRadius = new Display.FloatInterpolator(0.0F);
   private final Display.FloatInterpolator shadowStrength = new Display.FloatInterpolator(1.0F);
   private final Quaternionf orientation = new Quaternionf();
   protected final Display.InterpolatorSet interpolators = new Display.InterpolatorSet();
   private long interpolationStartClientTick;
   private float lastProgress;
   private AABB cullingBoundingBox;
   private boolean updateInterpolators;
   private boolean updateTime;

   public Display(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
      this.noCulling = true;
      this.cullingBoundingBox = this.getBoundingBox();
      this.interpolators
         .addEntry(
            Set.of(DATA_TRANSLATION_ID, DATA_LEFT_ROTATION_ID, DATA_SCALE_ID, DATA_RIGHT_ROTATION_ID),
            (var1x, var2x) -> this.transformation.updateValue(var1x, createTransformation(var2x))
         );
      this.interpolators.addEntry(DATA_SHADOW_STRENGTH_ID, this.shadowStrength);
      this.interpolators.addEntry(DATA_SHADOW_RADIUS_ID, this.shadowRadius);
   }

   @Override
   public void onSyncedDataUpdated(List<SynchedEntityData.DataValue<?>> var1) {
      super.onSyncedDataUpdated(var1);
      boolean var2 = false;

      for(SynchedEntityData.DataValue var4 : var1) {
         var2 |= this.interpolators.shouldTriggerUpdate(var4.id());
      }

      if (var2) {
         boolean var5 = this.tickCount <= 0;
         if (var5) {
            this.interpolators.updateValues(1.0F / 0.0F, this.entityData);
         } else {
            this.updateInterpolators = true;
         }
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_HEIGHT_ID.equals(var1) || DATA_WIDTH_ID.equals(var1)) {
         this.updateCulling();
      }

      if (DATA_INTERPOLATION_START_DELTA_TICKS_ID.equals(var1)) {
         this.updateTime = true;
      }
   }

   private static Transformation createTransformation(SynchedEntityData var0) {
      Vector3f var1 = var0.get(DATA_TRANSLATION_ID);
      Quaternionf var2 = var0.get(DATA_LEFT_ROTATION_ID);
      Vector3f var3 = var0.get(DATA_SCALE_ID);
      Quaternionf var4 = var0.get(DATA_RIGHT_ROTATION_ID);
      return new Transformation(var1, var2, var3, var4);
   }

   @Override
   public void tick() {
      Entity var1 = this.getVehicle();
      if (var1 != null && var1.isRemoved()) {
         this.stopRiding();
      }

      if (this.level.isClientSide) {
         if (this.updateTime) {
            this.updateTime = false;
            int var2 = this.getInterpolationDelay();
            this.interpolationStartClientTick = (long)(this.tickCount + var2);
         }

         if (this.updateInterpolators) {
            this.updateInterpolators = false;
            this.interpolators.updateValues(this.lastProgress, this.entityData);
         }
      }
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(DATA_INTERPOLATION_START_DELTA_TICKS_ID, 0);
      this.entityData.define(DATA_INTERPOLATION_DURATION_ID, 0);
      this.entityData.define(DATA_TRANSLATION_ID, new Vector3f());
      this.entityData.define(DATA_SCALE_ID, new Vector3f(1.0F, 1.0F, 1.0F));
      this.entityData.define(DATA_RIGHT_ROTATION_ID, new Quaternionf());
      this.entityData.define(DATA_LEFT_ROTATION_ID, new Quaternionf());
      this.entityData.define(DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, Display.BillboardConstraints.FIXED.getId());
      this.entityData.define(DATA_BRIGHTNESS_OVERRIDE_ID, -1);
      this.entityData.define(DATA_VIEW_RANGE_ID, 1.0F);
      this.entityData.define(DATA_SHADOW_RADIUS_ID, 0.0F);
      this.entityData.define(DATA_SHADOW_STRENGTH_ID, 1.0F);
      this.entityData.define(DATA_WIDTH_ID, 0.0F);
      this.entityData.define(DATA_HEIGHT_ID, 0.0F);
      this.entityData.define(DATA_GLOW_COLOR_OVERRIDE_ID, -1);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.contains("transformation")) {
         Transformation.EXTENDED_CODEC
            .decode(NbtOps.INSTANCE, var1.get("transformation"))
            .resultOrPartial(Util.prefix("Display entity", LOGGER::error))
            .ifPresent(var1x -> this.setTransformation((Transformation)var1x.getFirst()));
      }

      if (var1.contains("interpolation_duration", 99)) {
         int var2 = var1.getInt("interpolation_duration");
         this.setInterpolationDuration(var2);
      }

      if (var1.contains("start_interpolation", 99)) {
         int var3 = var1.getInt("start_interpolation");
         this.setInterpolationDelay(var3);
      }

      if (var1.contains("billboard", 8)) {
         Display.BillboardConstraints.CODEC
            .decode(NbtOps.INSTANCE, var1.get("billboard"))
            .resultOrPartial(Util.prefix("Display entity", LOGGER::error))
            .ifPresent(var1x -> this.setBillboardConstraints((Display.BillboardConstraints)var1x.getFirst()));
      }

      if (var1.contains("view_range", 99)) {
         this.setViewRange(var1.getFloat("view_range"));
      }

      if (var1.contains("shadow_radius", 99)) {
         this.setShadowRadius(var1.getFloat("shadow_radius"));
      }

      if (var1.contains("shadow_strength", 99)) {
         this.setShadowStrength(var1.getFloat("shadow_strength"));
      }

      if (var1.contains("width", 99)) {
         this.setWidth(var1.getFloat("width"));
      }

      if (var1.contains("height", 99)) {
         this.setHeight(var1.getFloat("height"));
      }

      if (var1.contains("glow_color_override", 99)) {
         this.setGlowColorOverride(var1.getInt("glow_color_override"));
      }

      if (var1.contains("brightness", 10)) {
         Brightness.CODEC
            .decode(NbtOps.INSTANCE, var1.get("brightness"))
            .resultOrPartial(Util.prefix("Display entity", LOGGER::error))
            .ifPresent(var1x -> this.setBrightnessOverride((Brightness)var1x.getFirst()));
      } else {
         this.setBrightnessOverride(null);
      }
   }

   private void setTransformation(Transformation var1) {
      this.entityData.set(DATA_TRANSLATION_ID, var1.getTranslation());
      this.entityData.set(DATA_LEFT_ROTATION_ID, var1.getLeftRotation());
      this.entityData.set(DATA_SCALE_ID, var1.getScale());
      this.entityData.set(DATA_RIGHT_ROTATION_ID, var1.getRightRotation());
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      Transformation.EXTENDED_CODEC
         .encodeStart(NbtOps.INSTANCE, createTransformation(this.entityData))
         .result()
         .ifPresent(var1x -> var1.put("transformation", var1x));
      Display.BillboardConstraints.CODEC
         .encodeStart(NbtOps.INSTANCE, this.getBillboardConstraints())
         .result()
         .ifPresent(var1x -> var1.put("billboard", var1x));
      var1.putInt("interpolation_duration", this.getInterpolationDuration());
      var1.putFloat("view_range", this.getViewRange());
      var1.putFloat("shadow_radius", this.getShadowRadius());
      var1.putFloat("shadow_strength", this.getShadowStrength());
      var1.putFloat("width", this.getWidth());
      var1.putFloat("height", this.getHeight());
      var1.putInt("glow_color_override", this.getGlowColorOverride());
      Brightness var2 = this.getBrightnessOverride();
      if (var2 != null) {
         Brightness.CODEC.encodeStart(NbtOps.INSTANCE, var2).result().ifPresent(var1x -> var1.put("brightness", var1x));
      }
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   @Override
   public AABB getBoundingBoxForCulling() {
      return this.cullingBoundingBox;
   }

   @Override
   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public Quaternionf orientation() {
      return this.orientation;
   }

   public Transformation transformation(float var1) {
      return this.transformation.get(var1);
   }

   private void setInterpolationDuration(int var1) {
      this.entityData.set(DATA_INTERPOLATION_DURATION_ID, var1);
   }

   private int getInterpolationDuration() {
      return this.entityData.get(DATA_INTERPOLATION_DURATION_ID);
   }

   private void setInterpolationDelay(int var1) {
      this.entityData.set(DATA_INTERPOLATION_START_DELTA_TICKS_ID, var1, true);
   }

   private int getInterpolationDelay() {
      return this.entityData.get(DATA_INTERPOLATION_START_DELTA_TICKS_ID);
   }

   private void setBillboardConstraints(Display.BillboardConstraints var1) {
      this.entityData.set(DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, var1.getId());
   }

   public Display.BillboardConstraints getBillboardConstraints() {
      return Display.BillboardConstraints.BY_ID.apply(this.entityData.get(DATA_BILLBOARD_RENDER_CONSTRAINTS_ID));
   }

   private void setBrightnessOverride(@Nullable Brightness var1) {
      this.entityData.set(DATA_BRIGHTNESS_OVERRIDE_ID, var1 != null ? var1.pack() : -1);
   }

   @Nullable
   private Brightness getBrightnessOverride() {
      int var1 = this.entityData.get(DATA_BRIGHTNESS_OVERRIDE_ID);
      return var1 != -1 ? Brightness.unpack(var1) : null;
   }

   public int getPackedBrightnessOverride() {
      return this.entityData.get(DATA_BRIGHTNESS_OVERRIDE_ID);
   }

   private void setViewRange(float var1) {
      this.entityData.set(DATA_VIEW_RANGE_ID, var1);
   }

   private float getViewRange() {
      return this.entityData.get(DATA_VIEW_RANGE_ID);
   }

   private void setShadowRadius(float var1) {
      this.entityData.set(DATA_SHADOW_RADIUS_ID, var1);
   }

   private float getShadowRadius() {
      return this.entityData.get(DATA_SHADOW_RADIUS_ID);
   }

   public float getShadowRadius(float var1) {
      return this.shadowRadius.get(var1);
   }

   private void setShadowStrength(float var1) {
      this.entityData.set(DATA_SHADOW_STRENGTH_ID, var1);
   }

   private float getShadowStrength() {
      return this.entityData.get(DATA_SHADOW_STRENGTH_ID);
   }

   public float getShadowStrength(float var1) {
      return this.shadowStrength.get(var1);
   }

   private void setWidth(float var1) {
      this.entityData.set(DATA_WIDTH_ID, var1);
   }

   private float getWidth() {
      return this.entityData.get(DATA_WIDTH_ID);
   }

   private void setHeight(float var1) {
      this.entityData.set(DATA_HEIGHT_ID, var1);
   }

   private int getGlowColorOverride() {
      return this.entityData.get(DATA_GLOW_COLOR_OVERRIDE_ID);
   }

   private void setGlowColorOverride(int var1) {
      this.entityData.set(DATA_GLOW_COLOR_OVERRIDE_ID, var1);
   }

   public float calculateInterpolationProgress(float var1) {
      int var2 = this.getInterpolationDuration();
      if (var2 <= 0) {
         return 1.0F;
      } else {
         float var3 = (float)((long)this.tickCount - this.interpolationStartClientTick);
         float var4 = var3 + var1;
         float var5 = Mth.clamp(Mth.inverseLerp(var4, 0.0F, (float)var2), 0.0F, 1.0F);
         this.lastProgress = var5;
         return var5;
      }
   }

   private float getHeight() {
      return this.entityData.get(DATA_HEIGHT_ID);
   }

   @Override
   public void setPos(double var1, double var3, double var5) {
      super.setPos(var1, var3, var5);
      this.updateCulling();
   }

   private void updateCulling() {
      float var1 = this.getWidth();
      float var2 = this.getHeight();
      if (var1 != 0.0F && var2 != 0.0F) {
         this.noCulling = false;
         float var3 = var1 / 2.0F;
         double var4 = this.getX();
         double var6 = this.getY();
         double var8 = this.getZ();
         this.cullingBoundingBox = new AABB(var4 - (double)var3, var6, var8 - (double)var3, var4 + (double)var3, var6 + (double)var2, var8 + (double)var3);
      } else {
         this.noCulling = true;
      }
   }

   @Override
   public void setXRot(float var1) {
      super.setXRot(var1);
      this.updateOrientation();
   }

   @Override
   public void setYRot(float var1) {
      super.setYRot(var1);
      this.updateOrientation();
   }

   private void updateOrientation() {
      this.orientation.rotationYXZ(-0.017453292F * this.getYRot(), 0.017453292F * this.getXRot(), 0.0F);
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      return var1 < Mth.square((double)this.getViewRange() * 64.0 * getViewScale());
   }

   @Override
   public int getTeamColor() {
      int var1 = this.getGlowColorOverride();
      return var1 != -1 ? var1 : super.getTeamColor();
   }

   public static enum BillboardConstraints implements StringRepresentable {
      FIXED((byte)0, "fixed"),
      VERTICAL((byte)1, "vertical"),
      HORIZONTAL((byte)2, "horizontal"),
      CENTER((byte)3, "center");

      public static final Codec<Display.BillboardConstraints> CODEC = StringRepresentable.fromEnum(Display.BillboardConstraints::values);
      public static final IntFunction<Display.BillboardConstraints> BY_ID = ByIdMap.continuous(
         Display.BillboardConstraints::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO
      );
      private final byte id;
      private final String name;

      private BillboardConstraints(byte var3, String var4) {
         this.name = var4;
         this.id = var3;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      byte getId() {
         return this.id;
      }
   }

   public static class BlockDisplay extends Display {
      public static final String TAG_BLOCK_STATE = "block_state";
      private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(
         Display.BlockDisplay.class, EntityDataSerializers.BLOCK_STATE
      );

      public BlockDisplay(EntityType<?> var1, Level var2) {
         super(var1, var2);
      }

      @Override
      protected void defineSynchedData() {
         super.defineSynchedData();
         this.entityData.define(DATA_BLOCK_STATE_ID, Blocks.AIR.defaultBlockState());
      }

      public BlockState getBlockState() {
         return this.entityData.get(DATA_BLOCK_STATE_ID);
      }

      public void setBlockState(BlockState var1) {
         this.entityData.set(DATA_BLOCK_STATE_ID, var1);
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag var1) {
         super.readAdditionalSaveData(var1);
         this.setBlockState(NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), var1.getCompound("block_state")));
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
      }
   }

   static class ColorInterpolator extends Display.IntInterpolator {
      protected ColorInterpolator(int var1) {
         super(var1);
      }

      @Override
      protected int interpolate(float var1, int var2, int var3) {
         return FastColor.ARGB32.lerp(var1, var2, var3);
      }
   }

   static class FloatInterpolator extends Display.Interpolator<Float> {
      protected FloatInterpolator(float var1) {
         super(var1);
      }

      protected float interpolate(float var1, float var2, float var3) {
         return Mth.lerp(var1, var2, var3);
      }

      public float get(float var1) {
         return !((double)var1 >= 1.0) && this.lastValue != null ? this.interpolate(var1, this.lastValue, this.currentValue) : this.currentValue;
      }

      protected Float getGeneric(float var1) {
         return this.get(var1);
      }
   }

   abstract static class GenericInterpolator<T> extends Display.Interpolator<T> {
      protected GenericInterpolator(T var1) {
         super((T)var1);
      }

      protected abstract T interpolate(float var1, T var2, T var3);

      public T get(float var1) {
         return (T)(!((double)var1 >= 1.0) && this.lastValue != null ? this.interpolate(var1, this.lastValue, this.currentValue) : this.currentValue);
      }

      @Override
      protected T getGeneric(float var1) {
         return this.get(var1);
      }
   }

   static class IntInterpolator extends Display.Interpolator<Integer> {
      protected IntInterpolator(int var1) {
         super(var1);
      }

      protected int interpolate(float var1, int var2, int var3) {
         return Mth.lerpInt(var1, var2, var3);
      }

      public int get(float var1) {
         return !((double)var1 >= 1.0) && this.lastValue != null ? this.interpolate(var1, this.lastValue, this.currentValue) : this.currentValue;
      }

      protected Integer getGeneric(float var1) {
         return this.get(var1);
      }
   }

   @FunctionalInterface
   interface IntepolatorUpdater {
      void update(float var1, SynchedEntityData var2);
   }

   abstract static class Interpolator<T> {
      @Nullable
      protected T lastValue;
      protected T currentValue;

      protected Interpolator(T var1) {
         super();
         this.currentValue = (T)var1;
      }

      protected abstract T getGeneric(float var1);

      public void updateValue(float var1, T var2) {
         if (var1 != 1.0F / 0.0F) {
            this.lastValue = this.getGeneric(var1);
         }

         this.currentValue = (T)var2;
      }
   }

   static class InterpolatorSet {
      private final IntSet interpolatedData = new IntOpenHashSet();
      private final List<Display.IntepolatorUpdater> updaters = new ArrayList<>();

      InterpolatorSet() {
         super();
      }

      protected <T> void addEntry(EntityDataAccessor<T> var1, Display.Interpolator<T> var2) {
         this.interpolatedData.add(var1.getId());
         this.updaters.add((var2x, var3) -> var2.updateValue(var2x, var3.get(var1)));
      }

      protected void addEntry(Set<EntityDataAccessor<?>> var1, Display.IntepolatorUpdater var2) {
         for(EntityDataAccessor var4 : var1) {
            this.interpolatedData.add(var4.getId());
         }

         this.updaters.add(var2);
      }

      public boolean shouldTriggerUpdate(int var1) {
         return this.interpolatedData.contains(var1);
      }

      public void updateValues(float var1, SynchedEntityData var2) {
         for(Display.IntepolatorUpdater var4 : this.updaters) {
            var4.update(var1, var2);
         }
      }
   }

   public static class ItemDisplay extends Display {
      private static final String TAG_ITEM = "item";
      private static final String TAG_ITEM_DISPLAY = "item_display";
      private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK_ID = SynchedEntityData.defineId(
         Display.ItemDisplay.class, EntityDataSerializers.ITEM_STACK
      );
      private static final EntityDataAccessor<Byte> DATA_ITEM_DISPLAY_ID = SynchedEntityData.defineId(Display.ItemDisplay.class, EntityDataSerializers.BYTE);
      private final SlotAccess slot = new SlotAccess() {
         @Override
         public ItemStack get() {
            return ItemDisplay.this.getItemStack();
         }

         @Override
         public boolean set(ItemStack var1) {
            ItemDisplay.this.setItemStack(var1);
            return true;
         }
      };

      public ItemDisplay(EntityType<?> var1, Level var2) {
         super(var1, var2);
      }

      @Override
      protected void defineSynchedData() {
         super.defineSynchedData();
         this.entityData.define(DATA_ITEM_STACK_ID, ItemStack.EMPTY);
         this.entityData.define(DATA_ITEM_DISPLAY_ID, ItemDisplayContext.NONE.getId());
      }

      public ItemStack getItemStack() {
         return this.entityData.get(DATA_ITEM_STACK_ID);
      }

      void setItemStack(ItemStack var1) {
         this.entityData.set(DATA_ITEM_STACK_ID, var1);
      }

      private void setItemTransform(ItemDisplayContext var1) {
         this.entityData.set(DATA_ITEM_DISPLAY_ID, var1.getId());
      }

      public ItemDisplayContext getItemTransform() {
         return ItemDisplayContext.BY_ID.apply(this.entityData.get(DATA_ITEM_DISPLAY_ID));
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag var1) {
         super.readAdditionalSaveData(var1);
         this.setItemStack(ItemStack.of(var1.getCompound("item")));
         if (var1.contains("item_display", 8)) {
            ItemDisplayContext.CODEC
               .decode(NbtOps.INSTANCE, var1.get("item_display"))
               .resultOrPartial(Util.prefix("Display entity", Display.LOGGER::error))
               .ifPresent(var1x -> this.setItemTransform((ItemDisplayContext)var1x.getFirst()));
         }
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.put("item", this.getItemStack().save(new CompoundTag()));
         ItemDisplayContext.CODEC.encodeStart(NbtOps.INSTANCE, this.getItemTransform()).result().ifPresent(var1x -> var1.put("item_display", var1x));
      }

      @Override
      public SlotAccess getSlot(int var1) {
         return var1 == 0 ? this.slot : SlotAccess.NULL;
      }
   }

   public static class TextDisplay extends Display {
      public static final String TAG_TEXT = "text";
      private static final String TAG_LINE_WIDTH = "line_width";
      private static final String TAG_TEXT_OPACITY = "text_opacity";
      private static final String TAG_BACKGROUND_COLOR = "background";
      private static final String TAG_SHADOW = "shadow";
      private static final String TAG_SEE_THROUGH = "see_through";
      private static final String TAG_USE_DEFAULT_BACKGROUND = "default_background";
      private static final String TAG_ALIGNMENT = "alignment";
      public static final byte FLAG_SHADOW = 1;
      public static final byte FLAG_SEE_THROUGH = 2;
      public static final byte FLAG_USE_DEFAULT_BACKGROUND = 4;
      public static final byte FLAG_ALIGN_LEFT = 8;
      public static final byte FLAG_ALIGN_RIGHT = 16;
      private static final byte INITIAL_TEXT_OPACITY = -1;
      public static final int INITIAL_BACKGROUND = 1073741824;
      private static final EntityDataAccessor<Component> DATA_TEXT_ID = SynchedEntityData.defineId(Display.TextDisplay.class, EntityDataSerializers.COMPONENT);
      private static final EntityDataAccessor<Integer> DATA_LINE_WIDTH_ID = SynchedEntityData.defineId(Display.TextDisplay.class, EntityDataSerializers.INT);
      private static final EntityDataAccessor<Integer> DATA_BACKGROUND_COLOR_ID = SynchedEntityData.defineId(
         Display.TextDisplay.class, EntityDataSerializers.INT
      );
      private static final EntityDataAccessor<Byte> DATA_TEXT_OPACITY_ID = SynchedEntityData.defineId(Display.TextDisplay.class, EntityDataSerializers.BYTE);
      private static final EntityDataAccessor<Byte> DATA_STYLE_FLAGS_ID = SynchedEntityData.defineId(Display.TextDisplay.class, EntityDataSerializers.BYTE);
      private final Display.IntInterpolator textOpacity = new Display.IntInterpolator(-1);
      private final Display.IntInterpolator backgroundColor = new Display.ColorInterpolator(1073741824);
      @Nullable
      private Display.TextDisplay.CachedInfo clientDisplayCache;

      public TextDisplay(EntityType<?> var1, Level var2) {
         super(var1, var2);
         this.interpolators.addEntry(DATA_BACKGROUND_COLOR_ID, this.backgroundColor);
         this.interpolators
            .addEntry(
               Set.of(DATA_TEXT_OPACITY_ID), (var1x, var2x) -> this.textOpacity.updateValue(var1x, Integer.valueOf(var2x.get(DATA_TEXT_OPACITY_ID) & 255))
            );
      }

      @Override
      protected void defineSynchedData() {
         super.defineSynchedData();
         this.entityData.define(DATA_TEXT_ID, Component.empty());
         this.entityData.define(DATA_LINE_WIDTH_ID, 200);
         this.entityData.define(DATA_BACKGROUND_COLOR_ID, 1073741824);
         this.entityData.define(DATA_TEXT_OPACITY_ID, (byte)-1);
         this.entityData.define(DATA_STYLE_FLAGS_ID, (byte)0);
      }

      @Override
      public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
         super.onSyncedDataUpdated(var1);
         this.clientDisplayCache = null;
      }

      public Component getText() {
         return this.entityData.get(DATA_TEXT_ID);
      }

      private void setText(Component var1) {
         this.entityData.set(DATA_TEXT_ID, var1);
      }

      public int getLineWidth() {
         return this.entityData.get(DATA_LINE_WIDTH_ID);
      }

      private void setLineWidth(int var1) {
         this.entityData.set(DATA_LINE_WIDTH_ID, var1);
      }

      public byte getTextOpacity(float var1) {
         return (byte)this.textOpacity.get(var1);
      }

      private byte getTextOpacity() {
         return this.entityData.get(DATA_TEXT_OPACITY_ID);
      }

      private void setTextOpacity(byte var1) {
         this.entityData.set(DATA_TEXT_OPACITY_ID, var1);
      }

      public int getBackgroundColor(float var1) {
         return this.backgroundColor.get(var1);
      }

      private int getBackgroundColor() {
         return this.entityData.get(DATA_BACKGROUND_COLOR_ID);
      }

      private void setBackgroundColor(int var1) {
         this.entityData.set(DATA_BACKGROUND_COLOR_ID, var1);
      }

      public byte getFlags() {
         return this.entityData.get(DATA_STYLE_FLAGS_ID);
      }

      private void setFlags(byte var1) {
         this.entityData.set(DATA_STYLE_FLAGS_ID, var1);
      }

      private static byte loadFlag(byte var0, CompoundTag var1, String var2, byte var3) {
         return var1.getBoolean(var2) ? (byte)(var0 | var3) : var0;
      }

      @Override
      protected void readAdditionalSaveData(CompoundTag var1) {
         super.readAdditionalSaveData(var1);
         if (var1.contains("line_width", 99)) {
            this.setLineWidth(var1.getInt("line_width"));
         }

         if (var1.contains("text_opacity", 99)) {
            this.setTextOpacity(var1.getByte("text_opacity"));
         }

         if (var1.contains("background", 99)) {
            this.setBackgroundColor(var1.getInt("background"));
         }

         byte var2 = loadFlag((byte)0, var1, "shadow", (byte)1);
         var2 = loadFlag(var2, var1, "see_through", (byte)2);
         var2 = loadFlag(var2, var1, "default_background", (byte)4);
         Optional var3 = Display.TextDisplay.Align.CODEC
            .decode(NbtOps.INSTANCE, var1.get("alignment"))
            .resultOrPartial(Util.prefix("Display entity", Display.LOGGER::error))
            .map(Pair::getFirst);
         if (var3.isPresent()) {
            var2 = switch((Display.TextDisplay.Align)var3.get()) {
               case CENTER -> var2;
               case LEFT -> (byte)(var2 | 8);
               case RIGHT -> (byte)(var2 | 16);
            };
         }

         this.setFlags(var2);
         if (var1.contains("text", 8)) {
            String var4 = var1.getString("text");

            try {
               MutableComponent var5 = Component.Serializer.fromJson(var4);
               if (var5 != null) {
                  CommandSourceStack var6 = this.createCommandSourceStack().withPermission(2);
                  MutableComponent var7 = ComponentUtils.updateForEntity(var6, var5, this, 0);
                  this.setText(var7);
               } else {
                  this.setText(Component.empty());
               }
            } catch (Exception var8) {
               Display.LOGGER.warn("Failed to parse display entity text {}", var4, var8);
            }
         }
      }

      private static void storeFlag(byte var0, CompoundTag var1, String var2, byte var3) {
         var1.putBoolean(var2, (var0 & var3) != 0);
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putString("text", Component.Serializer.toJson(this.getText()));
         var1.putInt("line_width", this.getLineWidth());
         var1.putInt("background", this.getBackgroundColor());
         var1.putByte("text_opacity", this.getTextOpacity());
         byte var2 = this.getFlags();
         storeFlag(var2, var1, "shadow", (byte)1);
         storeFlag(var2, var1, "see_through", (byte)2);
         storeFlag(var2, var1, "default_background", (byte)4);
         Display.TextDisplay.Align.CODEC.encodeStart(NbtOps.INSTANCE, getAlign(var2)).result().ifPresent(var1x -> var1.put("alignment", var1x));
      }

      public Display.TextDisplay.CachedInfo cacheDisplay(Display.TextDisplay.LineSplitter var1) {
         if (this.clientDisplayCache == null) {
            int var2 = this.getLineWidth();
            this.clientDisplayCache = var1.split(this.getText(), var2);
         }

         return this.clientDisplayCache;
      }

      public static Display.TextDisplay.Align getAlign(byte var0) {
         if ((var0 & 8) != 0) {
            return Display.TextDisplay.Align.LEFT;
         } else {
            return (var0 & 16) != 0 ? Display.TextDisplay.Align.RIGHT : Display.TextDisplay.Align.CENTER;
         }
      }

      public static enum Align implements StringRepresentable {
         CENTER("center"),
         LEFT("left"),
         RIGHT("right");

         public static final Codec<Display.TextDisplay.Align> CODEC = StringRepresentable.fromEnum(Display.TextDisplay.Align::values);
         private final String name;

         private Align(String var3) {
            this.name = var3;
         }

         @Override
         public String getSerializedName() {
            return this.name;
         }
      }

      public static record CachedInfo(List<Display.TextDisplay.CachedLine> a, int b) {
         private final List<Display.TextDisplay.CachedLine> lines;
         private final int width;

         public CachedInfo(List<Display.TextDisplay.CachedLine> var1, int var2) {
            super();
            this.lines = var1;
            this.width = var2;
         }
      }

      public static record CachedLine(FormattedCharSequence a, int b) {
         private final FormattedCharSequence contents;
         private final int width;

         public CachedLine(FormattedCharSequence var1, int var2) {
            super();
            this.contents = var1;
            this.width = var2;
         }
      }

      @FunctionalInterface
      public interface LineSplitter {
         Display.TextDisplay.CachedInfo split(Component var1, int var2);
      }
   }
}
