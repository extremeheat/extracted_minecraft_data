package net.minecraft.network.chat.numbers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class NumberFormatTypes {
   public static final MapCodec<NumberFormat> MAP_CODEC = BuiltInRegistries.NUMBER_FORMAT_TYPE
      .byNameCodec()
      .dispatchMap(NumberFormat::type, var0 -> var0.mapCodec().codec());
   public static final Codec<NumberFormat> CODEC = MAP_CODEC.codec();

   public NumberFormatTypes() {
      super();
   }

   public static NumberFormatType<?> bootstrap(Registry<NumberFormatType<?>> var0) {
      NumberFormatType var1 = Registry.register(var0, "blank", BlankFormat.TYPE);
      Registry.register(var0, "styled", StyledFormat.TYPE);
      Registry.register(var0, "fixed", FixedFormat.TYPE);
      return var1;
   }

   public static <T extends NumberFormat> void writeToStream(FriendlyByteBuf var0, T var1) {
      NumberFormatType var2 = var1.type();
      var0.writeId(BuiltInRegistries.NUMBER_FORMAT_TYPE, var2);
      var2.writeToStream(var0, var1);
   }

   public static NumberFormat readFromStream(FriendlyByteBuf var0) {
      NumberFormatType var1 = var0.readById(BuiltInRegistries.NUMBER_FORMAT_TYPE);
      return var1.readFromStream(var0);
   }
}
