package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record LootItemKillerMainHandToolCondition(ItemPredicate b) implements LootItemCondition {
   private final ItemPredicate predicate;
   public static final Codec<LootItemKillerMainHandToolCondition> CODEC = ItemPredicate.CODEC
      .xmap(LootItemKillerMainHandToolCondition::new, LootItemKillerMainHandToolCondition::predicate);

   public LootItemKillerMainHandToolCondition(ItemPredicate var1) {
      super();
      this.predicate = var1;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.KILLER_MAIN_HAND_TOOL;
   }

   public boolean test(LootContext var1) {
      DamageSource var2 = var1.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
      if (var2 != null) {
         Entity var3 = var2.getEntity();
         return var3 != null && var3 instanceof LivingEntity var4 ? this.predicate.matches(var4.getMainHandItem()) : false;
      } else {
         return false;
      }
   }

   public static LootItemCondition.Builder killedWithItemInHand(ItemLike var0) {
      return () -> new LootItemKillerMainHandToolCondition(ItemPredicate.Builder.item().of(var0).build());
   }
}
