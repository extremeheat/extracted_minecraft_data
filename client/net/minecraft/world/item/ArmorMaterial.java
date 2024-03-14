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

public record ArmorMaterial(Map<ArmorItem.Type, Integer> b, int c, Holder<SoundEvent> d, Supplier<Ingredient> e, List<ArmorMaterial.Layer> f, float g, float h) {
   private final Map<ArmorItem.Type, Integer> defense;
   private final int enchantmentValue;
   private final Holder<SoundEvent> equipSound;
   private final Supplier<Ingredient> repairIngredient;
   private final List<ArmorMaterial.Layer> layers;
   private final float toughness;
   private final float knockbackResistance;
   public static final Codec<Holder<ArmorMaterial>> CODEC = BuiltInRegistries.ARMOR_MATERIAL.holderByNameCodec();

   public ArmorMaterial(
      Map<ArmorItem.Type, Integer> var1, int var2, Holder<SoundEvent> var3, Supplier<Ingredient> var4, List<ArmorMaterial.Layer> var5, float var6, float var7
   ) {
      super();
      this.defense = var1;
      this.enchantmentValue = var2;
      this.equipSound = var3;
      this.repairIngredient = var4;
      this.layers = var5;
      this.toughness = var6;
      this.knockbackResistance = var7;
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
