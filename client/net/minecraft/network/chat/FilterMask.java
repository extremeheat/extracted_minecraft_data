package net.minecraft.network.chat;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;

public class FilterMask {
   public static final FilterMask FULLY_FILTERED;
   public static final FilterMask PASS_THROUGH;
   private static final char HASH = '#';
   private final BitSet mask;
   private final Type type;

   private FilterMask(BitSet var1, Type var2) {
      super();
      this.mask = var1;
      this.type = var2;
   }

   public FilterMask(int var1) {
      this(new BitSet(var1), FilterMask.Type.PARTIALLY_FILTERED);
   }

   public static FilterMask read(FriendlyByteBuf var0) {
      Type var1 = (Type)var0.readEnum(Type.class);
      FilterMask var10000;
      switch (var1) {
         case PASS_THROUGH:
            var10000 = PASS_THROUGH;
            break;
         case FULLY_FILTERED:
            var10000 = FULLY_FILTERED;
            break;
         case PARTIALLY_FILTERED:
            var10000 = new FilterMask(var0.readBitSet(), FilterMask.Type.PARTIALLY_FILTERED);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
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
      String var10000;
      switch (this.type) {
         case PASS_THROUGH:
            var10000 = var1;
            break;
         case FULLY_FILTERED:
            var10000 = null;
            break;
         case PARTIALLY_FILTERED:
            char[] var2 = var1.toCharArray();

            for(int var3 = 0; var3 < var2.length && var3 < this.mask.length(); ++var3) {
               if (this.mask.get(var3)) {
                  var2[var3] = '#';
               }
            }

            var10000 = new String(var2);
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   @Nullable
   public Component apply(ChatMessageContent var1) {
      String var2 = var1.plain();
      return (Component)Util.mapNullable(this.apply(var2), Component::literal);
   }

   public boolean isEmpty() {
      return this.type == FilterMask.Type.PASS_THROUGH;
   }

   public boolean isFullyFiltered() {
      return this.type == FilterMask.Type.FULLY_FILTERED;
   }

   static {
      FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMask.Type.FULLY_FILTERED);
      PASS_THROUGH = new FilterMask(new BitSet(0), FilterMask.Type.PASS_THROUGH);
   }

   private static enum Type {
      PASS_THROUGH,
      FULLY_FILTERED,
      PARTIALLY_FILTERED;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{PASS_THROUGH, FULLY_FILTERED, PARTIALLY_FILTERED};
      }
   }
}
