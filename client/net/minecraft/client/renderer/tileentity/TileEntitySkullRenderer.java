package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSkullWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelDragonHead;
import net.minecraft.client.renderer.entity.model.ModelHumanoidHead;
import net.minecraft.client.renderer.entity.model.ModelSkeletonHead;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class TileEntitySkullRenderer extends TileEntityRenderer<TileEntitySkull> {
   public static TileEntitySkullRenderer field_147536_b;
   private static final Map<BlockSkull.ISkullType, ModelBase> field_199358_e = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
      ModelSkeletonHead var1 = new ModelSkeletonHead(0, 0, 64, 32);
      ModelHumanoidHead var2 = new ModelHumanoidHead();
      ModelDragonHead var3 = new ModelDragonHead(0.0F);
      var0.put(BlockSkull.Types.SKELETON, var1);
      var0.put(BlockSkull.Types.WITHER_SKELETON, var1);
      var0.put(BlockSkull.Types.PLAYER, var2);
      var0.put(BlockSkull.Types.ZOMBIE, var2);
      var0.put(BlockSkull.Types.CREEPER, var1);
      var0.put(BlockSkull.Types.DRAGON, var3);
   });
   private static final Map<BlockSkull.ISkullType, ResourceLocation> field_199357_d = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
      var0.put(BlockSkull.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      var0.put(BlockSkull.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      var0.put(BlockSkull.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      var0.put(BlockSkull.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      var0.put(BlockSkull.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      var0.put(BlockSkull.Types.PLAYER, DefaultPlayerSkin.func_177335_a());
   });

   public TileEntitySkullRenderer() {
      super();
   }

   public void func_199341_a(TileEntitySkull var1, double var2, double var4, double var6, float var8, int var9) {
      float var10 = var1.func_184295_a(var8);
      IBlockState var11 = var1.func_195044_w();
      boolean var12 = var11.func_177230_c() instanceof BlockSkullWall;
      EnumFacing var13 = var12 ? (EnumFacing)var11.func_177229_b(BlockSkullWall.field_196302_a) : null;
      float var14 = 22.5F * (float)(var12 ? (2 + var13.func_176736_b()) * 4 : (Integer)var11.func_177229_b(BlockSkull.field_196294_a));
      this.func_199355_a((float)var2, (float)var4, (float)var6, var13, var14, ((BlockAbstractSkull)var11.func_177230_c()).func_196292_N_(), var1.func_152108_a(), var9, var10);
   }

   public void func_147497_a(TileEntityRendererDispatcher var1) {
      super.func_147497_a(var1);
      field_147536_b = this;
   }

   public void func_199355_a(float var1, float var2, float var3, @Nullable EnumFacing var4, float var5, BlockSkull.ISkullType var6, @Nullable GameProfile var7, int var8, float var9) {
      ModelBase var10 = (ModelBase)field_199358_e.get(var6);
      if (var8 >= 0) {
         this.func_147499_a(field_178460_a[var8]);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(4.0F, 2.0F, 1.0F);
         GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.func_179128_n(5888);
      } else {
         this.func_147499_a(this.func_199356_a(var6, var7));
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179129_p();
      if (var4 == null) {
         GlStateManager.func_179109_b(var1 + 0.5F, var2, var3 + 0.5F);
      } else {
         switch(var4) {
         case NORTH:
            GlStateManager.func_179109_b(var1 + 0.5F, var2 + 0.25F, var3 + 0.74F);
            break;
         case SOUTH:
            GlStateManager.func_179109_b(var1 + 0.5F, var2 + 0.25F, var3 + 0.26F);
            break;
         case WEST:
            GlStateManager.func_179109_b(var1 + 0.74F, var2 + 0.25F, var3 + 0.5F);
            break;
         case EAST:
         default:
            GlStateManager.func_179109_b(var1 + 0.26F, var2 + 0.25F, var3 + 0.5F);
         }
      }

      GlStateManager.func_179091_B();
      GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
      GlStateManager.func_179141_d();
      if (var6 == BlockSkull.Types.PLAYER) {
         GlStateManager.func_187408_a(GlStateManager.Profile.PLAYER_SKIN);
      }

      var10.func_78088_a((Entity)null, var9, 0.0F, 0.0F, var5, 0.0F, 0.0625F);
      GlStateManager.func_179121_F();
      if (var8 >= 0) {
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
      }

   }

   private ResourceLocation func_199356_a(BlockSkull.ISkullType var1, @Nullable GameProfile var2) {
      ResourceLocation var3 = (ResourceLocation)field_199357_d.get(var1);
      if (var1 == BlockSkull.Types.PLAYER && var2 != null) {
         Minecraft var4 = Minecraft.func_71410_x();
         Map var5 = var4.func_152342_ad().func_152788_a(var2);
         if (var5.containsKey(Type.SKIN)) {
            var3 = var4.func_152342_ad().func_152792_a((MinecraftProfileTexture)var5.get(Type.SKIN), Type.SKIN);
         } else {
            var3 = DefaultPlayerSkin.func_177334_a(EntityPlayer.func_146094_a(var2));
         }
      }

      return var3;
   }
}
