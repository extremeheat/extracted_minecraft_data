package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

public class RenderHorse extends RenderLiving<EntityHorse> {
   private static final Map<String, ResourceLocation> field_110852_a = Maps.newHashMap();
   private static final ResourceLocation field_110850_f = new ResourceLocation("textures/entity/horse/horse_white.png");
   private static final ResourceLocation field_110851_g = new ResourceLocation("textures/entity/horse/mule.png");
   private static final ResourceLocation field_110855_h = new ResourceLocation("textures/entity/horse/donkey.png");
   private static final ResourceLocation field_110854_k = new ResourceLocation("textures/entity/horse/horse_zombie.png");
   private static final ResourceLocation field_110853_l = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

   public RenderHorse(RenderManager var1, ModelHorse var2, float var3) {
      super(var1, var2, var3);
   }

   protected void func_77041_b(EntityHorse var1, float var2) {
      float var3 = 1.0F;
      int var4 = var1.func_110265_bP();
      if (var4 == 1) {
         var3 *= 0.87F;
      } else if (var4 == 2) {
         var3 *= 0.92F;
      }

      GlStateManager.func_179152_a(var3, var3, var3);
      super.func_77041_b(var1, var2);
   }

   protected ResourceLocation func_110775_a(EntityHorse var1) {
      if (!var1.func_110239_cn()) {
         switch(var1.func_110265_bP()) {
         case 0:
         default:
            return field_110850_f;
         case 1:
            return field_110855_h;
         case 2:
            return field_110851_g;
         case 3:
            return field_110854_k;
         case 4:
            return field_110853_l;
         }
      } else {
         return this.func_110848_b(var1);
      }
   }

   private ResourceLocation func_110848_b(EntityHorse var1) {
      String var2 = var1.func_110264_co();
      if (!var1.func_175507_cI()) {
         return null;
      } else {
         ResourceLocation var3 = (ResourceLocation)field_110852_a.get(var2);
         if (var3 == null) {
            var3 = new ResourceLocation(var2);
            Minecraft.func_71410_x().func_110434_K().func_110579_a(var3, new LayeredTexture(var1.func_110212_cp()));
            field_110852_a.put(var2, var3);
         }

         return var3;
      }
   }
}
