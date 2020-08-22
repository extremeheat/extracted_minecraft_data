package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class LlamaSpitModel extends ListModel {
   private final ModelPart main;

   public LlamaSpitModel() {
      this(0.0F);
   }

   public LlamaSpitModel(float var1) {
      this.main = new ModelPart(this);
      boolean var2 = true;
      this.main.texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, var1);
      this.main.texOffs(0, 0).addBox(0.0F, -4.0F, 0.0F, 2.0F, 2.0F, 2.0F, var1);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, -4.0F, 2.0F, 2.0F, 2.0F, var1);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, var1);
      this.main.texOffs(0, 0).addBox(2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, var1);
      this.main.texOffs(0, 0).addBox(0.0F, 2.0F, 0.0F, 2.0F, 2.0F, 2.0F, var1);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 2.0F, var1);
      this.main.setPos(0.0F, 0.0F, 0.0F);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public Iterable parts() {
      return ImmutableList.of(this.main);
   }
}
