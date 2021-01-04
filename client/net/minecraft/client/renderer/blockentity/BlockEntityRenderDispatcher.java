package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.HitResult;

public class BlockEntityRenderDispatcher {
   private final Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers = Maps.newHashMap();
   public static final BlockEntityRenderDispatcher instance = new BlockEntityRenderDispatcher();
   private Font font;
   public static double xOff;
   public static double yOff;
   public static double zOff;
   public TextureManager textureManager;
   public Level level;
   public Camera camera;
   public HitResult cameraHitResult;

   private BlockEntityRenderDispatcher() {
      super();
      this.renderers.put(SignBlockEntity.class, new SignRenderer());
      this.renderers.put(SpawnerBlockEntity.class, new SpawnerRenderer());
      this.renderers.put(PistonMovingBlockEntity.class, new PistonHeadRenderer());
      this.renderers.put(ChestBlockEntity.class, new ChestRenderer());
      this.renderers.put(EnderChestBlockEntity.class, new ChestRenderer());
      this.renderers.put(EnchantmentTableBlockEntity.class, new EnchantTableRenderer());
      this.renderers.put(LecternBlockEntity.class, new LecternRenderer());
      this.renderers.put(TheEndPortalBlockEntity.class, new TheEndPortalRenderer());
      this.renderers.put(TheEndGatewayBlockEntity.class, new TheEndGatewayRenderer());
      this.renderers.put(BeaconBlockEntity.class, new BeaconRenderer());
      this.renderers.put(SkullBlockEntity.class, new SkullBlockRenderer());
      this.renderers.put(BannerBlockEntity.class, new BannerRenderer());
      this.renderers.put(StructureBlockEntity.class, new StructureBlockRenderer());
      this.renderers.put(ShulkerBoxBlockEntity.class, new ShulkerBoxRenderer(new ShulkerModel()));
      this.renderers.put(BedBlockEntity.class, new BedRenderer());
      this.renderers.put(ConduitBlockEntity.class, new ConduitRenderer());
      this.renderers.put(BellBlockEntity.class, new BellRenderer());
      this.renderers.put(CampfireBlockEntity.class, new CampfireRenderer());
      Iterator var1 = this.renderers.values().iterator();

      while(var1.hasNext()) {
         BlockEntityRenderer var2 = (BlockEntityRenderer)var1.next();
         var2.init(this);
      }

   }

   public <T extends BlockEntity> BlockEntityRenderer<T> getRenderer(Class<? extends BlockEntity> var1) {
      BlockEntityRenderer var2 = (BlockEntityRenderer)this.renderers.get(var1);
      if (var2 == null && var1 != BlockEntity.class) {
         var2 = this.getRenderer(var1.getSuperclass());
         this.renderers.put(var1, var2);
      }

      return var2;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityRenderer<T> getRenderer(@Nullable BlockEntity var1) {
      return var1 == null ? null : this.getRenderer(var1.getClass());
   }

   public void prepare(Level var1, TextureManager var2, Font var3, Camera var4, HitResult var5) {
      if (this.level != var1) {
         this.setLevel(var1);
      }

      this.textureManager = var2;
      this.camera = var4;
      this.font = var3;
      this.cameraHitResult = var5;
   }

   public void render(BlockEntity var1, float var2, int var3) {
      if (var1.distanceToSqr(this.camera.getPosition().x, this.camera.getPosition().y, this.camera.getPosition().z) < var1.getViewDistance()) {
         Lighting.turnOn();
         int var4 = this.level.getLightColor(var1.getBlockPos(), 0);
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var5, (float)var6);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos var7 = var1.getBlockPos();
         this.render(var1, (double)var7.getX() - xOff, (double)var7.getY() - yOff, (double)var7.getZ() - zOff, var2, var3, false);
      }

   }

   public void render(BlockEntity var1, double var2, double var4, double var6, float var8) {
      this.render(var1, var2, var4, var6, var8, -1, false);
   }

   public void renderItem(BlockEntity var1) {
      this.render(var1, 0.0D, 0.0D, 0.0D, 0.0F, -1, true);
   }

   public void render(BlockEntity var1, double var2, double var4, double var6, float var8, int var9, boolean var10) {
      BlockEntityRenderer var11 = this.getRenderer(var1);
      if (var11 != null) {
         try {
            if (var10 || var1.hasLevel() && var1.getType().isValid(var1.getBlockState().getBlock())) {
               var11.render(var1, var2, var4, var6, var8, var9);
            }
         } catch (Throwable var15) {
            CrashReport var13 = CrashReport.forThrowable(var15, "Rendering Block Entity");
            CrashReportCategory var14 = var13.addCategory("Block Entity Details");
            var1.fillCrashReportCategory(var14);
            throw new ReportedException(var13);
         }
      }

   }

   public void setLevel(@Nullable Level var1) {
      this.level = var1;
      if (var1 == null) {
         this.camera = null;
      }

   }

   public Font getFont() {
      return this.font;
   }
}
