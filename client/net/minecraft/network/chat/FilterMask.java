package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.BitSet;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public class FilterMask {
   public static final Codec<FilterMask> CODEC = StringRepresentable.fromEnum(Type::values).dispatch(FilterMask::type, Type::codec);
   public static final FilterMask FULLY_FILTERED;
   public static final FilterMask PASS_THROUGH;
   public static final Style FILTERED_STYLE;
   private static final MapCodec<FilterMask> PASS_THROUGH_CODEC;
   private static final MapCodec<FilterMask> FULLY_FILTERED_CODEC;
   private static final MapCodec<FilterMask> PARTIALLY_FILTERED_CODEC;
   private static final char HASH = '#';
   private final BitSet mask;
   private final Type type;

   private FilterMask(final BitSet mask, final Type type) {
      super();
      this.mask = mask;
      this.type = type;
   }

   private FilterMask(final BitSet mask) {
      super();
      this.mask = mask;
      this.type = FilterMask.Type.PARTIALLY_FILTERED;
   }

   public FilterMask(final int length) {
      this(new BitSet(length), FilterMask.Type.PARTIALLY_FILTERED);
   }

   private Type type() {
      return this.type;
   }

   private BitSet mask() {
      return this.mask;
   }

   public static FilterMask read(final FriendlyByteBuf input) {
      Type type = (Type)input.readEnum(Type.class);
      FilterMask var10000;
      switch (type.ordinal()) {
         case 0 -> var10000 = PASS_THROUGH;
         case 1 -> var10000 = FULLY_FILTERED;
         case 2 -> var10000 = new FilterMask(input.readBitSet(), FilterMask.Type.PARTIALLY_FILTERED);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static void write(final FriendlyByteBuf output, final FilterMask mask) {
      output.writeEnum(mask.type);
      if (mask.type == FilterMask.Type.PARTIALLY_FILTERED) {
         output.writeBitSet(mask.mask);
      }

   }

   public void setFiltered(final int index) {
      this.mask.set(index);
   }

   public @Nullable String apply(final String text) {
      String var10000;
      switch (this.type.ordinal()) {
         case 0:
            var10000 = text;
            break;
         case 1:
            var10000 = null;
            break;
         case 2:
            char[] chars = text.toCharArray();

            for(int i = 0; i < chars.length && i < this.mask.length(); ++i) {
               if (this.mask.get(i)) {
                  chars[i] = '#';
               }
            }

            var10000 = new String(chars);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public @Nullable Component applyWithFormatting(final String text) {
      MutableComponent var10000;
      switch (this.type.ordinal()) {
         case 0:
            var10000 = Component.literal(text);
            break;
         case 1:
            var10000 = null;
            break;
         case 2:
            MutableComponent result = Component.empty();
            int previousIndex = 0;
            boolean filtered = this.mask.get(0);

            while(true) {
               int nextIndex = filtered ? this.mask.nextClearBit(previousIndex) : this.mask.nextSetBit(previousIndex);
               nextIndex = nextIndex < 0 ? text.length() : nextIndex;
               if (nextIndex == previousIndex) {
                  var10000 = result;
                  return var10000;
               }

               if (filtered) {
                  result.append((Component)Component.literal(StringUtils.repeat('#', nextIndex - previousIndex)).withStyle(FILTERED_STYLE));
               } else {
                  result.append(text.substring(previousIndex, nextIndex));
               }

               filtered = !filtered;
               previousIndex = nextIndex;
            }
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean isEmpty() {
      return this.type == FilterMask.Type.PASS_THROUGH;
   }

   public boolean isFullyFiltered() {
      return this.type == FilterMask.Type.FULLY_FILTERED;
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FilterMask that = (FilterMask)o;
         return this.mask.equals(that.mask) && this.type == that.type;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.mask.hashCode();
      result = 31 * result + this.type.hashCode();
      return result;
   }

   static {
      FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMask.Type.FULLY_FILTERED);
      PASS_THROUGH = new FilterMask(new BitSet(0), FilterMask.Type.PASS_THROUGH);
      FILTERED_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.filtered")));
      PASS_THROUGH_CODEC = MapCodec.unit(PASS_THROUGH);
      FULLY_FILTERED_CODEC = MapCodec.unit(FULLY_FILTERED);
      PARTIALLY_FILTERED_CODEC = ExtraCodecs.BIT_SET.xmap(FilterMask::new, FilterMask::mask).fieldOf("value");
   }

   private static enum Type implements StringRepresentable {
      PASS_THROUGH("pass_through", () -> FilterMask.PASS_THROUGH_CODEC),
      FULLY_FILTERED("fully_filtered", () -> FilterMask.FULLY_FILTERED_CODEC),
      PARTIALLY_FILTERED("partially_filtered", () -> FilterMask.PARTIALLY_FILTERED_CODEC);

      private final String serializedName;
      private final Supplier<MapCodec<FilterMask>> codec;

      private Type(final String serializedName, final Supplier<MapCodec<FilterMask>> codec) {
         this.serializedName = serializedName;
         this.codec = codec;
      }

      public String getSerializedName() {
         return this.serializedName;
      }

      private MapCodec<FilterMask> codec() {
         return (MapCodec)this.codec.get();
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{PASS_THROUGH, FULLY_FILTERED, PARTIALLY_FILTERED};
      }
   }
}
