package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttribute<Value> {
   private final AttributeType<Value> type;
   private final Value defaultValue;
   private final AttributeRange<Value> valueRange;
   private final boolean isSyncable;
   private final boolean isPositional;
   private final boolean isSpatiallyInterpolated;
   private final boolean fullResolutionBiomes;

   private EnvironmentAttribute(final AttributeType<Value> type, final Value defaultValue, final AttributeRange<Value> valueRange, final boolean isSyncable, final boolean isPositional, final boolean isSpatiallyInterpolated, final boolean fullResolutionBiomes) {
      super();
      this.type = type;
      this.defaultValue = defaultValue;
      this.valueRange = valueRange;
      this.isSyncable = isSyncable;
      this.isPositional = isPositional;
      this.isSpatiallyInterpolated = isSpatiallyInterpolated;
      this.fullResolutionBiomes = fullResolutionBiomes;
   }

   public static <Value> Builder<Value> builder(final AttributeType<Value> type) {
      return new Builder<Value>(type);
   }

   public AttributeType<Value> type() {
      return this.type;
   }

   public Value defaultValue() {
      return this.defaultValue;
   }

   public Codec<Value> valueCodec() {
      Codec var10000 = this.type.valueCodec();
      AttributeRange var10001 = this.valueRange;
      Objects.requireNonNull(var10001);
      return var10000.validate(var10001::validate);
   }

   public Value sanitizeValue(final Value value) {
      return this.valueRange.sanitize(value);
   }

   public boolean isSyncable() {
      return this.isSyncable;
   }

   public boolean isPositional() {
      return this.isPositional;
   }

   public boolean isSpatiallyInterpolated() {
      return this.isSpatiallyInterpolated;
   }

   public boolean isFullResolutionBiomes() {
      return this.fullResolutionBiomes;
   }

   public String toString() {
      return Util.getRegisteredName(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, this);
   }

   public static class Builder<Value> {
      private final AttributeType<Value> type;
      private @Nullable Value defaultValue;
      private AttributeRange<Value> valueRange = AttributeRange.<Value>any();
      private boolean isSyncable = false;
      private boolean isPositional = true;
      private boolean isSpatiallyInterpolated = false;
      private boolean fullResolutionBiomes = false;

      public Builder(final AttributeType<Value> type) {
         super();
         this.type = type;
      }

      public Builder<Value> defaultValue(final Value defaultValue) {
         this.defaultValue = defaultValue;
         return this;
      }

      public Builder<Value> valueRange(final AttributeRange<Value> valueRange) {
         this.valueRange = valueRange;
         return this;
      }

      public Builder<Value> syncable() {
         this.isSyncable = true;
         return this;
      }

      public Builder<Value> notPositional() {
         this.isPositional = false;
         return this;
      }

      public Builder<Value> spatiallyInterpolated() {
         this.isSpatiallyInterpolated = true;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder<Value> fullResolutionBiomes() {
         this.fullResolutionBiomes = true;
         return this;
      }

      public EnvironmentAttribute<Value> build() {
         return new EnvironmentAttribute<Value>(this.type, Objects.requireNonNull(this.defaultValue, "Missing default value"), this.valueRange, this.isSyncable, this.isPositional, this.isSpatiallyInterpolated, this.fullResolutionBiomes);
      }
   }
}
