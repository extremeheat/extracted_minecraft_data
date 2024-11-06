package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;

public record Firework(int defaultColor) implements ItemTintSource {
   public static final MapCodec<Firework> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(Firework::defaultColor)).apply(var0, Firework::new);
   });

   public Firework() {
      this(-7697782);
   }

   public Firework(int var1) {
      super();
      this.defaultColor = var1;
   }

   public int calculate(ItemStack var1) {
      FireworkExplosion var2 = (FireworkExplosion)var1.get(DataComponents.FIREWORK_EXPLOSION);
      IntList var3 = var2 != null ? var2.colors() : IntList.of();
      int var4 = var3.size();
      if (var4 == 0) {
         return this.defaultColor;
      } else if (var4 == 1) {
         return ARGB.opaque(var3.getInt(0));
      } else {
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;

         for(int var8 = 0; var8 < var4; ++var8) {
            int var9 = var3.getInt(var8);
            var5 += ARGB.red(var9);
            var6 += ARGB.green(var9);
            var7 += ARGB.blue(var9);
         }

         return ARGB.color(var5 / var4, var6 / var4, var7 / var4);
      }
   }

   public MapCodec<Firework> type() {
      return MAP_CODEC;
   }

   public int defaultColor() {
      return this.defaultColor;
   }
}
