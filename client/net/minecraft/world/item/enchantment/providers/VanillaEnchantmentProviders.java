package net.minecraft.world.item.enchantment.providers;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.enchantment.Enchantments;

public interface VanillaEnchantmentProviders {
   ResourceKey<EnchantmentProvider> MOB_SPAWN_EQUIPMENT = create("mob_spawn_equipment");
   ResourceKey<EnchantmentProvider> PILLAGER_SPAWN_CROSSBOW = create("pillager_spawn_crossbow");
   ResourceKey<EnchantmentProvider> RAID_PILLAGER_POST_WAVE_3 = create("raid/pillager_post_wave_3");
   ResourceKey<EnchantmentProvider> RAID_PILLAGER_POST_WAVE_5 = create("raid/pillager_post_wave_5");
   ResourceKey<EnchantmentProvider> RAID_VINDICATOR = create("raid/vindicator");
   ResourceKey<EnchantmentProvider> RAID_VINDICATOR_POST_WAVE_5 = create("raid/vindicator_post_wave_5");
   ResourceKey<EnchantmentProvider> ENDERMAN_LOOT_DROP = create("enderman_loot_drop");

   static void bootstrap(BootstrapContext<EnchantmentProvider> var0) {
      HolderGetter var1 = var0.lookup(Registries.ENCHANTMENT);
      var0.register(MOB_SPAWN_EQUIPMENT, new EnchantmentsByCostWithDifficulty(var1.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 5, 17));
      var0.register(PILLAGER_SPAWN_CROSSBOW, new SingleEnchantment(var1.getOrThrow(Enchantments.PIERCING), ConstantInt.of(1)));
      var0.register(RAID_PILLAGER_POST_WAVE_3, new SingleEnchantment(var1.getOrThrow(Enchantments.QUICK_CHARGE), ConstantInt.of(1)));
      var0.register(RAID_PILLAGER_POST_WAVE_5, new SingleEnchantment(var1.getOrThrow(Enchantments.QUICK_CHARGE), ConstantInt.of(2)));
      var0.register(RAID_VINDICATOR, new SingleEnchantment(var1.getOrThrow(Enchantments.SHARPNESS), ConstantInt.of(1)));
      var0.register(RAID_VINDICATOR_POST_WAVE_5, new SingleEnchantment(var1.getOrThrow(Enchantments.SHARPNESS), ConstantInt.of(2)));
      var0.register(ENDERMAN_LOOT_DROP, new SingleEnchantment(var1.getOrThrow(Enchantments.SILK_TOUCH), ConstantInt.of(1)));
   }

   static ResourceKey<EnchantmentProvider> create(String var0) {
      return ResourceKey.create(Registries.ENCHANTMENT_PROVIDER, ResourceLocation.withDefaultNamespace(var0));
   }
}