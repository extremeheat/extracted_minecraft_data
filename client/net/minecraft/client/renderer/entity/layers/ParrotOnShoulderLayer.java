package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class ParrotOnShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
   private final ParrotModel model = new ParrotModel();

   public ParrotOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.render(var1, var2, var3, var4, var6, var7, var8, true);
      this.render(var1, var2, var3, var4, var6, var7, var8, false);
      GlStateManager.disableRescaleNormal();
   }

   private void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8) {
      CompoundTag var9 = var8 ? var1.getShoulderEntityLeft() : var1.getShoulderEntityRight();
      EntityType.byString(var9.getString("id")).filter((var0) -> {
         return var0 == EntityType.PARROT;
      }).ifPresent((var9x) -> {
         GlStateManager.pushMatrix();
         GlStateManager.translatef(var8 ? 0.4F : -0.4F, var1.isVisuallySneaking() ? -1.3F : -1.5F, 0.0F);
         this.bindTexture(ParrotRenderer.PARROT_LOCATIONS[var9.getInt("Variant")]);
         this.model.renderOnShoulder(var2, var3, var5, var6, var7, var1.tickCount);
         GlStateManager.popMatrix();
      });
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
