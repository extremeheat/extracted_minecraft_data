package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public record DyedItemColor(int rgb, boolean showInTooltip) implements TooltipProvider {
   private static final Codec<DyedItemColor> FULL_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("rgb").forGetter(DyedItemColor::rgb), Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(DyedItemColor::showInTooltip)).apply(var0, DyedItemColor::new);
   });
   public static final Codec<DyedItemColor> CODEC;
   public static final StreamCodec<ByteBuf, DyedItemColor> STREAM_CODEC;
   public static final int LEATHER_COLOR = -6265536;

   public DyedItemColor(int var1, boolean var2) {
      super();
      this.rgb = var1;
      this.showInTooltip = var2;
   }

   public static int getOrDefault(ItemStack var0, int var1) {
      DyedItemColor var2 = (DyedItemColor)var0.get(DataComponents.DYED_COLOR);
      return var2 != null ? ARGB.opaque(var2.rgb()) : var1;
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
         DyedItemColor var8 = (DyedItemColor)var2.get(DataComponents.DYED_COLOR);
         int var9;
         int var10;
         int var11;
         if (var8 != null) {
            var9 = ARGB.red(var8.rgb());
            var10 = ARGB.green(var8.rgb());
            var11 = ARGB.blue(var8.rgb());
            var6 += Math.max(var9, Math.max(var10, var11));
            var3 += var9;
            var4 += var10;
            var5 += var11;
            ++var7;
         }

         int var14;
         for(Iterator var16 = var1.iterator(); var16.hasNext(); ++var7) {
            DyeItem var17 = (DyeItem)var16.next();
            var11 = var17.getDyeColor().getTextureDiffuseColor();
            int var12 = ARGB.red(var11);
            int var13 = ARGB.green(var11);
            var14 = ARGB.blue(var11);
            var6 += Math.max(var12, Math.max(var13, var14));
            var3 += var12;
            var4 += var13;
            var5 += var14;
         }

         var9 = var3 / var7;
         var10 = var4 / var7;
         var11 = var5 / var7;
         float var18 = (float)var6 / (float)var7;
         float var19 = (float)Math.max(var9, Math.max(var10, var11));
         var9 = (int)((float)var9 * var18 / var19);
         var10 = (int)((float)var10 * var18 / var19);
         var11 = (int)((float)var11 * var18 / var19);
         var14 = ARGB.color(0, var9, var10, var11);
         boolean var15 = var8 == null || var8.showInTooltip();
         var2.set(DataComponents.DYED_COLOR, new DyedItemColor(var14, var15));
         return var2;
      }
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3) {
      if (this.showInTooltip) {
         if (var3.isAdvanced()) {
            var2.accept(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.rgb)).withStyle(ChatFormatting.GRAY));
         } else {
            var2.accept(Component.translatable("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
         }

      }
   }

   public DyedItemColor withTooltip(boolean var1) {
      return new DyedItemColor(this.rgb, var1);
   }

   public int rgb() {
      return this.rgb;
   }

   public boolean showInTooltip() {
      return this.showInTooltip;
   }

   static {
      CODEC = Codec.withAlternative(FULL_CODEC, Codec.INT, (var0) -> {
         return new DyedItemColor(var0, true);
      });
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, DyedItemColor::rgb, ByteBufCodecs.BOOL, DyedItemColor::showInTooltip, DyedItemColor::new);
   }
}
