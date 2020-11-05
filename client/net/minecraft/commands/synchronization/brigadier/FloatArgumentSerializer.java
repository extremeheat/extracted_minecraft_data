package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class FloatArgumentSerializer implements ArgumentSerializer<FloatArgumentType> {
   public FloatArgumentSerializer() {
      super();
   }

   public void serializeToNetwork(FloatArgumentType var1, FriendlyByteBuf var2) {
      boolean var3 = var1.getMinimum() != -3.4028235E38F;
      boolean var4 = var1.getMaximum() != 3.4028235E38F;
      var2.writeByte(BrigadierArgumentSerializers.createNumberFlags(var3, var4));
      if (var3) {
         var2.writeFloat(var1.getMinimum());
      }

      if (var4) {
         var2.writeFloat(var1.getMaximum());
      }

   }

   public FloatArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      byte var2 = var1.readByte();
      float var3 = BrigadierArgumentSerializers.numberHasMin(var2) ? var1.readFloat() : -3.4028235E38F;
      float var4 = BrigadierArgumentSerializers.numberHasMax(var2) ? var1.readFloat() : 3.4028235E38F;
      return FloatArgumentType.floatArg(var3, var4);
   }

   public void serializeToJson(FloatArgumentType var1, JsonObject var2) {
      if (var1.getMinimum() != -3.4028235E38F) {
         var2.addProperty("min", var1.getMinimum());
      }

      if (var1.getMaximum() != 3.4028235E38F) {
         var2.addProperty("max", var1.getMaximum());
      }

   }

   // $FF: synthetic method
   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }
}
