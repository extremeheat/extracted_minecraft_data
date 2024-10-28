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
   public static final Holder<ArmorMaterial> LEATHER;
   public static final Holder<ArmorMaterial> CHAIN;
   public static final Holder<ArmorMaterial> IRON;
   public static final Holder<ArmorMaterial> GOLD;
   public static final Holder<ArmorMaterial> DIAMOND;
   public static final Holder<ArmorMaterial> TURTLE;
   public static final Holder<ArmorMaterial> NETHERITE;
   public static final Holder<ArmorMaterial> ARMADILLO;

   public ArmorMaterials() {
      super();
   }

   public static Holder<ArmorMaterial> bootstrap(Registry<ArmorMaterial> var0) {
      return LEATHER;
   }

   private static Holder<ArmorMaterial> register(String var0, EnumMap<ArmorItem.Type, Integer> var1, int var2, Holder<SoundEvent> var3, float var4, float var5, Supplier<Ingredient> var6) {
      List var7 = List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace(var0)));
      return register(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   private static Holder<ArmorMaterial> register(String var0, EnumMap<ArmorItem.Type, Integer> var1, int var2, Holder<SoundEvent> var3, float var4, float var5, Supplier<Ingredient> var6, List<ArmorMaterial.Layer> var7) {
      EnumMap var8 = new EnumMap(ArmorItem.Type.class);
      ArmorItem.Type[] var9 = ArmorItem.Type.values();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         ArmorItem.Type var12 = var9[var11];
         var8.put(var12, (Integer)var1.get(var12));
      }

      return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), new ArmorMaterial(var8, var2, var3, var6, var7, var4, var5));
   }

   static {
      LEATHER = register("leather", (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (var0) -> {
         var0.put(ArmorItem.Type.BOOTS, 1);
         var0.put(ArmorItem.Type.LEGGINGS, 2);
         var0.put(ArmorItem.Type.CHESTPLATE, 3);
         var0.put(ArmorItem.Type.HELMET, 1);
         var0.put(ArmorItem.Type.BODY, 3);
      }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
         return Ingredient.of(Items.LEATHER);
      }, List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("leather"), "", true), new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("leather"), "_overlay", false)));
      CHAIN = register("chainmail", (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (var0) -> {
         var0.put(ArmorItem.Type.BOOTS, 1);
         var0.put(ArmorItem.Type.LEGGINGS, 4);
         var0.put(ArmorItem.Type.CHESTPLATE, 5);
         var0.put(ArmorItem.Type.HELMET, 2);
         var0.put(ArmorItem.Type.BODY, 4);
      }), 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> {
         return Ingredient.of(Items.IRON_INGOT);
      });
      IRON = register("iron", (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (var0) -> {
         var0.put(ArmorItem.Type.BOOTS, 2);
         var0.put(ArmorItem.Type.LEGGINGS, 5);
         var0.put(ArmorItem.Type.CHESTPLATE, 6);
         var0.put(ArmorItem.Type.HELMET, 2);
         var0.put(ArmorItem.Type.BODY, 5);
      }), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
         return Ingredient.of(Items.IRON_INGOT);
      });
      GOLD = register("gold", (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (var0) -> {
         var0.put(ArmorItem.Type.BOOTS, 1);
         var0.put(ArmorItem.Type.LEGGINGS, 3);
         var0.put(ArmorItem.Type.CHESTPLATE, 5);
         var0.put(ArmorItem.Type.HELMET, 2);
         var0.put(ArmorItem.Type.BODY, 7);
      }), 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
         return Ingredient.of(Items.GOLD_INGOT);
      });
      DIAMOND = register("diamond", (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (var0) -> {
         var0.put(ArmorItem.Type.BOOTS, 3);
         var0.put(ArmorItem.Type.LEGGINGS, 6);
         var0.put(ArmorItem.Type.CHESTPLATE, 8);
         var0.put(ArmorItem.Type.HELMET, 3);
         var0.put(ArmorItem.Type.BODY, 11);
      }), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
         return Ingredient.of(Items.DIAMOND);
      });
      TURTLE = register("turtle", (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (var0) -> {
         var0.put(ArmorItem.Type.BOOTS, 2);
         var0.put(ArmorItem.Type.LEGGINGS, 5);
         var0.put(ArmorItem.Type.CHESTPLATE, 6);
         var0.put(ArmorItem.Type.HELMET, 2);
         var0.put(ArmorItem.Type.BODY, 5);
      }), 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> {
         return Ingredient.of(Items.TURTLE_SCUTE);
      });
      NETHERITE = register("netherite", (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (var0) -> {
         var0.put(ArmorItem.Type.BOOTS, 3);
         var0.put(ArmorItem.Type.LEGGINGS, 6);
         var0.put(ArmorItem.Type.CHESTPLATE, 8);
         var0.put(ArmorItem.Type.HELMET, 3);
         var0.put(ArmorItem.Type.BODY, 11);
      }), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> {
         return Ingredient.of(Items.NETHERITE_INGOT);
      });
      ARMADILLO = register("armadillo", (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (var0) -> {
         var0.put(ArmorItem.Type.BOOTS, 3);
         var0.put(ArmorItem.Type.LEGGINGS, 6);
         var0.put(ArmorItem.Type.CHESTPLATE, 8);
         var0.put(ArmorItem.Type.HELMET, 3);
         var0.put(ArmorItem.Type.BODY, 11);
      }), 10, SoundEvents.ARMOR_EQUIP_WOLF, 0.0F, 0.0F, () -> {
         return Ingredient.of(Items.ARMADILLO_SCUTE);
      });
   }
}
