package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record ChangeItemDamage(LevelBasedValue amount) implements EnchantmentEntityEffect {
   public static final MapCodec<ChangeItemDamage> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("amount").forGetter((e) -> e.amount)).apply(i, ChangeItemDamage::new));

   public ChangeItemDamage {
      super();
   }

   public void apply(final ServerLevel serverLevel, final int enchantmentLevel, final EnchantedItemInUse item, final Entity entity, final Vec3 position) {
      ItemStack itemStack = item.itemStack();
      if (itemStack.has(DataComponents.MAX_DAMAGE) && itemStack.has(DataComponents.DAMAGE)) {
         Entity var9 = item.owner();
         ServerPlayer var10000;
         if (var9 instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)var9;
            var10000 = sp;
         } else {
            var10000 = null;
         }

         ServerPlayer player = var10000;
         int change = (int)this.amount.calculate(enchantmentLevel);
         itemStack.hurtAndBreak(change, serverLevel, player, item.onBreak());
      }

   }

   public MapCodec<ChangeItemDamage> codec() {
      return CODEC;
   }
}
