package net.minecraft.client.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;

public record BabyModelTransform(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset, Set<String> headParts) implements MeshTransformer {
   public BabyModelTransform(Set<String> var1) {
      this(false, 5.0F, 2.0F, var1);
   }

   public BabyModelTransform(boolean var1, float var2, float var3, Set<String> var4) {
      this(var1, var2, var3, 2.0F, 2.0F, 24.0F, var4);
   }

   public BabyModelTransform(boolean var1, float var2, float var3, float var4, float var5, float var6, Set<String> var7) {
      super();
      this.scaleHead = var1;
      this.babyYHeadOffset = var2;
      this.babyZHeadOffset = var3;
      this.babyHeadScale = var4;
      this.babyBodyScale = var5;
      this.bodyYOffset = var6;
      this.headParts = var7;
   }

   public MeshDefinition apply(MeshDefinition var1) {
      float var2 = this.scaleHead ? 1.5F / this.babyHeadScale : 1.0F;
      float var3 = 1.0F / this.babyBodyScale;
      UnaryOperator var4 = (var2x) -> {
         return var2x.translated(0.0F, this.babyYHeadOffset, this.babyZHeadOffset).scaled(var2);
      };
      UnaryOperator var5 = (var2x) -> {
         return var2x.translated(0.0F, this.bodyYOffset, 0.0F).scaled(var3);
      };
      MeshDefinition var6 = new MeshDefinition();
      Iterator var7 = var1.getRoot().getChildren().iterator();

      while(var7.hasNext()) {
         Map.Entry var8 = (Map.Entry)var7.next();
         String var9 = (String)var8.getKey();
         PartDefinition var10 = (PartDefinition)var8.getValue();
         var6.getRoot().addOrReplaceChild(var9, var10.transformed(this.headParts.contains(var9) ? var4 : var5));
      }

      return var6;
   }

   public boolean scaleHead() {
      return this.scaleHead;
   }

   public float babyYHeadOffset() {
      return this.babyYHeadOffset;
   }

   public float babyZHeadOffset() {
      return this.babyZHeadOffset;
   }

   public float babyHeadScale() {
      return this.babyHeadScale;
   }

   public float babyBodyScale() {
      return this.babyBodyScale;
   }

   public float bodyYOffset() {
      return this.bodyYOffset;
   }

   public Set<String> headParts() {
      return this.headParts;
   }
}
