package net.minecraft.network.chat;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;

public class FilterMask {
   public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMask.Type.FULLY_FILTERED);
   public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterMask.Type.PASS_THROUGH);
   private static final char HASH = '#';
   private final BitSet mask;
   private final FilterMask.Type type;

   private FilterMask(BitSet var1, FilterMask.Type var2) {
      super();
      this.mask = var1;
      this.type = var2;
   }

   public FilterMask(int var1) {
      this(new BitSet(var1), FilterMask.Type.PARTIALLY_FILTERED);
   }

   public static FilterMask read(FriendlyByteBuf var0) {
      FilterMask.Type var1 = var0.readEnum(FilterMask.Type.class);

      return switch(var1) {
         case PASS_THROUGH -> PASS_THROUGH;
         case FULLY_FILTERED -> FULLY_FILTERED;
         case PARTIALLY_FILTERED -> new FilterMask(var0.readBitSet(), FilterMask.Type.PARTIALLY_FILTERED);
      };
   }

   public static void write(FriendlyByteBuf var0, FilterMask var1) {
      var0.writeEnum(var1.type);
      if (var1.type == FilterMask.Type.PARTIALLY_FILTERED) {
         var0.writeBitSet(var1.mask);
      }
   }

   public void setFiltered(int var1) {
      this.mask.set(var1);
   }

   @Nullable
   public String apply(String var1) {
      return switch(this.type) {
         case PASS_THROUGH -> var1;
         case FULLY_FILTERED -> null;
         case PARTIALLY_FILTERED -> {
            char[] var2 = var1.toCharArray();

            for(int var3 = 0; var3 < var2.length && var3 < this.mask.length(); ++var3) {
               if (this.mask.get(var3)) {
                  var2[var3] = '#';
               }
            }

            yield new String(var2);
         }
      };
   }

   @Nullable
   public Component apply(ChatMessageContent var1) {
      String var2 = var1.plain();
      return Util.mapNullable(this.apply(var2), Component::literal);
   }

   public boolean isEmpty() {
      return this.type == FilterMask.Type.PASS_THROUGH;
   }

   public boolean isFullyFiltered() {
      return this.type == FilterMask.Type.FULLY_FILTERED;
   }

   static enum Type {
      PASS_THROUGH,
      FULLY_FILTERED,
      PARTIALLY_FILTERED;

      private Type() {
      }
   }
}
