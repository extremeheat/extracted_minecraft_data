package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class LongArgumentInfo implements ArgumentTypeInfo<LongArgumentType, LongArgumentInfo.Template> {
   public LongArgumentInfo() {
      super();
   }

   public void serializeToNetwork(LongArgumentInfo.Template var1, FriendlyByteBuf var2) {
      boolean var3 = var1.min != -9223372036854775808L;
      boolean var4 = var1.max != 9223372036854775807L;
      var2.writeByte(ArgumentUtils.createNumberFlags(var3, var4));
      if (var3) {
         var2.writeLong(var1.min);
      }

      if (var4) {
         var2.writeLong(var1.max);
      }
   }

   public LongArgumentInfo.Template deserializeFromNetwork(FriendlyByteBuf var1) {
      byte var2 = var1.readByte();
      long var3 = ArgumentUtils.numberHasMin(var2) ? var1.readLong() : -9223372036854775808L;
      long var5 = ArgumentUtils.numberHasMax(var2) ? var1.readLong() : 9223372036854775807L;
      return new LongArgumentInfo.Template(var3, var5);
   }

   public void serializeToJson(LongArgumentInfo.Template var1, JsonObject var2) {
      if (var1.min != -9223372036854775808L) {
         var2.addProperty("min", var1.min);
      }

      if (var1.max != 9223372036854775807L) {
         var2.addProperty("max", var1.max);
      }
   }

   public LongArgumentInfo.Template unpack(LongArgumentType var1) {
      return new LongArgumentInfo.Template(var1.getMinimum(), var1.getMaximum());
   }

   public final class Template implements ArgumentTypeInfo.Template<LongArgumentType> {
      final long min;
      final long max;

      Template(long var2, long var4) {
         super();
         this.min = var2;
         this.max = var4;
      }

      public LongArgumentType instantiate(CommandBuildContext var1) {
         return LongArgumentType.longArg(this.min, this.max);
      }

      @Override
      public ArgumentTypeInfo<LongArgumentType, ?> type() {
         return LongArgumentInfo.this;
      }
   }
}
