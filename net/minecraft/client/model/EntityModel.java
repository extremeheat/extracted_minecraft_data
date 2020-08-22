package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;

public abstract class EntityModel extends Model {
   public float attackTime;
   public boolean riding;
   public boolean young;

   protected EntityModel() {
      this(RenderType::entityCutoutNoCull);
   }

   protected EntityModel(Function var1) {
      super(var1);
      this.young = true;
   }

   public abstract void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6);

   public void prepareMobModel(Entity var1, float var2, float var3, float var4) {
   }

   public void copyPropertiesTo(EntityModel var1) {
      var1.attackTime = this.attackTime;
      var1.riding = this.riding;
      var1.young = this.young;
   }
}
