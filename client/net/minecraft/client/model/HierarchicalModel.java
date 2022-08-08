package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;

public abstract class HierarchicalModel<E extends Entity> extends EntityModel<E> {
   private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

   public HierarchicalModel() {
      this(RenderType::entityCutoutNoCull);
   }

   public HierarchicalModel(Function<ResourceLocation, RenderType> var1) {
      super(var1);
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.root().render(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public abstract ModelPart root();

   public Optional<ModelPart> getAnyDescendantWithName(String var1) {
      return this.root().getAllParts().filter((var1x) -> {
         return var1x.hasChild(var1);
      }).findFirst().map((var1x) -> {
         return var1x.getChild(var1);
      });
   }

   protected void animate(AnimationState var1, AnimationDefinition var2, float var3) {
      this.animate(var1, var2, var3, 1.0F);
   }

   protected void animate(AnimationState var1, AnimationDefinition var2, float var3, float var4) {
      var1.updateTime(var3, var4);
      var1.ifStarted((var2x) -> {
         KeyframeAnimations.animate(this, var2, var2x.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE);
      });
   }
}
