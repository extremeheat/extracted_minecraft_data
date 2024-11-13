package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class Time extends NeedleDirectionHelper implements RangeSelectItemModelProperty {
   public static final MapCodec<Time> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(NeedleDirectionHelper::wobble), Codec.BOOL.optionalFieldOf("natural_only", true).forGetter((var0x) -> var0x.naturalOnly)).apply(var0, Time::new));
   private final boolean naturalOnly;
   private final RandomSource randomSource = RandomSource.create();
   private final NeedleDirectionHelper.Wobbler wobbler;

   public Time(boolean var1, boolean var2) {
      super(var1);
      this.naturalOnly = var2;
      this.wobbler = this.newWobbler(0.9F);
   }

   protected float calculate(ItemStack var1, ClientLevel var2, int var3, Entity var4) {
      float var5;
      if (this.naturalOnly && !var2.dimensionType().natural()) {
         var5 = this.randomSource.nextFloat();
      } else {
         var5 = var2.getTimeOfDay(1.0F);
      }

      long var6 = var2.getGameTime();
      if (this.wobbler.shouldUpdate(var6)) {
         this.wobbler.update(var6, var5);
      }

      return this.wobbler.rotation();
   }

   public MapCodec<Time> type() {
      return MAP_CODEC;
   }
}
