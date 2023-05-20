package net.minecraft.world.item;

import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;

public enum ArmorMaterials implements StringRepresentable, ArmorMaterial {
   LEATHER("leather", 5, Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 1);
      var0.put(ArmorItem.Type.LEGGINGS, 2);
      var0.put(ArmorItem.Type.CHESTPLATE, 3);
      var0.put(ArmorItem.Type.HELMET, 1);
   }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Items.LEATHER)),
   CHAIN("chainmail", 15, Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 1);
      var0.put(ArmorItem.Type.LEGGINGS, 4);
      var0.put(ArmorItem.Type.CHESTPLATE, 5);
      var0.put(ArmorItem.Type.HELMET, 2);
   }), 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.of(Items.IRON_INGOT)),
   IRON("iron", 15, Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 2);
      var0.put(ArmorItem.Type.LEGGINGS, 5);
      var0.put(ArmorItem.Type.CHESTPLATE, 6);
      var0.put(ArmorItem.Type.HELMET, 2);
   }), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.of(Items.IRON_INGOT)),
   GOLD("gold", 7, Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 1);
      var0.put(ArmorItem.Type.LEGGINGS, 3);
      var0.put(ArmorItem.Type.CHESTPLATE, 5);
      var0.put(ArmorItem.Type.HELMET, 2);
   }), 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> Ingredient.of(Items.GOLD_INGOT)),
   DIAMOND("diamond", 33, Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 3);
      var0.put(ArmorItem.Type.LEGGINGS, 6);
      var0.put(ArmorItem.Type.CHESTPLATE, 8);
      var0.put(ArmorItem.Type.HELMET, 3);
   }), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> Ingredient.of(Items.DIAMOND)),
   TURTLE("turtle", 25, Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 2);
      var0.put(ArmorItem.Type.LEGGINGS, 5);
      var0.put(ArmorItem.Type.CHESTPLATE, 6);
      var0.put(ArmorItem.Type.HELMET, 2);
   }), 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> Ingredient.of(Items.SCUTE)),
   NETHERITE("netherite", 37, Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 3);
      var0.put(ArmorItem.Type.LEGGINGS, 6);
      var0.put(ArmorItem.Type.CHESTPLATE, 8);
      var0.put(ArmorItem.Type.HELMET, 3);
   }), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> Ingredient.of(Items.NETHERITE_INGOT));

   public static final StringRepresentable.EnumCodec<ArmorMaterials> CODEC = StringRepresentable.fromEnum(ArmorMaterials::values);
   private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 13);
      var0.put(ArmorItem.Type.LEGGINGS, 15);
      var0.put(ArmorItem.Type.CHESTPLATE, 16);
      var0.put(ArmorItem.Type.HELMET, 11);
   });
   private final String name;
   private final int durabilityMultiplier;
   private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
   private final int enchantmentValue;
   private final SoundEvent sound;
   private final float toughness;
   private final float knockbackResistance;
   private final LazyLoadedValue<Ingredient> repairIngredient;

   private ArmorMaterials(
      String var3, int var4, EnumMap<ArmorItem.Type, Integer> var5, int var6, SoundEvent var7, float var8, float var9, Supplier<Ingredient> var10
   ) {
      this.name = var3;
      this.durabilityMultiplier = var4;
      this.protectionFunctionForType = var5;
      this.enchantmentValue = var6;
      this.sound = var7;
      this.toughness = var8;
      this.knockbackResistance = var9;
      this.repairIngredient = new LazyLoadedValue<>(var10);
   }

   @Override
   public int getDurabilityForType(ArmorItem.Type var1) {
      return HEALTH_FUNCTION_FOR_TYPE.get(var1) * this.durabilityMultiplier;
   }

   @Override
   public int getDefenseForType(ArmorItem.Type var1) {
      return this.protectionFunctionForType.get(var1);
   }

   @Override
   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   @Override
   public SoundEvent getEquipSound() {
      return this.sound;
   }

   @Override
   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public float getToughness() {
      return this.toughness;
   }

   @Override
   public float getKnockbackResistance() {
      return this.knockbackResistance;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }
}
