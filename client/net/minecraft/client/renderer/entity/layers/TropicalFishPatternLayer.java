package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishPatternLayer extends RenderLayer<TropicalFishRenderState, EntityModel<TropicalFishRenderState>> {
   private static final ResourceLocation KOB_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_1.png");
   private static final ResourceLocation SUNSTREAK_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_2.png");
   private static final ResourceLocation SNOOPER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_3.png");
   private static final ResourceLocation DASHER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_4.png");
   private static final ResourceLocation BRINELY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_5.png");
   private static final ResourceLocation SPOTTY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a_pattern_6.png");
   private static final ResourceLocation FLOPPER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_1.png");
   private static final ResourceLocation STRIPEY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_2.png");
   private static final ResourceLocation GLITTER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_3.png");
   private static final ResourceLocation BLOCKFISH_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_4.png");
   private static final ResourceLocation BETTY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_5.png");
   private static final ResourceLocation CLAYFISH_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b_pattern_6.png");
   private final TropicalFishModelA modelA;
   private final TropicalFishModelB modelB;

   public TropicalFishPatternLayer(RenderLayerParent<TropicalFishRenderState, EntityModel<TropicalFishRenderState>> var1, EntityModelSet var2) {
      super(var1);
      this.modelA = new TropicalFishModelA(var2.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL_PATTERN));
      this.modelB = new TropicalFishModelB(var2.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE_PATTERN));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, TropicalFishRenderState var4, float var5, float var6) {
      TropicalFish.Pattern var7 = var4.variant;
      Object var10000;
      switch (var7.base()) {
         case SMALL -> var10000 = this.modelA;
         case LARGE -> var10000 = this.modelB;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      Object var8 = var10000;
      ResourceLocation var10;
      switch (var7) {
         case KOB -> var10 = KOB_TEXTURE;
         case SUNSTREAK -> var10 = SUNSTREAK_TEXTURE;
         case SNOOPER -> var10 = SNOOPER_TEXTURE;
         case DASHER -> var10 = DASHER_TEXTURE;
         case BRINELY -> var10 = BRINELY_TEXTURE;
         case SPOTTY -> var10 = SPOTTY_TEXTURE;
         case FLOPPER -> var10 = FLOPPER_TEXTURE;
         case STRIPEY -> var10 = STRIPEY_TEXTURE;
         case GLITTER -> var10 = GLITTER_TEXTURE;
         case BLOCKFISH -> var10 = BLOCKFISH_TEXTURE;
         case BETTY -> var10 = BETTY_TEXTURE;
         case CLAYFISH -> var10 = CLAYFISH_TEXTURE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      ResourceLocation var9 = var10;
      coloredCutoutModelCopyLayerRender((EntityModel)var8, var9, var1, var2, var3, var4, var4.patternColor);
   }
}
