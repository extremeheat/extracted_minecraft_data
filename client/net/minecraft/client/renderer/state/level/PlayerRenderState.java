package net.minecraft.client.renderer.state.level;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class PlayerRenderState {
   public boolean hasPlayer;
   public @Nullable AvatarRenderState avatarRenderState;
   public final FirstPersonHandsAndItemsRenderState firstPersonHandsAndItems = new FirstPersonHandsAndItemsRenderState();
   public float portalEffectIntensity;
   public float nauseaEffectIntensity;
   public float spinningEffectAngle;
   public boolean isEyeInWater;
   public boolean isOnFire;
   public boolean isUnderWater;
   public double eyePositionY;
   public @Nullable BlockOverlay blockOverlay;
   public @Nullable WaterOverlay waterOverlay;
   public @Nullable ItemActivationRenderState itemActivation;

   public PlayerRenderState() {
      super();
   }

   public void reset() {
      this.hasPlayer = false;
      this.avatarRenderState = null;
      this.blockOverlay = null;
      this.waterOverlay = null;
      this.itemActivation = null;
   }

   public static record BlockOverlay(Identifier atlasLocation, float u0, float v0, float u1, float v1) {
      public BlockOverlay {
         super();
      }
   }

   public static record WaterOverlay(int color, float uOffset, float vOffset) {
      public WaterOverlay {
         super();
      }
   }

   public static class ItemActivationRenderState {
      public final ItemStack item;
      public final int ticks;
      public final float offX;
      public final float offY;
      public final ItemStackRenderState itemState = new ItemStackRenderState();

      public ItemActivationRenderState(final ItemStack item, final int ticks, final float offX, final float offY) {
         super();
         this.item = item;
         this.ticks = ticks;
         this.offX = offX;
         this.offY = offY;
      }
   }
}
