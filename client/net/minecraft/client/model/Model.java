package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import org.joml.Vector3f;

public abstract class Model {
   private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
   protected final ModelPart root;
   protected final Function<ResourceLocation, RenderType> renderType;
   private final List<ModelPart> allParts;

   public Model(ModelPart var1, Function<ResourceLocation, RenderType> var2) {
      super();
      this.root = var1;
      this.renderType = var2;
      this.allParts = var1.getAllParts().toList();
   }

   public final RenderType renderType(ResourceLocation var1) {
      return this.renderType.apply(var1);
   }

   public final void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      this.root().render(var1, var2, var3, var4, var5);
   }

   public final void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4) {
      this.renderToBuffer(var1, var2, var3, var4, -1);
   }

   public final ModelPart root() {
      return this.root;
   }

   public Optional<ModelPart> getAnyDescendantWithName(String var1) {
      return var1.equals("root")
         ? Optional.of(this.root())
         : this.root().getAllParts().filter(var1x -> var1x.hasChild(var1)).findFirst().map(var1x -> var1x.getChild(var1));
   }

   public final List<ModelPart> allParts() {
      return this.allParts;
   }

   public final void resetPose() {
      for (ModelPart var2 : this.allParts) {
         var2.resetPose();
      }
   }

   protected void animate(AnimationState var1, AnimationDefinition var2, float var3) {
      this.animate(var1, var2, var3, 1.0F);
   }

   protected void animateWalk(AnimationDefinition var1, float var2, float var3, float var4, float var5) {
      long var6 = (long)(var2 * 50.0F * var4);
      float var8 = Math.min(var3 * var5, 1.0F);
      KeyframeAnimations.animate(this, var1, var6, var8, ANIMATION_VECTOR_CACHE);
   }

   protected void animate(AnimationState var1, AnimationDefinition var2, float var3, float var4) {
      var1.ifStarted(var4x -> KeyframeAnimations.animate(this, var2, (long)((float)var4x.getTimeInMillis(var3) * var4), 1.0F, ANIMATION_VECTOR_CACHE));
   }

   protected void applyStatic(AnimationDefinition var1) {
      KeyframeAnimations.animate(this, var1, 0L, 1.0F, ANIMATION_VECTOR_CACHE);
   }

   public static class Simple extends Model {
      public Simple(ModelPart var1, Function<ResourceLocation, RenderType> var2) {
         super(var1, var2);
      }
   }
}
