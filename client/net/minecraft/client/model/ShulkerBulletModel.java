package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class ShulkerBulletModel<T extends Entity> extends ListModel<T> {
   private final ModelPart main;

   public ShulkerBulletModel() {
      super();
      this.texWidth = 64;
      this.texHeight = 32;
      this.main = new ModelPart(this);
      this.main.texOffs(0, 0).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F, 0.0F);
      this.main.texOffs(0, 10).addBox(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F, 0.0F);
      this.main.texOffs(20, 0).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, 0.0F);
      this.main.setPos(0.0F, 0.0F, 0.0F);
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.main);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.main.yRot = var5 * 0.017453292F;
      this.main.xRot = var6 * 0.017453292F;
   }
}
