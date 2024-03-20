package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public record DyedItemColor(int d, boolean e) implements TooltipProvider {
   private final int rgb;
   private final boolean showInTooltip;
   private static final Codec<DyedItemColor> FULL_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.INT.fieldOf("rgb").forGetter(DyedItemColor::rgb),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "show_in_tooltip", true).forGetter(DyedItemColor::showInTooltip)
            )
            .apply(var0, DyedItemColor::new)
   );
   public static final Codec<DyedItemColor> CODEC = ExtraCodecs.withAlternative(FULL_CODEC, Codec.INT, var0 -> new DyedItemColor(var0, true));
   public static final StreamCodec<ByteBuf, DyedItemColor> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, DyedItemColor::rgb, ByteBufCodecs.BOOL, DyedItemColor::showInTooltip, DyedItemColor::new
   );
   public static final int LEATHER_COLOR = -6265536;

   public DyedItemColor(int var1, boolean var2) {
      super();
      this.rgb = var1;
      this.showInTooltip = var2;
   }

   public static int getOrDefault(ItemStack var0, int var1) {
      DyedItemColor var2 = var0.get(DataComponents.DYED_COLOR);
      return var2 != null ? FastColor.ARGB32.opaque(var2.rgb()) : var1;
   }

   public static ItemStack applyDyes(ItemStack var0, List<DyeItem> var1) {
      if (!var0.is(ItemTags.DYEABLE)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var2 = var0.copyWithCount(1);
         int var3 = 0;
         int var4 = 0;
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;
         DyedItemColor var8 = var2.get(DataComponents.DYED_COLOR);
         if (var8 != null) {
            int var9 = FastColor.ARGB32.red(var8.rgb());
            int var10 = FastColor.ARGB32.green(var8.rgb());
            int var11 = FastColor.ARGB32.blue(var8.rgb());
            var6 += Math.max(var9, Math.max(var10, var11));
            var3 += var9;
            var4 += var10;
            var5 += var11;
            ++var7;
         }

         for(DyeItem var19 : var1) {
            float[] var22 = var19.getDyeColor().getTextureDiffuseColors();
            int var12 = (int)(var22[0] * 255.0F);
            int var13 = (int)(var22[1] * 255.0F);
            int var14 = (int)(var22[2] * 255.0F);
            var6 += Math.max(var12, Math.max(var13, var14));
            var3 += var12;
            var4 += var13;
            var5 += var14;
            ++var7;
         }

         int var17 = var3 / var7;
         int var20 = var4 / var7;
         int var23 = var5 / var7;
         float var25 = (float)var6 / (float)var7;
         float var26 = (float)Math.max(var17, Math.max(var20, var23));
         var17 = (int)((float)var17 * var25 / var26);
         var20 = (int)((float)var20 * var25 / var26);
         var23 = (int)((float)var23 * var25 / var26);
         int var27 = FastColor.ARGB32.color(0, var17, var20, var23);
         boolean var15 = var8 == null || var8.showInTooltip();
         var2.set(DataComponents.DYED_COLOR, new DyedItemColor(var27, var15));
         return var2;
      }
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      if (this.showInTooltip) {
         if (var2.isAdvanced()) {
            var1.accept(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.rgb)).withStyle(ChatFormatting.GRAY));
         } else {
            var1.accept(Component.translatable("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
         }
      }
   }

   public DyedItemColor withTooltip(boolean var1) {
      return new DyedItemColor(this.rgb, var1);
   }
}
