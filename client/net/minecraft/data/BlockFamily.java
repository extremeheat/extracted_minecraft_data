package net.minecraft.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Block;

public class BlockFamily {
   private final Block baseBlock;
   final Map<Variant, Block> variants = Maps.newHashMap();
   boolean generateModel = true;
   boolean generateRecipe = true;
   @Nullable
   String recipeGroupPrefix;
   @Nullable
   String recipeUnlockedBy;

   BlockFamily(Block var1) {
      super();
      this.baseBlock = var1;
   }

   public Block getBaseBlock() {
      return this.baseBlock;
   }

   public Map<Variant, Block> getVariants() {
      return this.variants;
   }

   public Block get(Variant var1) {
      return (Block)this.variants.get(var1);
   }

   public boolean shouldGenerateModel() {
      return this.generateModel;
   }

   public boolean shouldGenerateRecipe() {
      return this.generateRecipe;
   }

   public Optional<String> getRecipeGroupPrefix() {
      return StringUtil.isBlank(this.recipeGroupPrefix) ? Optional.empty() : Optional.of(this.recipeGroupPrefix);
   }

   public Optional<String> getRecipeUnlockedBy() {
      return StringUtil.isBlank(this.recipeUnlockedBy) ? Optional.empty() : Optional.of(this.recipeUnlockedBy);
   }

   public static class Builder {
      private final BlockFamily family;

      public Builder(Block var1) {
         super();
         this.family = new BlockFamily(var1);
      }

      public BlockFamily getFamily() {
         return this.family;
      }

      public Builder button(Block var1) {
         this.family.variants.put(BlockFamily.Variant.BUTTON, var1);
         return this;
      }

      public Builder chiseled(Block var1) {
         this.family.variants.put(BlockFamily.Variant.CHISELED, var1);
         return this;
      }

      public Builder mosaic(Block var1) {
         this.family.variants.put(BlockFamily.Variant.MOSAIC, var1);
         return this;
      }

      public Builder cracked(Block var1) {
         this.family.variants.put(BlockFamily.Variant.CRACKED, var1);
         return this;
      }

      public Builder cut(Block var1) {
         this.family.variants.put(BlockFamily.Variant.CUT, var1);
         return this;
      }

      public Builder door(Block var1) {
         this.family.variants.put(BlockFamily.Variant.DOOR, var1);
         return this;
      }

      public Builder customFence(Block var1) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE, var1);
         return this;
      }

      public Builder fence(Block var1) {
         this.family.variants.put(BlockFamily.Variant.FENCE, var1);
         return this;
      }

      public Builder customFenceGate(Block var1) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, var1);
         return this;
      }

      public Builder fenceGate(Block var1) {
         this.family.variants.put(BlockFamily.Variant.FENCE_GATE, var1);
         return this;
      }

      public Builder sign(Block var1, Block var2) {
         this.family.variants.put(BlockFamily.Variant.SIGN, var1);
         this.family.variants.put(BlockFamily.Variant.WALL_SIGN, var2);
         return this;
      }

      public Builder slab(Block var1) {
         this.family.variants.put(BlockFamily.Variant.SLAB, var1);
         return this;
      }

      public Builder stairs(Block var1) {
         this.family.variants.put(BlockFamily.Variant.STAIRS, var1);
         return this;
      }

      public Builder pressurePlate(Block var1) {
         this.family.variants.put(BlockFamily.Variant.PRESSURE_PLATE, var1);
         return this;
      }

      public Builder polished(Block var1) {
         this.family.variants.put(BlockFamily.Variant.POLISHED, var1);
         return this;
      }

      public Builder trapdoor(Block var1) {
         this.family.variants.put(BlockFamily.Variant.TRAPDOOR, var1);
         return this;
      }

      public Builder wall(Block var1) {
         this.family.variants.put(BlockFamily.Variant.WALL, var1);
         return this;
      }

      public Builder dontGenerateModel() {
         this.family.generateModel = false;
         return this;
      }

      public Builder dontGenerateRecipe() {
         this.family.generateRecipe = false;
         return this;
      }

      public Builder recipeGroupPrefix(String var1) {
         this.family.recipeGroupPrefix = var1;
         return this;
      }

      public Builder recipeUnlockedBy(String var1) {
         this.family.recipeUnlockedBy = var1;
         return this;
      }
   }

   public static enum Variant {
      BUTTON("button"),
      CHISELED("chiseled"),
      CRACKED("cracked"),
      CUT("cut"),
      DOOR("door"),
      CUSTOM_FENCE("fence"),
      FENCE("fence"),
      CUSTOM_FENCE_GATE("fence_gate"),
      FENCE_GATE("fence_gate"),
      MOSAIC("mosaic"),
      SIGN("sign"),
      SLAB("slab"),
      STAIRS("stairs"),
      PRESSURE_PLATE("pressure_plate"),
      POLISHED("polished"),
      TRAPDOOR("trapdoor"),
      WALL("wall"),
      WALL_SIGN("wall_sign");

      private final String recipeGroup;

      private Variant(final String var3) {
         this.recipeGroup = var3;
      }

      public String getRecipeGroup() {
         return this.recipeGroup;
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{BUTTON, CHISELED, CRACKED, CUT, DOOR, CUSTOM_FENCE, FENCE, CUSTOM_FENCE_GATE, FENCE_GATE, MOSAIC, SIGN, SLAB, STAIRS, PRESSURE_PLATE, POLISHED, TRAPDOOR, WALL, WALL_SIGN};
      }
   }
}
