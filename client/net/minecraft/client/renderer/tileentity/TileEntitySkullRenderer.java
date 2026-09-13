package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer {
   private static final ResourceLocation field_147537_c = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation field_147534_d = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
   private static final ResourceLocation field_147535_e = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation field_147532_f = new ResourceLocation("textures/entity/creeper/creeper.png");
   public static TileEntitySkullRenderer field_147536_b;
   private ModelSkeletonHead field_147533_g = new ModelSkeletonHead(0, 0, 64, 32);
   private ModelSkeletonHead field_147538_h = new ModelSkeletonHead(0, 0, 64, 64);

   public TileEntitySkullRenderer() {
      super();
   }

   public void func_147500_a(TileEntitySkull var1, double var2, double var4, double var6, float var8) {
      this.func_152674_a(
         (float)var2,
         (float)var4,
         (float)var6,
         var1.func_145832_p() & 7,
         (float)(var1.func_145906_b() * 360) / 16.0F,
         var1.func_145904_a(),
         var1.func_152108_a()
      );
   }

   @Override
   public void func_147497_a(TileEntityRendererDispatcher var1) {
      super.func_147497_a(var1);
      field_147536_b = this;
   }

   public void func_152674_a(float var1, float var2, float var3, int var4, float var5, int var6, GameProfile var7) {
      ModelSkeletonHead var8 = this.field_147533_g;
      switch(var6) {
         case 0:
         default:
            this.func_147499_a(field_147537_c);
            break;
         case 1:
            this.func_147499_a(field_147534_d);
            break;
         case 2:
            this.func_147499_a(field_147535_e);
            var8 = this.field_147538_h;
            break;
         case 3:
            ResourceLocation var9 = AbstractClientPlayer.field_110314_b;
            if (var7 != null) {
               Minecraft var10 = Minecraft.func_71410_x();
               Map var11 = var10.func_152342_ad().func_152788_a(var7);
               if (var11.containsKey(Type.SKIN)) {
                  var9 = var10.func_152342_ad().func_152792_a((MinecraftProfileTexture)var11.get(Type.SKIN), Type.SKIN);
               }
            }

            this.func_147499_a(var9);
            break;
         case 4:
            this.func_147499_a(field_147532_f);
      }

      GL11.glPushMatrix();
      GL11.glDisable(2884);
      if (var4 != 1) {
         switch(var4) {
            case 2:
               GL11.glTranslatef(var1 + 0.5F, var2 + 0.25F, var3 + 0.74F);
               break;
            case 3:
               GL11.glTranslatef(var1 + 0.5F, var2 + 0.25F, var3 + 0.26F);
               var5 = 180.0F;
               break;
            case 4:
               GL11.glTranslatef(var1 + 0.74F, var2 + 0.25F, var3 + 0.5F);
               var5 = 270.0F;
               break;
            case 5:
            default:
               GL11.glTranslatef(var1 + 0.26F, var2 + 0.25F, var3 + 0.5F);
               var5 = 90.0F;
         }
      } else {
         GL11.glTranslatef(var1 + 0.5F, var2, var3 + 0.5F);
      }

      float var12 = 0.0625F;
      GL11.glEnable(32826);
      GL11.glScalef(-1.0F, -1.0F, 1.0F);
      GL11.glEnable(3008);
      var8.func_78088_a(null, 0.0F, 0.0F, 0.0F, var5, 0.0F, var12);
      GL11.glPopMatrix();
   }
}
