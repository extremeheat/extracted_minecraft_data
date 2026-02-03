package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.Objects;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class IntegerArgumentInfo implements ArgumentTypeInfo<IntegerArgumentType, Template> {
   public IntegerArgumentInfo() {
      super();
   }

   public void serializeToNetwork(final Template template, final FriendlyByteBuf out) {
      boolean hasMin = template.min != -2147483648;
      boolean hasMax = template.max != 2147483647;
      out.writeByte(ArgumentUtils.createNumberFlags(hasMin, hasMax));
      if (hasMin) {
         out.writeInt(template.min);
      }

      if (hasMax) {
         out.writeInt(template.max);
      }

   }

   public Template deserializeFromNetwork(final FriendlyByteBuf in) {
      byte flags = in.readByte();
      int min = ArgumentUtils.numberHasMin(flags) ? in.readInt() : -2147483648;
      int max = ArgumentUtils.numberHasMax(flags) ? in.readInt() : 2147483647;
      return new Template(min, max);
   }

   public void serializeToJson(final Template template, final JsonObject out) {
      if (template.min != -2147483648) {
         out.addProperty("min", template.min);
      }

      if (template.max != 2147483647) {
         out.addProperty("max", template.max);
      }

   }

   public Template unpack(final IntegerArgumentType argument) {
      return new Template(argument.getMinimum(), argument.getMaximum());
   }

   public final class Template implements ArgumentTypeInfo.Template<IntegerArgumentType> {
      private final int min;
      private final int max;

      private Template(final int min, final int max) {
         Objects.requireNonNull(IntegerArgumentInfo.this);
         super();
         this.min = min;
         this.max = max;
      }

      public IntegerArgumentType instantiate(final CommandBuildContext context) {
         return IntegerArgumentType.integer(this.min, this.max);
      }

      public ArgumentTypeInfo<IntegerArgumentType, ?> type() {
         return IntegerArgumentInfo.this;
      }
   }
}
