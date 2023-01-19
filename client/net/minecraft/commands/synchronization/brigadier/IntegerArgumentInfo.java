package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class IntegerArgumentInfo implements ArgumentTypeInfo<IntegerArgumentType, IntegerArgumentInfo.Template> {
   public IntegerArgumentInfo() {
      super();
   }

   public void serializeToNetwork(IntegerArgumentInfo.Template var1, FriendlyByteBuf var2) {
      boolean var3 = var1.min != -2147483648;
      boolean var4 = var1.max != 2147483647;
      var2.writeByte(ArgumentUtils.createNumberFlags(var3, var4));
      if (var3) {
         var2.writeInt(var1.min);
      }

      if (var4) {
         var2.writeInt(var1.max);
      }
   }

   public IntegerArgumentInfo.Template deserializeFromNetwork(FriendlyByteBuf var1) {
      byte var2 = var1.readByte();
      int var3 = ArgumentUtils.numberHasMin(var2) ? var1.readInt() : -2147483648;
      int var4 = ArgumentUtils.numberHasMax(var2) ? var1.readInt() : 2147483647;
      return new IntegerArgumentInfo.Template(var3, var4);
   }

   public void serializeToJson(IntegerArgumentInfo.Template var1, JsonObject var2) {
      if (var1.min != -2147483648) {
         var2.addProperty("min", var1.min);
      }

      if (var1.max != 2147483647) {
         var2.addProperty("max", var1.max);
      }
   }

   public IntegerArgumentInfo.Template unpack(IntegerArgumentType var1) {
      return new IntegerArgumentInfo.Template(var1.getMinimum(), var1.getMaximum());
   }

   public final class Template implements ArgumentTypeInfo.Template<IntegerArgumentType> {
      final int min;
      final int max;

      Template(int var2, int var3) {
         super();
         this.min = var2;
         this.max = var3;
      }

      public IntegerArgumentType instantiate(CommandBuildContext var1) {
         return IntegerArgumentType.integer(this.min, this.max);
      }

      @Override
      public ArgumentTypeInfo<IntegerArgumentType, ?> type() {
         return IntegerArgumentInfo.this;
      }
   }
}
