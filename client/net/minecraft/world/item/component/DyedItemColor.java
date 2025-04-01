package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public record DyedItemColor(int rgb) implements TooltipProvider {
   public static final Codec<DyedItemColor> CODEC;
   public static final StreamCodec<ByteBuf, DyedItemColor> STREAM_CODEC;
   public static final int LEATHER_COLOR = -6265536;

   public DyedItemColor(int var1) {
      super();
      this.rgb = var1;
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
         if (var8 != null) {
            int var9 = ARGB.red(var8.rgb());
            int var10 = ARGB.green(var8.rgb());
            int var11 = ARGB.blue(var8.rgb());
            var6 += Math.max(var9, Math.max(var10, var11));
            var3 += var9;
            var4 += var10;
            var5 += var11;
            ++var7;
         }

         for(DyeItem var18 : var1) {
            int var21 = var18.getDyeColor().getTextureDiffuseColor();
            int var12 = ARGB.red(var21);
            int var13 = ARGB.green(var21);
            int var14 = ARGB.blue(var21);
            var6 += Math.max(var12, Math.max(var13, var14));
            var3 += var12;
            var4 += var13;
            var5 += var14;
            ++var7;
         }

         int var16 = var3 / var7;
         int var19 = var4 / var7;
         int var22 = var5 / var7;
         float var24 = (float)var6 / (float)var7;
         float var25 = (float)Math.max(var16, Math.max(var19, var22));
         var16 = (int)((float)var16 * var24 / var25);
         var19 = (int)((float)var19 * var24 / var25);
         var22 = (int)((float)var22 * var24 / var25);
         int var26 = ARGB.color(0, var16, var19, var22);
         var2.set(DataComponents.DYED_COLOR, new DyedItemColor(var26));
         return var2;
      }
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      if (var3.isAdvanced()) {
         var2.accept(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.rgb)).withStyle(ChatFormatting.GRAY));
      } else {
         var2.accept(Component.translatable("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
      }

   }

   static {
      CODEC = ExtraCodecs.RGB_COLOR_CODEC.xmap(DyedItemColor::new, DyedItemColor::rgb);
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, DyedItemColor::rgb, DyedItemColor::new);
   }
}
