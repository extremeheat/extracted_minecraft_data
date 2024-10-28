package net.minecraft.world.item;

import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public enum Tiers implements Tier {
   WOOD(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 59, 2.0F, 0.0F, 15, () -> {
      return Ingredient.of(ItemTags.PLANKS);
   }),
   STONE(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, 4.0F, 1.0F, 5, () -> {
      return Ingredient.of(ItemTags.STONE_TOOL_MATERIALS);
   }),
   IRON(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6.0F, 2.0F, 14, () -> {
      return Ingredient.of(Items.IRON_INGOT);
   }),
   DIAMOND(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8.0F, 3.0F, 10, () -> {
      return Ingredient.of(Items.DIAMOND);
   }),
   GOLD(BlockTags.INCORRECT_FOR_GOLD_TOOL, 32, 12.0F, 0.0F, 22, () -> {
      return Ingredient.of(Items.GOLD_INGOT);
   }),
   NETHERITE(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, () -> {
      return Ingredient.of(Items.NETHERITE_INGOT);
   });

   private final TagKey<Block> incorrectBlocksForDrops;
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final Supplier<Ingredient> repairIngredient;

   private Tiers(final TagKey var3, final int var4, final float var5, final float var6, final int var7, final Supplier var8) {
      this.incorrectBlocksForDrops = var3;
      this.uses = var4;
      this.speed = var5;
      this.damage = var6;
      this.enchantmentValue = var7;
      Objects.requireNonNull(var8);
      this.repairIngredient = Suppliers.memoize(var8::get);
   }

   public int getUses() {
      return this.uses;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.incorrectBlocksForDrops;
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairIngredient.get();
   }

   // $FF: synthetic method
   private static Tiers[] $values() {
      return new Tiers[]{WOOD, STONE, IRON, DIAMOND, GOLD, NETHERITE};
   }
}
