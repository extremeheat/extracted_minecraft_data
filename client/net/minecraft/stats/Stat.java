package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Stat<T> extends ObjectiveCriteria {
   public static final StreamCodec<RegistryFriendlyByteBuf, Stat<?>> STREAM_CODEC;
   private final StatFormatter formatter;
   private final T value;
   private final StatType<T> type;

   protected Stat(StatType<T> var1, T var2, StatFormatter var3) {
      super(buildName(var1, var2));
      this.type = var1;
      this.formatter = var3;
      this.value = var2;
   }

   public static <T> String buildName(StatType<T> var0, T var1) {
      String var10000 = locationToKey(BuiltInRegistries.STAT_TYPE.getKey(var0));
      return var10000 + ":" + locationToKey(var0.getRegistry().getKey(var1));
   }

   private static <T> String locationToKey(@Nullable ResourceLocation var0) {
      return var0.toString().replace(':', '.');
   }

   public StatType<T> getType() {
      return this.type;
   }

   public T getValue() {
      return this.value;
   }

   public String format(int var1) {
      return this.formatter.format(var1);
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof Stat && Objects.equals(this.getName(), ((Stat)var1).getName());
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
