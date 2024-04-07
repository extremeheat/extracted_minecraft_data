package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Ingredient;

public record ArmorMaterial(
   Map<ArmorItem.Type, Integer> defense,
   int enchantmentValue,
   Holder<SoundEvent> equipSound,
   Supplier<Ingredient> repairIngredient,
   List<ArmorMaterial.Layer> layers,
   float toughness,
   float knockbackResistance
) {
   public static final Codec<Holder<ArmorMaterial>> CODEC = BuiltInRegistries.ARMOR_MATERIAL.holderByNameCodec();

   public ArmorMaterial(
      Map<ArmorItem.Type, Integer> defense,
      int enchantmentValue,
      Holder<SoundEvent> equipSound,
      Supplier<Ingredient> repairIngredient,
      List<ArmorMaterial.Layer> layers,
      float toughness,
      float knockbackResistance
   ) {
      super();
      this.defense = defense;
      this.enchantmentValue = enchantmentValue;
      this.equipSound = equipSound;
      this.repairIngredient = repairIngredient;
      this.layers = layers;
      this.toughness = toughness;
      this.knockbackResistance = knockbackResistance;
   }

   public int getDefense(ArmorItem.Type var1) {
      return this.defense.getOrDefault(var1, 0);
   }

   public static final class Layer {
      private final ResourceLocation assetName;
      private final String suffix;
      private final boolean dyeable;
      private final ResourceLocation innerTexture;
      private final ResourceLocation outerTexture;

      public Layer(ResourceLocation var1, String var2, boolean var3) {
         super();
         this.assetName = var1;
         this.suffix = var2;
         this.dyeable = var3;
         this.innerTexture = this.resolveTexture(true);
         this.outerTexture = this.resolveTexture(false);
      }

      public Layer(ResourceLocation var1) {
         this(var1, "", false);
      }

      private ResourceLocation resolveTexture(boolean var1) {
         return this.assetName.withPath(var2 -> "textures/models/armor/" + this.assetName.getPath() + "_layer_" + (var1 ? 2 : 1) + this.suffix + ".png");
      }

      public ResourceLocation texture(boolean var1) {
         return var1 ? this.innerTexture : this.outerTexture;
      }

      public boolean dyeable() {
         return this.dyeable;
      }
   }
}
