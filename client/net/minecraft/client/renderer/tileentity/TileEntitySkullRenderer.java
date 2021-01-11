package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull> {
   private static final ResourceLocation field_147537_c = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation field_147534_d = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
   private static final ResourceLocation field_147535_e = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation field_147532_f = new ResourceLocation("textures/entity/creeper/creeper.png");
   public static TileEntitySkullRenderer field_147536_b;
   private final ModelSkeletonHead field_178467_h = new ModelSkeletonHead(0, 0, 64, 32);
   private final ModelSkeletonHead field_178468_i = new ModelHumanoidHead();

   public TileEntitySkullRenderer() {
      super();
   }

   public void func_180535_a(TileEntitySkull var1, double var2, double var4, double var6, float var8, int var9) {
      EnumFacing var10 = EnumFacing.func_82600_a(var1.func_145832_p() & 7);
      this.func_180543_a((float)var2, (float)var4, (float)var6, var10, (float)(var1.func_145906_b() * 360) / 16.0F, var1.func_145904_a(), var1.func_152108_a(), var9);
   }

   public void func_147497_a(TileEntityRendererDispatcher var1) {
      super.func_147497_a(var1);
      field_147536_b = this;
   }

   public void func_180543_a(float var1, float var2, float var3, EnumFacing var4, float var5, int var6, GameProfile var7, int var8) {
      ModelSkeletonHead var9 = this.field_178467_h;
      if (var8 >= 0) {
         this.func_147499_a(field_178460_a[var8]);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(4.0F, 2.0F, 1.0F);
         GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.func_179128_n(5888);
      } else {
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
            var9 = this.field_178468_i;
            break;
         case 3:
            var9 = this.field_178468_i;
            ResourceLocation var10 = DefaultPlayerSkin.func_177335_a();
            if (var7 != null) {
               Minecraft var11 = Minecraft.func_71410_x();
               Map var12 = var11.func_152342_ad().func_152788_a(var7);
               if (var12.containsKey(Type.SKIN)) {
                  var10 = var11.func_152342_ad().func_152792_a((MinecraftProfileTexture)var12.get(Type.SKIN), Type.SKIN);
               } else {
                  UUID var13 = EntityPlayer.func_146094_a(var7);
                  var10 = DefaultPlayerSkin.func_177334_a(var13);
               }
            }

            this.func_147499_a(var10);
            break;
         case 4:
            this.func_147499_a(field_147532_f);
         }
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179129_p();
      if (var4 != EnumFacing.UP) {
         switch(var4) {
         case NORTH:
            GlStateManager.func_179109_b(var1 + 0.5F, var2 + 0.25F, var3 + 0.74F);
            break;
         case SOUTH:
            GlStateManager.func_179109_b(var1 + 0.5F, var2 + 0.25F, var3 + 0.26F);
            var5 = 180.0F;
            break;
         case WEST:
            GlStateManager.func_179109_b(var1 + 0.74F, var2 + 0.25F, var3 + 0.5F);
            var5 = 270.0F;
            break;
         case EAST:
         default:
            GlStateManager.func_179109_b(var1 + 0.26F, var2 + 0.25F, var3 + 0.5F);
            var5 = 90.0F;
         }
      } else {
         GlStateManager.func_179109_b(var1 + 0.5F, var2, var3 + 0.5F);
      }

      float var14 = 0.0625F;
      GlStateManager.func_179091_B();
      GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
      GlStateManager.func_179141_d();
      var9.func_78088_a((Entity)null, 0.0F, 0.0F, 0.0F, var5, 0.0F, var14);
      GlStateManager.func_179121_F();
      if (var8 >= 0) {
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
      }

   }
}
