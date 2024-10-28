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
   private final Codec<T> codec;
   private final Codec<Value<T>> valueCodec;

   protected Property(String var1, Class<T> var2) {
      super();
      this.codec = Codec.STRING.comapFlatMap((var1x) -> {
         return (DataResult)this.getValue(var1x).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               String var10000 = String.valueOf(this);
               return "Unable to read property: " + var10000 + " with value: " + var1x;
            });
         });
      }, this::getName);
      this.valueCodec = this.codec.xmap(this::value, Value::value);
      this.clazz = var2;
      this.name = var1;
   }

   public Value<T> value(T var1) {
      return new Value(this, var1);
   }

   public Value<T> value(StateHolder<?, ?> var1) {
      return new Value(this, var1.getValue(this));
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

   public abstract Collection<T> getPossibleValues();

   public abstract String getName(T var1);

   public abstract Optional<T> getValue(String var1);

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Property)) {
         return false;
      } else {
         Property var2 = (Property)var1;
         return this.clazz.equals(var2.clazz) && this.name.equals(var2.name);
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

   public <U, S extends StateHolder<?, S>> DataResult<S> parseValue(DynamicOps<U> var1, S var2, U var3) {
      DataResult var4 = this.codec.parse(var1, var3);
      return var4.map((var2x) -> {
         return (StateHolder)var2.setValue(this, var2x);
      }).setPartial(var2);
   }

   public static record Value<T extends Comparable<T>>(Property<T> property, T value) {
      public Value(Property<T> var1, T var2) {
         super();
         if (!var1.getPossibleValues().contains(var2)) {
            String var10002 = String.valueOf(var2);
            throw new IllegalArgumentException("Value " + var10002 + " does not belong to property " + String.valueOf(var1));
         } else {
            this.property = var1;
            this.value = var2;
         }
      }

      public String toString() {
         String var10000 = this.property.getName();
         return var10000 + "=" + this.property.getName(this.value);
      }

      public Property<T> property() {
         return this.property;
      }

      public T value() {
         return this.value;
      }
   }
}
