package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;

public abstract class EntityModel<T extends Entity> extends Model {
   public float attackTime;
   public boolean riding;
   public boolean young = true;

   public EntityModel() {
      super();
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
   }

   public void copyPropertiesTo(EntityModel<T> var1) {
      var1.attackTime = this.attackTime;
      var1.riding = this.riding;
      var1.young = this.young;
   }
}
