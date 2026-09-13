package net.minecraft.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public abstract class ModelBase {
   public float field_78095_p;
   public boolean field_78093_q;
   public List field_78092_r = new ArrayList();
   public boolean field_78091_s = true;
   private Map field_78094_a = new HashMap();
   public int field_78090_t = 64;
   public int field_78089_u = 32;

   public ModelBase() {
      super();
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
   }

   public ModelRenderer func_85181_a(Random var1) {
      return (ModelRenderer)this.field_78092_r.get(var1.nextInt(this.field_78092_r.size()));
   }

   protected void func_78085_a(String var1, int var2, int var3) {
      this.field_78094_a.put(var1, new TextureOffset(var2, var3));
   }

   public TextureOffset func_78084_a(String var1) {
      return (TextureOffset)this.field_78094_a.get(var1);
   }
}
