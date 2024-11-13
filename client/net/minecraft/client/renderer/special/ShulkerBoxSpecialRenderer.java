package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;

public class ShulkerBoxSpecialRenderer implements NoDataSpecialModelRenderer {
   private final ShulkerBoxRenderer shulkerBoxRenderer;
   private final float openness;
   private final Direction orientation;
   private final Material material;

   public ShulkerBoxSpecialRenderer(ShulkerBoxRenderer var1, float var2, Direction var3, Material var4) {
      super();
      this.shulkerBoxRenderer = var1;
      this.openness = var2;
      this.orientation = var3;
      this.material = var4;
   }

   public void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6) {
      this.shulkerBoxRenderer.render(var2, var3, var4, var5, this.orientation, this.openness, this.material);
   }

   public static record Unbaked(ResourceLocation texture, float openness, Direction orientation) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture), Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness), Direction.CODEC.optionalFieldOf("orientation", Direction.UP).forGetter(Unbaked::orientation)).apply(var0, Unbaked::new));

      public Unbaked() {
         this(ResourceLocation.withDefaultNamespace("shulker"), 0.0F, Direction.UP);
      }

      public Unbaked(DyeColor var1) {
         this(Sheets.colorToShulkerMaterial(var1), 0.0F, Direction.UP);
      }

      public Unbaked(ResourceLocation var1, float var2, Direction var3) {
         super();
         this.texture = var1;
         this.openness = var2;
         this.orientation = var3;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         return new ShulkerBoxSpecialRenderer(new ShulkerBoxRenderer(var1), this.openness, this.orientation, Sheets.createShulkerMaterial(this.texture));
      }
   }
}
