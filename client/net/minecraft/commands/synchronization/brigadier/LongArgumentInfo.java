package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import java.util.Objects;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class LongArgumentInfo implements ArgumentTypeInfo<LongArgumentType, Template> {
   public LongArgumentInfo() {
      super();
   }

   public void serializeToNetwork(final Template template, final FriendlyByteBuf out) {
      boolean hasMin = template.min != -9223372036854775808L;
      boolean hasMax = template.max != 9223372036854775807L;
      out.writeByte(ArgumentUtils.createNumberFlags(hasMin, hasMax));
      if (hasMin) {
         out.writeLong(template.min);
      }

      if (hasMax) {
         out.writeLong(template.max);
      }

   }

   public Template deserializeFromNetwork(final FriendlyByteBuf in) {
      byte flags = in.readByte();
      long min = ArgumentUtils.numberHasMin(flags) ? in.readLong() : -9223372036854775808L;
      long max = ArgumentUtils.numberHasMax(flags) ? in.readLong() : 9223372036854775807L;
      return new Template(min, max);
   }

   public void serializeToJson(final Template template, final JsonObject out) {
      if (template.min != -9223372036854775808L) {
         out.addProperty("min", template.min);
      }

      if (template.max != 9223372036854775807L) {
         out.addProperty("max", template.max);
      }

   }

   public Template unpack(final LongArgumentType argument) {
      return new Template(argument.getMinimum(), argument.getMaximum());
   }

   public final class Template implements ArgumentTypeInfo.Template<LongArgumentType> {
      private final long min;
      private final long max;

      private Template(final long min, final long max) {
         Objects.requireNonNull(LongArgumentInfo.this);
         super();
         this.min = min;
         this.max = max;
      }

      public LongArgumentType instantiate(final CommandBuildContext context) {
         return LongArgumentType.longArg(this.min, this.max);
      }

      public ArgumentTypeInfo<LongArgumentType, ?> type() {
         return LongArgumentInfo.this;
      }
   }
}
