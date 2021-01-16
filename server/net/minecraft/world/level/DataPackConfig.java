package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public class DataPackConfig {
   public static final DataPackConfig DEFAULT = new DataPackConfig(ImmutableList.of("vanilla"), ImmutableList.of());
   public static final Codec<DataPackConfig> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.STRING.listOf().fieldOf("Enabled").forGetter((var0x) -> {
         return var0x.enabled;
      }), Codec.STRING.listOf().fieldOf("Disabled").forGetter((var0x) -> {
         return var0x.disabled;
      })).apply(var0, (BiFunction)(DataPackConfig::new));
   });
   private final List<String> enabled;
   private final List<String> disabled;

   public DataPackConfig(List<String> var1, List<String> var2) {
      super();
      this.enabled = ImmutableList.copyOf((Collection)var1);
      this.disabled = ImmutableList.copyOf((Collection)var2);
   }

   public List<String> getEnabled() {
      return this.enabled;
   }

   public List<String> getDisabled() {
      return this.disabled;
   }
}
