package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import java.util.Objects;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class FloatArgumentInfo implements ArgumentTypeInfo<FloatArgumentType, Template> {
   public FloatArgumentInfo() {
      super();
   }

   public void serializeToNetwork(final Template template, final FriendlyByteBuf out) {
      boolean hasMin = template.min != -3.4028235E38F;
      boolean hasMax = template.max != 3.4028235E38F;
      out.writeByte(ArgumentUtils.createNumberFlags(hasMin, hasMax));
      if (hasMin) {
         out.writeFloat(template.min);
      }

      if (hasMax) {
         out.writeFloat(template.max);
      }

   }

   public Template deserializeFromNetwork(final FriendlyByteBuf in) {
      byte flags = in.readByte();
      float min = ArgumentUtils.numberHasMin(flags) ? in.readFloat() : -3.4028235E38F;
      float max = ArgumentUtils.numberHasMax(flags) ? in.readFloat() : 3.4028235E38F;
      return new Template(min, max);
   }

   public void serializeToJson(final Template template, final JsonObject out) {
      if (template.min != -3.4028235E38F) {
         out.addProperty("min", template.min);
      }

      if (template.max != 3.4028235E38F) {
         out.addProperty("max", template.max);
      }

   }

   public Template unpack(final FloatArgumentType argument) {
      return new Template(argument.getMinimum(), argument.getMaximum());
   }

   public final class Template implements ArgumentTypeInfo.Template<FloatArgumentType> {
      private final float min;
      private final float max;

      private Template(final float min, final float max) {
         Objects.requireNonNull(FloatArgumentInfo.this);
         super();
         this.min = min;
         this.max = max;
      }

      public FloatArgumentType instantiate(final CommandBuildContext context) {
         return FloatArgumentType.floatArg(this.min, this.max);
      }

      public ArgumentTypeInfo<FloatArgumentType, ?> type() {
         return FloatArgumentInfo.this;
      }
   }
}
