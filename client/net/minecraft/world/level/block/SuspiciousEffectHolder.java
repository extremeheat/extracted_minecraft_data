package net.minecraft.world.level.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.ItemLike;

public interface SuspiciousEffectHolder {
   RandomSource random = RandomSource.create();
   List<Holder<MobEffect>> POTATO_EFFECTS = List.of(
      MobEffects.DIG_SLOWDOWN,
      MobEffects.DIG_SPEED,
      MobEffects.POTATO_OIL,
      MobEffects.LUCK,
      MobEffects.UNLUCK,
      MobEffects.SLOW_FALLING,
      MobEffects.HERO_OF_THE_VILLAGE,
      MobEffects.GLOWING
   );

   SuspiciousStewEffects getSuspiciousEffects();

   static List<SuspiciousEffectHolder> getAllEffectHolders() {
      return BuiltInRegistries.ITEM.stream().map(SuspiciousEffectHolder::tryGet).filter(Objects::nonNull).collect(Collectors.toList());
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   static SuspiciousEffectHolder tryGet(ItemLike var0) {
      Item var3 = var0.asItem();
      if (var3 instanceof BlockItem var1) {
         Block var4 = var1.getBlock();
         if (var4 instanceof SuspiciousEffectHolder) {
            return (SuspiciousEffectHolder)var4;
         }
      }

      Item var2 = var0.asItem();
      if (var2 instanceof SuspiciousEffectHolder) {
         return (SuspiciousEffectHolder)var2;
      } else {
         return var0.asItem() == Items.POISONOUS_POTATO
            ? () -> new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(Util.getRandom(POTATO_EFFECTS, random), random.nextInt(60))))
            : null;
      }
   }
}
