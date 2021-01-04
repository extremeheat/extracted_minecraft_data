package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockRenderer extends BlockEntityRenderer<SkullBlockEntity> {
   public static SkullBlockRenderer instance;
   private static final Map<SkullBlock.Type, SkullModel> MODEL_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      SkullModel var1 = new SkullModel(0, 0, 64, 32);
      HumanoidHeadModel var2 = new HumanoidHeadModel();
      DragonHeadModel var3 = new DragonHeadModel(0.0F);
      var0.put(SkullBlock.Types.SKELETON, var1);
      var0.put(SkullBlock.Types.WITHER_SKELETON, var1);
      var0.put(SkullBlock.Types.PLAYER, var2);
      var0.put(SkullBlock.Types.ZOMBIE, var2);
      var0.put(SkullBlock.Types.CREEPER, var1);
      var0.put(SkullBlock.Types.DRAGON, var3);
   });
   private static final Map<SkullBlock.Type, ResourceLocation> SKIN_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
      var0.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
      var0.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
      var0.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
      var0.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
      var0.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkin());
   });

   public SkullBlockRenderer() {
      super();
   }

   public void render(SkullBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      float var10 = var1.getMouthAnimation(var8);
      BlockState var11 = var1.getBlockState();
      boolean var12 = var11.getBlock() instanceof WallSkullBlock;
      Direction var13 = var12 ? (Direction)var11.getValue(WallSkullBlock.FACING) : null;
      float var14 = 22.5F * (float)(var12 ? (2 + var13.get2DDataValue()) * 4 : (Integer)var11.getValue(SkullBlock.ROTATION));
      this.renderSkull((float)var2, (float)var4, (float)var6, var13, var14, ((AbstractSkullBlock)var11.getBlock()).getType(), var1.getOwnerProfile(), var9, var10);
   }

   public void init(BlockEntityRenderDispatcher var1) {
      super.init(var1);
      instance = this;
   }

   public void renderSkull(float var1, float var2, float var3, @Nullable Direction var4, float var5, SkullBlock.Type var6, @Nullable GameProfile var7, int var8, float var9) {
      SkullModel var10 = (SkullModel)MODEL_BY_TYPE.get(var6);
      if (var8 >= 0) {
         this.bindTexture(BREAKING_LOCATIONS[var8]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 2.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         this.bindTexture(this.getLocation(var6, var7));
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (var4 == null) {
         GlStateManager.translatef(var1 + 0.5F, var2, var3 + 0.5F);
      } else {
         switch(var4) {
         case NORTH:
            GlStateManager.translatef(var1 + 0.5F, var2 + 0.25F, var3 + 0.74F);
            break;
         case SOUTH:
            GlStateManager.translatef(var1 + 0.5F, var2 + 0.25F, var3 + 0.26F);
            break;
         case WEST:
            GlStateManager.translatef(var1 + 0.74F, var2 + 0.25F, var3 + 0.5F);
            break;
         case EAST:
         default:
            GlStateManager.translatef(var1 + 0.26F, var2 + 0.25F, var3 + 0.5F);
         }
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      if (var6 == SkullBlock.Types.PLAYER) {
         GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);
      }

      var10.render(var9, 0.0F, 0.0F, var5, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if (var8 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private ResourceLocation getLocation(SkullBlock.Type var1, @Nullable GameProfile var2) {
      ResourceLocation var3 = (ResourceLocation)SKIN_BY_TYPE.get(var1);
      if (var1 == SkullBlock.Types.PLAYER && var2 != null) {
         Minecraft var4 = Minecraft.getInstance();
         Map var5 = var4.getSkinManager().getInsecureSkinInformation(var2);
         if (var5.containsKey(Type.SKIN)) {
            var3 = var4.getSkinManager().registerTexture((MinecraftProfileTexture)var5.get(Type.SKIN), Type.SKIN);
         } else {
            var3 = DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(var2));
         }
      }

      return var3;
   }
}
