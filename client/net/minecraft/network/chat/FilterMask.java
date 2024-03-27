package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.BitSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.apache.commons.lang3.StringUtils;

public class FilterMask {
   public static final Codec<FilterMask> CODEC = StringRepresentable.fromEnum(FilterMask.Type::values).dispatch(FilterMask::type, FilterMask.Type::codec);
   public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMask.Type.FULLY_FILTERED);
   public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterMask.Type.PASS_THROUGH);
   public static final Style FILTERED_STYLE = Style.EMPTY
      .withColor(ChatFormatting.DARK_GRAY)
      .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.filtered")));
   static final MapCodec<FilterMask> PASS_THROUGH_CODEC = MapCodec.unit(PASS_THROUGH);
   static final MapCodec<FilterMask> FULLY_FILTERED_CODEC = MapCodec.unit(FULLY_FILTERED);
   static final MapCodec<FilterMask> PARTIALLY_FILTERED_CODEC = ExtraCodecs.BIT_SET.xmap(FilterMask::new, FilterMask::mask).fieldOf("value");
   private static final char HASH = '#';
   private final BitSet mask;
   private final FilterMask.Type type;

   private FilterMask(BitSet var1, FilterMask.Type var2) {
      super();
      this.mask = var1;
      this.type = var2;
   }

   private FilterMask(BitSet var1) {
      super();
      this.mask = var1;
      this.type = FilterMask.Type.PARTIALLY_FILTERED;
   }

   public FilterMask(int var1) {
      this(new BitSet(var1), FilterMask.Type.PARTIALLY_FILTERED);
   }

   private FilterMask.Type type() {
      return this.type;
   }

   private BitSet mask() {
      return this.mask;
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
   public Component applyWithFormatting(String var1) {
      return switch(this.type) {
         case PASS_THROUGH -> Component.literal(var1);
         case FULLY_FILTERED -> null;
         case PARTIALLY_FILTERED -> {
            MutableComponent var2 = Component.empty();
            int var3 = 0;
            boolean var4 = this.mask.get(0);

            while(true) {
               int var5 = var4 ? this.mask.nextClearBit(var3) : this.mask.nextSetBit(var3);
               var5 = var5 < 0 ? var1.length() : var5;
               if (var5 == var3) {
                  yield var2;
               }

               if (var4) {
                  var2.append(Component.literal(StringUtils.repeat('#', var5 - var3)).withStyle(FILTERED_STYLE));
               } else {
                  var2.append(var1.substring(var3, var5));
               }

               var4 = !var4;
               var3 = var5;
            }
         }
      };
   }

   public boolean isEmpty() {
      return this.type == FilterMask.Type.PASS_THROUGH;
   }

   public boolean isFullyFiltered() {
      return this.type == FilterMask.Type.FULLY_FILTERED;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         FilterMask var2 = (FilterMask)var1;
         return this.mask.equals(var2.mask) && this.type == var2.type;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.mask.hashCode();
      return 31 * var1 + this.type.hashCode();
   }

   static enum Type implements StringRepresentable {
      PASS_THROUGH("pass_through", () -> FilterMask.PASS_THROUGH_CODEC),
      FULLY_FILTERED("fully_filtered", () -> FilterMask.FULLY_FILTERED_CODEC),
      PARTIALLY_FILTERED("partially_filtered", () -> FilterMask.PARTIALLY_FILTERED_CODEC);

      private final String serializedName;
      private final Supplier<MapCodec<FilterMask>> codec;

      private Type(String var3, Supplier<MapCodec<FilterMask>> var4) {
         this.serializedName = var3;
         this.codec = var4;
      }

      @Override
      public String getSerializedName() {
         return this.serializedName;
      }

      private MapCodec<FilterMask> codec() {
         return (MapCodec<FilterMask>)this.codec.get();
      }
   }
}
