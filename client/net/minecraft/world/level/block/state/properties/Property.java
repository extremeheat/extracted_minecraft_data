package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateHolder;

public abstract class Property<T extends Comparable<T>> {
   private final Class<T> clazz;
   private final String name;
   @Nullable
   private Integer hashCode;
   private final Codec<T> codec = Codec.STRING
      .comapFlatMap(
         var1x -> this.getValue(var1x)
               .<DataResult>map(DataResult::success)
               .orElseGet(() -> DataResult.error(() -> "Unable to read property: " + this + " with value: " + var1x)),
         this::getName
      );
   private final Codec<Property.Value<T>> valueCodec = this.codec.xmap(this::value, Property.Value::value);

   protected Property(String var1, Class<T> var2) {
      super();
      this.clazz = var2;
      this.name = var1;
   }

   public Property.Value<T> value(T var1) {
      return new Property.Value<>(this, var1);
   }

   public Property.Value<T> value(StateHolder<?, ?> var1) {
      return new Property.Value<>(this, var1.getValue(this));
   }

   public Stream<Property.Value<T>> getAllValues() {
      return this.getPossibleValues().stream().map(this::value);
   }

   public Codec<T> codec() {
      return this.codec;
   }

   public Codec<Property.Value<T>> valueCodec() {
      return this.valueCodec;
   }

   public String getName() {
      return this.name;
   }

   public Class<T> getValueClass() {
      return this.clazz;
   }

   public abstract Collection<T> getPossibleValues();

   public abstract String getName(T var1);

   public abstract Optional<T> getValue(String var1);

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof Property var2) ? false : this.clazz.equals(var2.clazz) && this.name.equals(var2.name);
      }
   }

   @Override
   public final int hashCode() {
      if (this.hashCode == null) {
         this.hashCode = this.generateHashCode();
      }

      return this.hashCode;
   }

   public int generateHashCode() {
      return 31 * this.clazz.hashCode() + this.name.hashCode();
   }

   public <U, S extends StateHolder<?, S>> DataResult<S> parseValue(DynamicOps<U> var1, S var2, U var3) {
      DataResult var4 = this.codec.parse(var1, var3);
      return var4.map(var2x -> (StateHolder)var2.setValue(this, var2x)).setPartial(var2);
   }

   public static record Value<T extends Comparable<T>>(Property<T> property, T value) {
      public Value(Property<T> property, T value) {
         super();
         if (!property.getPossibleValues().contains(value)) {
            throw new IllegalArgumentException("Value " + value + " does not belong to property " + property);
         } else {
            this.property = property;
            this.value = (T)value;
         }
      }

      public String toString() {
         return this.property.getName() + "=" + this.property.getName(this.value);
      }
   }
}
