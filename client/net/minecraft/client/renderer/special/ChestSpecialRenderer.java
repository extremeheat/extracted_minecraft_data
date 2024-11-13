package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class ChestSpecialRenderer implements NoDataSpecialModelRenderer {
   public static final ResourceLocation GIFT_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("christmas");
   public static final ResourceLocation NORMAL_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("normal");
   public static final ResourceLocation TRAPPED_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("trapped");
   public static final ResourceLocation ENDER_CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("ender");
   private final ChestModel model;
   private final Material material;
   private final float openness;

   public ChestSpecialRenderer(ChestModel var1, Material var2, float var3) {
      super();
      this.model = var1;
      this.material = var2;
      this.openness = var3;
   }

   public void render(ItemDisplayContext var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, boolean var6) {
      VertexConsumer var7 = this.material.buffer(var3, RenderType::entitySolid);
      this.model.setupAnim(this.openness);
      this.model.renderToBuffer(var2, var7, var4, var5);
   }

   public static record Unbaked(ResourceLocation texture, float openness) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture), Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness)).apply(var0, Unbaked::new));

      public Unbaked(ResourceLocation var1) {
         this(var1, 0.0F);
      }

      public Unbaked(ResourceLocation var1, float var2) {
         super();
         this.texture = var1;
         this.openness = var2;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         ChestModel var2 = new ChestModel(var1.bakeLayer(ModelLayers.CHEST));
         Material var3 = Sheets.chestMaterial(this.texture);
         return new ChestSpecialRenderer(var2, var3, this.openness);
      }
   }
}
