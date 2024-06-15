package net.minecraft.world.item;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Ingredient;

public class ArmorMaterials {
   public static final Holder<ArmorMaterial> LEATHER = register(
      "leather",
      Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
         var0.put(ArmorItem.Type.BOOTS, 1);
         var0.put(ArmorItem.Type.LEGGINGS, 2);
         var0.put(ArmorItem.Type.CHESTPLATE, 3);
         var0.put(ArmorItem.Type.HELMET, 1);
         var0.put(ArmorItem.Type.BODY, 3);
      }),
      15,
      SoundEvents.ARMOR_EQUIP_LEATHER,
      0.0F,
      0.0F,
      () -> Ingredient.of(Items.LEATHER),
      List.of(new ArmorMaterial.Layer(new ResourceLocation("leather"), "", true), new ArmorMaterial.Layer(new ResourceLocation("leather"), "_overlay", false))
   );
   public static final Holder<ArmorMaterial> CHAIN = register("chainmail", Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 1);
      var0.put(ArmorItem.Type.LEGGINGS, 4);
      var0.put(ArmorItem.Type.CHESTPLATE, 5);
      var0.put(ArmorItem.Type.HELMET, 2);
      var0.put(ArmorItem.Type.BODY, 4);
   }), 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.of(Items.IRON_INGOT));
   public static final Holder<ArmorMaterial> IRON = register("iron", Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 2);
      var0.put(ArmorItem.Type.LEGGINGS, 5);
      var0.put(ArmorItem.Type.CHESTPLATE, 6);
      var0.put(ArmorItem.Type.HELMET, 2);
      var0.put(ArmorItem.Type.BODY, 5);
   }), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.of(Items.IRON_INGOT));
   public static final Holder<ArmorMaterial> GOLD = register("gold", Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 1);
      var0.put(ArmorItem.Type.LEGGINGS, 3);
      var0.put(ArmorItem.Type.CHESTPLATE, 5);
      var0.put(ArmorItem.Type.HELMET, 2);
      var0.put(ArmorItem.Type.BODY, 7);
   }), 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> Ingredient.of(Items.GOLD_INGOT));
   public static final Holder<ArmorMaterial> DIAMOND = register("diamond", Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 3);
      var0.put(ArmorItem.Type.LEGGINGS, 6);
      var0.put(ArmorItem.Type.CHESTPLATE, 8);
      var0.put(ArmorItem.Type.HELMET, 3);
      var0.put(ArmorItem.Type.BODY, 11);
   }), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> Ingredient.of(Items.DIAMOND));
   public static final Holder<ArmorMaterial> TURTLE = register("turtle", Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 2);
      var0.put(ArmorItem.Type.LEGGINGS, 5);
      var0.put(ArmorItem.Type.CHESTPLATE, 6);
      var0.put(ArmorItem.Type.HELMET, 2);
      var0.put(ArmorItem.Type.BODY, 5);
   }), 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> Ingredient.of(Items.TURTLE_SCUTE));
   public static final Holder<ArmorMaterial> NETHERITE = register("netherite", Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 3);
      var0.put(ArmorItem.Type.LEGGINGS, 6);
      var0.put(ArmorItem.Type.CHESTPLATE, 8);
      var0.put(ArmorItem.Type.HELMET, 3);
      var0.put(ArmorItem.Type.BODY, 11);
   }), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> Ingredient.of(Items.NETHERITE_INGOT));
   public static final Holder<ArmorMaterial> ARMADILLO = register("armadillo", Util.make(new EnumMap<>(ArmorItem.Type.class), var0 -> {
      var0.put(ArmorItem.Type.BOOTS, 3);
      var0.put(ArmorItem.Type.LEGGINGS, 6);
      var0.put(ArmorItem.Type.CHESTPLATE, 8);
      var0.put(ArmorItem.Type.HELMET, 3);
      var0.put(ArmorItem.Type.BODY, 11);
   }), 10, SoundEvents.ARMOR_EQUIP_WOLF, 0.0F, 0.0F, () -> Ingredient.of(Items.ARMADILLO_SCUTE));

   public ArmorMaterials() {
      super();
   }

   public static Holder<ArmorMaterial> bootstrap(Registry<ArmorMaterial> var0) {
      return LEATHER;
   }

   private static Holder<ArmorMaterial> register(
      String var0, EnumMap<ArmorItem.Type, Integer> var1, int var2, Holder<SoundEvent> var3, float var4, float var5, Supplier<Ingredient> var6
   ) {
      List var7 = List.of(new ArmorMaterial.Layer(new ResourceLocation(var0)));
      return register(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   private static Holder<ArmorMaterial> register(
      String var0,
      EnumMap<ArmorItem.Type, Integer> var1,
      int var2,
      Holder<SoundEvent> var3,
      float var4,
      float var5,
      Supplier<Ingredient> var6,
      List<ArmorMaterial.Layer> var7
   ) {
      EnumMap var8 = new EnumMap<>(ArmorItem.Type.class);

      for (ArmorItem.Type var12 : ArmorItem.Type.values()) {
         var8.put(var12, (Integer)var1.get(var12));
      }

      return Registry.registerForHolder(
         BuiltInRegistries.ARMOR_MATERIAL, new ResourceLocation(var0), new ArmorMaterial(var8, var2, var3, var6, var7, var4, var5)
      );
   }
}
