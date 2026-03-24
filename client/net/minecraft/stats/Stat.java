package net.minecraft.stats;

import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jspecify.annotations.Nullable;

public class Stat<T> extends ObjectiveCriteria {
   public static final StreamCodec<RegistryFriendlyByteBuf, Stat<?>> STREAM_CODEC;
   private final StatFormatter formatter;
   private final T value;
   private final StatType<T> type;

   protected Stat(final StatType<T> type, final T value, final StatFormatter formatter) {
      super(buildName(type, value));
      this.type = type;
      this.formatter = formatter;
      this.value = value;
   }

   public static <T> String buildName(final StatType<T> type, final T value) {
      String var10000 = locationToKey(BuiltInRegistries.STAT_TYPE.getKey(type));
      return var10000 + ":" + locationToKey(type.getRegistry().getKey(value));
   }

   private static String locationToKey(final @Nullable Identifier location) {
      return location.toString().replace(':', '.');
   }

   public StatType<T> getType() {
      return this.type;
   }

   public T getValue() {
      return this.value;
   }

   public String format(final int value) {
      return this.formatter.format(value);
   }

   public boolean equals(final Object o) {
      return this == o || o instanceof Stat && Objects.equals(this.getName(), ((Stat)o).getName());
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public String toString() {
      String var10000 = this.getName();
      return "Stat{name=" + var10000 + ", formatter=" + String.valueOf(this.formatter) + "}";
   }

   static {
      STREAM_CODEC = ByteBufCodecs.registry(Registries.STAT_TYPE).dispatch(Stat::getType, StatType::streamCodec);
   }
}
