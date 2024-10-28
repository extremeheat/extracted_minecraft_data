package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleArgumentInfo implements ArgumentTypeInfo<DoubleArgumentType, Template> {
   public DoubleArgumentInfo() {
      super();
   }

   public void serializeToNetwork(Template var1, FriendlyByteBuf var2) {
      boolean var3 = var1.min != -1.7976931348623157E308;
      boolean var4 = var1.max != 1.7976931348623157E308;
      var2.writeByte(ArgumentUtils.createNumberFlags(var3, var4));
      if (var3) {
         var2.writeDouble(var1.min);
      }

      if (var4) {
         var2.writeDouble(var1.max);
      }

   }

   public Template deserializeFromNetwork(FriendlyByteBuf var1) {
      byte var2 = var1.readByte();
      double var3 = ArgumentUtils.numberHasMin(var2) ? var1.readDouble() : -1.7976931348623157E308;
      double var5 = ArgumentUtils.numberHasMax(var2) ? var1.readDouble() : 1.7976931348623157E308;
      return new Template(var3, var5);
   }

   public void serializeToJson(Template var1, JsonObject var2) {
      if (var1.min != -1.7976931348623157E308) {
         var2.addProperty("min", var1.min);
      }

      if (var1.max != 1.7976931348623157E308) {
         var2.addProperty("max", var1.max);
      }

   }

   public Template unpack(DoubleArgumentType var1) {
      return new Template(var1.getMinimum(), var1.getMaximum());
   }

   // $FF: synthetic method
   public ArgumentTypeInfo.Template deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }

   public final class Template implements ArgumentTypeInfo.Template<DoubleArgumentType> {
      final double min;
      final double max;

      Template(double var2, double var4) {
         super();
         this.min = var2;
         this.max = var4;
      }

      public DoubleArgumentType instantiate(CommandBuildContext var1) {
         return DoubleArgumentType.doubleArg(this.min, this.max);
      }

      public ArgumentTypeInfo<DoubleArgumentType, ?> type() {
         return DoubleArgumentInfo.this;
      }

      // $FF: synthetic method
      public ArgumentType instantiate(CommandBuildContext var1) {
         return this.instantiate(var1);
      }
   }
}
