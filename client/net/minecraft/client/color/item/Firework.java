package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;

public record Firework(int defaultColor) implements ItemTintSource {
   public static final MapCodec<Firework> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(Firework::defaultColor)).apply(var0, Firework::new));

   public Firework() {
      this(-7697782);
   }

   public Firework(int var1) {
      super();
      this.defaultColor = var1;
   }

   public int calculate(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3) {
      FireworkExplosion var4 = (FireworkExplosion)var1.get(DataComponents.FIREWORK_EXPLOSION);
      IntList var5 = var4 != null ? var4.colors() : IntList.of();
      int var6 = var5.size();
      if (var6 == 0) {
         return this.defaultColor;
      } else if (var6 == 1) {
         return ARGB.opaque(var5.getInt(0));
      } else {
         int var7 = 0;
         int var8 = 0;
         int var9 = 0;

         for(int var10 = 0; var10 < var6; ++var10) {
            int var11 = var5.getInt(var10);
            var7 += ARGB.red(var11);
            var8 += ARGB.green(var11);
            var9 += ARGB.blue(var11);
         }

         return ARGB.color(var7 / var6, var8 / var6, var9 / var6);
      }
   }

   public MapCodec<Firework> type() {
      return MAP_CODEC;
   }
}
