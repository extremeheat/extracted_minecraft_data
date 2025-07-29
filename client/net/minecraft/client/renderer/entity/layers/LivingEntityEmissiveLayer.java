package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class LivingEntityEmissiveLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private final Function<S, ResourceLocation> textureProvider;
   private final AlphaFunction<S> alphaFunction;
   private final M model;
   private final Function<ResourceLocation, RenderType> bufferProvider;
   private final boolean alwaysVisible;

   public LivingEntityEmissiveLayer(RenderLayerParent<S, M> var1, Function<S, ResourceLocation> var2, AlphaFunction<S> var3, M var4, Function<ResourceLocation, RenderType> var5, boolean var6) {
      super(var1);
      this.textureProvider = var2;
      this.alphaFunction = var3;
      this.model = var4;
      this.bufferProvider = var5;
      this.alwaysVisible = var6;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      if (!var4.isInvisible || this.alwaysVisible) {
         float var7 = this.alphaFunction.apply(var4, var4.ageInTicks);
         if (!(var7 <= 1.0E-5F)) {
            int var8 = ARGB.white(var7);
            RenderType var9 = (RenderType)this.bufferProvider.apply((ResourceLocation)this.textureProvider.apply(var4));
            var2.submitModel(this.model, var4, var1, var9, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), var8, (TextureAtlasSprite)null, var4.outlineColor, 1);
         }
      }
   }

   public interface AlphaFunction<S extends LivingEntityRenderState> {
      float apply(S var1, float var2);
   }
}
