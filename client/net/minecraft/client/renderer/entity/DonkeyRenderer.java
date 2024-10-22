package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.DonkeyModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class DonkeyRenderer<T extends AbstractChestedHorse> extends AbstractHorseRenderer<T, DonkeyRenderState, DonkeyModel> {
   public static final ResourceLocation DONKEY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/donkey.png");
   public static final ResourceLocation MULE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/horse/mule.png");
   private final ResourceLocation texture;

   public DonkeyRenderer(EntityRendererProvider.Context var1, float var2, ModelLayerLocation var3, ModelLayerLocation var4, boolean var5) {
      super(var1, new DonkeyModel(var1.bakeLayer(var3)), new DonkeyModel(var1.bakeLayer(var4)), var2);
      this.texture = var5 ? MULE_TEXTURE : DONKEY_TEXTURE;
   }

   public ResourceLocation getTextureLocation(DonkeyRenderState var1) {
      return this.texture;
   }

   public DonkeyRenderState createRenderState() {
      return new DonkeyRenderState();
   }

   public void extractRenderState(T var1, DonkeyRenderState var2, float var3) {
      super.extractRenderState((T)var1, var2, var3);
      var2.hasChest = var1.hasChest();
   }
}
