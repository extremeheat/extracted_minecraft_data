package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;

public enum ArmorMaterials implements ArmorMaterial {
   LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> {
      return Ingredient.of(Items.LEATHER);
   }),
   CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> {
      return Ingredient.of(Items.IRON_INGOT);
   }),
   IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> {
      return Ingredient.of(Items.IRON_INGOT);
   }),
   GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, () -> {
      return Ingredient.of(Items.GOLD_INGOT);
   }),
   DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, () -> {
      return Ingredient.of(Items.DIAMOND);
   }),
   TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, () -> {
      return Ingredient.of(Items.SCUTE);
   });

   private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
   private final String name;
   private final int durabilityMultiplier;
   private final int[] slotProtections;
   private final int enchantmentValue;
   private final SoundEvent sound;
   private final float toughness;
   private final LazyLoadedValue<Ingredient> repairIngredient;

   private ArmorMaterials(String var3, int var4, int[] var5, int var6, SoundEvent var7, float var8, Supplier<Ingredient> var9) {
      this.name = var3;
      this.durabilityMultiplier = var4;
      this.slotProtections = var5;
      this.enchantmentValue = var6;
      this.sound = var7;
      this.toughness = var8;
      this.repairIngredient = new LazyLoadedValue(var9);
   }

   public int getDurabilityForSlot(EquipmentSlot var1) {
      return HEALTH_PER_SLOT[var1.getIndex()] * this.durabilityMultiplier;
   }

   public int getDefenseForSlot(EquipmentSlot var1) {
      return this.slotProtections[var1.getIndex()];
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public SoundEvent getEquipSound() {
      return this.sound;
   }

   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairIngredient.get();
   }

   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.toughness;
   }
}
