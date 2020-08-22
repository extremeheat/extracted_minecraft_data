package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class Model implements Consumer {
   protected final Function renderType;
   public int texWidth = 64;
   public int texHeight = 32;

   public Model(Function var1) {
      this.renderType = var1;
   }

   public void accept(ModelPart var1) {
   }

   public final RenderType renderType(ResourceLocation var1) {
      return (RenderType)this.renderType.apply(var1);
   }

   public abstract void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8);

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((ModelPart)var1);
   }
}
