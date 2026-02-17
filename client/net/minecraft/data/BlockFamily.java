package net.minecraft.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class BlockFamily {
   private final Block baseBlock;
   private final Map<Variant, Block> variants = Maps.newHashMap();
   private boolean generateModel = true;
   private boolean generateCraftingRecipe = true;
   private boolean generateStonecutterRecipe = false;
   private @Nullable String recipeGroupPrefix;
   private @Nullable String recipeUnlockedBy;

   private BlockFamily(final Block baseBlock) {
      super();
      this.baseBlock = baseBlock;
   }

   public Block getBaseBlock() {
      return this.baseBlock;
   }

   public Map<Variant, Block> getVariants() {
      return this.variants;
   }

   public Block get(final Variant variant) {
      return (Block)this.variants.get(variant);
   }

   public boolean shouldGenerateModel() {
      return this.generateModel;
   }

   public boolean shouldGenerateCraftingRecipe() {
      return this.generateCraftingRecipe;
   }

   public boolean shouldGenerateStonecutterRecipe() {
      return this.generateStonecutterRecipe;
   }

   public Optional<String> getRecipeGroupPrefix() {
      return StringUtil.isBlank(this.recipeGroupPrefix) ? Optional.empty() : Optional.of(this.recipeGroupPrefix);
   }

   public Optional<String> getRecipeUnlockedBy() {
      return StringUtil.isBlank(this.recipeUnlockedBy) ? Optional.empty() : Optional.of(this.recipeUnlockedBy);
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
      WALL_SIGN("wall_sign"),
      BRICKS("bricks"),
      COBBLED("cobbled"),
      TILES("tiles");

      private final String recipeGroup;

      private Variant(final String recipeGroup) {
         this.recipeGroup = recipeGroup;
      }

      public String getRecipeGroup() {
         return this.recipeGroup;
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{BUTTON, CHISELED, CRACKED, CUT, DOOR, CUSTOM_FENCE, FENCE, CUSTOM_FENCE_GATE, FENCE_GATE, MOSAIC, SIGN, SLAB, STAIRS, PRESSURE_PLATE, POLISHED, TRAPDOOR, WALL, WALL_SIGN, BRICKS, COBBLED, TILES};
      }
   }

   public static class Builder {
      private final BlockFamily family;

      public Builder(final Block baseBlock) {
         super();
         this.family = new BlockFamily(baseBlock);
      }

      public BlockFamily getFamily() {
         return this.family;
      }

      public Builder button(final Block button) {
         this.family.variants.put(BlockFamily.Variant.BUTTON, button);
         return this;
      }

      public Builder chiseled(final Block chiseled) {
         this.family.variants.put(BlockFamily.Variant.CHISELED, chiseled);
         return this;
      }

      public Builder mosaic(final Block mosaic) {
         this.family.variants.put(BlockFamily.Variant.MOSAIC, mosaic);
         return this;
      }

      public Builder cracked(final Block cracked) {
         this.family.variants.put(BlockFamily.Variant.CRACKED, cracked);
         return this;
      }

      public Builder tiles(final Block tiles) {
         this.family.variants.put(BlockFamily.Variant.TILES, tiles);
         return this;
      }

      public Builder cut(final Block cut) {
         this.family.variants.put(BlockFamily.Variant.CUT, cut);
         return this;
      }

      public Builder door(final Block door) {
         this.family.variants.put(BlockFamily.Variant.DOOR, door);
         return this;
      }

      public Builder customFence(final Block fence) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE, fence);
         return this;
      }

      public Builder fence(final Block fence) {
         this.family.variants.put(BlockFamily.Variant.FENCE, fence);
         return this;
      }

      public Builder customFenceGate(final Block fenceGate) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, fenceGate);
         return this;
      }

      public Builder fenceGate(final Block fenceGate) {
         this.family.variants.put(BlockFamily.Variant.FENCE_GATE, fenceGate);
         return this;
      }

      public Builder sign(final Block sign, final Block wallSign) {
         this.family.variants.put(BlockFamily.Variant.SIGN, sign);
         this.family.variants.put(BlockFamily.Variant.WALL_SIGN, wallSign);
         return this;
      }

      public Builder slab(final Block slab) {
         this.family.variants.put(BlockFamily.Variant.SLAB, slab);
         return this;
      }

      public Builder stairs(final Block stairs) {
         this.family.variants.put(BlockFamily.Variant.STAIRS, stairs);
         return this;
      }

      public Builder pressurePlate(final Block pressurePlate) {
         this.family.variants.put(BlockFamily.Variant.PRESSURE_PLATE, pressurePlate);
         return this;
      }

      public Builder polished(final Block polished) {
         this.family.variants.put(BlockFamily.Variant.POLISHED, polished);
         return this;
      }

      public Builder trapdoor(final Block trapdoor) {
         this.family.variants.put(BlockFamily.Variant.TRAPDOOR, trapdoor);
         return this;
      }

      public Builder wall(final Block wall) {
         this.family.variants.put(BlockFamily.Variant.WALL, wall);
         return this;
      }

      public Builder cobbled(final Block cobble) {
         this.family.variants.put(BlockFamily.Variant.COBBLED, cobble);
         return this;
      }

      public Builder bricks(final Block bricks) {
         this.family.variants.put(BlockFamily.Variant.BRICKS, bricks);
         return this;
      }

      public Builder dontGenerateModel() {
         this.family.generateModel = false;
         return this;
      }

      public Builder dontGenerateCraftingRecipe() {
         this.family.generateCraftingRecipe = false;
         return this;
      }

      public Builder generateStonecutterRecipe() {
         this.family.generateStonecutterRecipe = true;
         return this;
      }

      public Builder recipeGroupPrefix(final String recipeGroupPrefix) {
         this.family.recipeGroupPrefix = recipeGroupPrefix;
         return this;
      }

      public Builder recipeUnlockedBy(final String recipeUnlockedBy) {
         this.family.recipeUnlockedBy = recipeUnlockedBy;
         return this;
      }
   }
}
