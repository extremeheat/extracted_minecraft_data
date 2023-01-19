package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class FloatArgumentInfo implements ArgumentTypeInfo<FloatArgumentType, FloatArgumentInfo.Template> {
   public FloatArgumentInfo() {
      super();
   }

   public void serializeToNetwork(FloatArgumentInfo.Template var1, FriendlyByteBuf var2) {
      boolean var3 = var1.min != -3.4028235E38F;
      boolean var4 = var1.max != 3.4028235E38F;
      var2.writeByte(ArgumentUtils.createNumberFlags(var3, var4));
      if (var3) {
         var2.writeFloat(var1.min);
      }

      if (var4) {
         var2.writeFloat(var1.max);
      }
   }

   public FloatArgumentInfo.Template deserializeFromNetwork(FriendlyByteBuf var1) {
      byte var2 = var1.readByte();
      float var3 = ArgumentUtils.numberHasMin(var2) ? var1.readFloat() : -3.4028235E38F;
      float var4 = ArgumentUtils.numberHasMax(var2) ? var1.readFloat() : 3.4028235E38F;
      return new FloatArgumentInfo.Template(var3, var4);
   }

   public void serializeToJson(FloatArgumentInfo.Template var1, JsonObject var2) {
      if (var1.min != -3.4028235E38F) {
         var2.addProperty("min", var1.min);
      }

      if (var1.max != 3.4028235E38F) {
         var2.addProperty("max", var1.max);
      }
   }

   public FloatArgumentInfo.Template unpack(FloatArgumentType var1) {
      return new FloatArgumentInfo.Template(var1.getMinimum(), var1.getMaximum());
   }

   public final class Template implements ArgumentTypeInfo.Template<FloatArgumentType> {
      final float min;
      final float max;

      Template(float var2, float var3) {
         super();
         this.min = var2;
         this.max = var3;
      }

      public FloatArgumentType instantiate(CommandBuildContext var1) {
         return FloatArgumentType.floatArg(this.min, this.max);
      }

      @Override
      public ArgumentTypeInfo<FloatArgumentType, ?> type() {
         return FloatArgumentInfo.this;
      }
   }
}
