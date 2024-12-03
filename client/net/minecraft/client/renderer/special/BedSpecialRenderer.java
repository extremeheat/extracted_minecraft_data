package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;

public class BedSpecialRenderer implements NoDataSpecialModelRenderer {
   private final BedRenderer bedRenderer;
   private final Material material;

   public BedSpecialRenderer(BedRenderer var1, Material var2) {
      super();
      this.bedRenderer = var1;
      this.material = var2;
   }

   public void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6) {
      this.bedRenderer.renderInHand(var2, var3, var4, var5, this.material);
   }

   public static record Unbaked(ResourceLocation texture) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture)).apply(var0, Unbaked::new));

      public Unbaked(DyeColor var1) {
         this(Sheets.colorToResourceMaterial(var1));
      }

      public Unbaked(ResourceLocation var1) {
         super();
         this.texture = var1;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         return new BedSpecialRenderer(new BedRenderer(var1), Sheets.createBedMaterial(this.texture));
      }
   }
}
