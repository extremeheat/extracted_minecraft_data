package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie extends RenderBiped<EntityZombie> {
   private static final ResourceLocation field_110865_p = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation field_110864_q = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
   private final ModelBiped field_82434_o;
   private final ModelZombieVillager field_82432_p;
   private final List<LayerRenderer<EntityZombie>> field_177121_n;
   private final List<LayerRenderer<EntityZombie>> field_177122_o;

   public RenderZombie(RenderManager var1) {
      super(var1, new ModelZombie(), 0.5F, 1.0F);
      LayerRenderer var2 = (LayerRenderer)this.field_177097_h.get(0);
      this.field_82434_o = this.field_77071_a;
      this.field_82432_p = new ModelZombieVillager();
      this.func_177094_a(new LayerHeldItem(this));
      LayerBipedArmor var3 = new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelZombie(0.5F, true);
            this.field_177186_d = new ModelZombie(1.0F, true);
         }
      };
      this.func_177094_a(var3);
      this.field_177122_o = Lists.newArrayList(this.field_177097_h);
      if (var2 instanceof LayerCustomHead) {
         this.func_177089_b(var2);
         this.func_177094_a(new LayerCustomHead(this.field_82432_p.field_78116_c));
      }

      this.func_177089_b(var3);
      this.func_177094_a(new LayerVillagerArmor(this));
      this.field_177121_n = Lists.newArrayList(this.field_177097_h);
   }

   public void func_76986_a(EntityZombie var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_82427_a(var1);
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityZombie var1) {
      return var1.func_82231_m() ? field_110864_q : field_110865_p;
   }

   private void func_82427_a(EntityZombie var1) {
      if (var1.func_82231_m()) {
         this.field_77045_g = this.field_82432_p;
         this.field_177097_h = this.field_177121_n;
      } else {
         this.field_77045_g = this.field_82434_o;
         this.field_177097_h = this.field_177122_o;
      }

      this.field_77071_a = (ModelBiped)this.field_77045_g;
   }

   protected void func_77043_a(EntityZombie var1, float var2, float var3, float var4) {
      if (var1.func_82230_o()) {
         var3 += (float)(Math.cos((double)var1.field_70173_aa * 3.25D) * 3.141592653589793D * 0.25D);
      }

      super.func_77043_a(var1, var2, var3, var4);
   }
}
