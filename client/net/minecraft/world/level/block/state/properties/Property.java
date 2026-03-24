package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.StateHolder;
import org.jspecify.annotations.Nullable;

public abstract class Property<T extends Comparable<T>> {
   private final Class<T> clazz;
   private final String name;
   private @Nullable Integer hashCode;
   private final Codec<T> codec;
   private final Codec<Value<T>> valueCodec;

   protected Property(final String name, final Class<T> clazz) {
      super();
      this.codec = Codec.STRING.comapFlatMap((namex) -> (DataResult)this.getValue(namex).map(DataResult::success).orElseGet(() -> DataResult.error(() -> {
               String var10000 = String.valueOf(this);
               return "Unable to read property: " + var10000 + " with value: " + namex;
            })), this::getName);
      this.valueCodec = this.codec.xmap(this::value, Value::value);
      this.clazz = clazz;
      this.name = name;
   }

   public Value<T> value(final T value) {
      return new Value<T>(this, value);
   }

   public Value<T> value(final StateHolder<?, ?> stateHolder) {
      return new Value<T>(this, stateHolder.getValue(this));
   }

   public Stream<Value<T>> getAllValues() {
      return this.getPossibleValues().stream().map(this::value);
   }

   public Codec<T> codec() {
      return this.codec;
   }

   public Codec<Value<T>> valueCodec() {
      return this.valueCodec;
   }

   public String getName() {
      return this.name;
   }

   public Class<T> getValueClass() {
      return this.clazz;
   }

   public abstract List<T> getPossibleValues();

   public abstract String getName(final T value);

   public abstract Optional<T> getValue(final String name);

   public abstract int getInternalIndex(final T value);

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Property)) {
         return false;
      } else {
         Property<?> that = (Property)o;
         return this.clazz.equals(that.clazz) && this.name.equals(that.name);
      }
   }

   public final int hashCode() {
      if (this.hashCode == null) {
         this.hashCode = this.generateHashCode();
      }

      return this.hashCode;
   }

   public int generateHashCode() {
      return 31 * this.clazz.hashCode() + this.name.hashCode();
   }

   public <U, S extends StateHolder<?, S>> DataResult<S> parseValue(final DynamicOps<U> ops, final S state, final U value) {
      DataResult<T> parsed = this.codec.parse(ops, value);
      return parsed.map((v) -> (StateHolder)((StateHolder)state).setValue(this, v)).setPartial(state);
   }

   public static record Value<T extends Comparable<T>>(Property<T> property, T value) {
      public Value {
         super();
         if (!property.getPossibleValues().contains(value)) {
            String var10002 = String.valueOf(value);
            throw new IllegalArgumentException("Value " + var10002 + " does not belong to property " + String.valueOf(property));
         }
      }

      public String toString() {
         String var10000 = this.property.getName();
         return var10000 + "=" + this.property.getName(this.value);
      }

      public String valueName() {
         return this.property.getName(this.value);
      }
   }
}
