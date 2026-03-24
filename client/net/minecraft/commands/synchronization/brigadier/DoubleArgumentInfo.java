package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import java.util.Objects;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleArgumentInfo implements ArgumentTypeInfo<DoubleArgumentType, Template> {
   public DoubleArgumentInfo() {
      super();
   }

   public void serializeToNetwork(final Template template, final FriendlyByteBuf out) {
      boolean hasMin = template.min != -1.7976931348623157E308;
      boolean hasMax = template.max != 1.7976931348623157E308;
      out.writeByte(ArgumentUtils.createNumberFlags(hasMin, hasMax));
      if (hasMin) {
         out.writeDouble(template.min);
      }

      if (hasMax) {
         out.writeDouble(template.max);
      }

   }

   public Template deserializeFromNetwork(final FriendlyByteBuf in) {
      byte flags = in.readByte();
      double min = ArgumentUtils.numberHasMin(flags) ? in.readDouble() : -1.7976931348623157E308;
      double max = ArgumentUtils.numberHasMax(flags) ? in.readDouble() : 1.7976931348623157E308;
      return new Template(min, max);
   }

   public void serializeToJson(final Template template, final JsonObject out) {
      if (template.min != -1.7976931348623157E308) {
         out.addProperty("min", template.min);
      }

      if (template.max != 1.7976931348623157E308) {
         out.addProperty("max", template.max);
      }

   }

   public Template unpack(final DoubleArgumentType argument) {
      return new Template(argument.getMinimum(), argument.getMaximum());
   }

   public final class Template implements ArgumentTypeInfo.Template<DoubleArgumentType> {
      private final double min;
      private final double max;

      private Template(final double min, final double max) {
         Objects.requireNonNull(DoubleArgumentInfo.this);
         super();
         this.min = min;
         this.max = max;
      }

      public DoubleArgumentType instantiate(final CommandBuildContext context) {
         return DoubleArgumentType.doubleArg(this.min, this.max);
      }

      public ArgumentTypeInfo<DoubleArgumentType, ?> type() {
         return DoubleArgumentInfo.this;
      }
   }
}
