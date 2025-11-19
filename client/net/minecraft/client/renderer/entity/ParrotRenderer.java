package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.parrot.Parrot;

public class ParrotRenderer extends MobRenderer<Parrot, ParrotRenderState, ParrotModel> {
   private static final Identifier RED_BLUE = Identifier.withDefaultNamespace("textures/entity/parrot/parrot_red_blue.png");
   private static final Identifier BLUE = Identifier.withDefaultNamespace("textures/entity/parrot/parrot_blue.png");
   private static final Identifier GREEN = Identifier.withDefaultNamespace("textures/entity/parrot/parrot_green.png");
   private static final Identifier YELLOW_BLUE = Identifier.withDefaultNamespace("textures/entity/parrot/parrot_yellow_blue.png");
   private static final Identifier GREY = Identifier.withDefaultNamespace("textures/entity/parrot/parrot_grey.png");

   public ParrotRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ParrotModel(var1.bakeLayer(ModelLayers.PARROT)), 0.3F);
   }

   public Identifier getTextureLocation(ParrotRenderState var1) {
      return getVariantTexture(var1.variant);
   }

   public ParrotRenderState createRenderState() {
      return new ParrotRenderState();
   }

   public void extractRenderState(Parrot var1, ParrotRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = var1.getVariant();
      float var4 = Mth.lerp(var3, var1.oFlap, var1.flap);
      float var5 = Mth.lerp(var3, var1.oFlapSpeed, var1.flapSpeed);
      var2.flapAngle = (Mth.sin((double)var4) + 1.0F) * var5;
      var2.pose = ParrotModel.getPose(var1);
   }

   public static Identifier getVariantTexture(Parrot.Variant var0) {
      Identifier var10000;
      switch (var0) {
         case RED_BLUE -> var10000 = RED_BLUE;
         case BLUE -> var10000 = BLUE;
         case GREEN -> var10000 = GREEN;
         case YELLOW_BLUE -> var10000 = YELLOW_BLUE;
         case GRAY -> var10000 = GREY;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ParrotRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
