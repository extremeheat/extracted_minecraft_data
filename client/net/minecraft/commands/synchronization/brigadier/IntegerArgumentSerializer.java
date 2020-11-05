package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class IntegerArgumentSerializer implements ArgumentSerializer<IntegerArgumentType> {
   public IntegerArgumentSerializer() {
      super();
   }

   public void serializeToNetwork(IntegerArgumentType var1, FriendlyByteBuf var2) {
      boolean var3 = var1.getMinimum() != -2147483648;
      boolean var4 = var1.getMaximum() != 2147483647;
      var2.writeByte(BrigadierArgumentSerializers.createNumberFlags(var3, var4));
      if (var3) {
         var2.writeInt(var1.getMinimum());
      }

      if (var4) {
         var2.writeInt(var1.getMaximum());
      }

   }

   public IntegerArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      byte var2 = var1.readByte();
      int var3 = BrigadierArgumentSerializers.numberHasMin(var2) ? var1.readInt() : -2147483648;
      int var4 = BrigadierArgumentSerializers.numberHasMax(var2) ? var1.readInt() : 2147483647;
      return IntegerArgumentType.integer(var3, var4);
   }

   public void serializeToJson(IntegerArgumentType var1, JsonObject var2) {
      if (var1.getMinimum() != -2147483648) {
         var2.addProperty("min", var1.getMinimum());
      }

      if (var1.getMaximum() != 2147483647) {
         var2.addProperty("max", var1.getMaximum());
      }

   }

   // $FF: synthetic method
   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }
}
