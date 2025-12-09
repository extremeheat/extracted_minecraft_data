package net.minecraft.client.gui.render.state;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public final class GuiItemRenderState implements ScreenArea {
   private final String name;
   private final Matrix3x2f pose;
   private final TrackingItemStackRenderState itemStackRenderState;
   private final int x;
   private final int y;
   private final @Nullable ScreenRectangle scissorArea;
   private final @Nullable ScreenRectangle oversizedItemBounds;
   private final @Nullable ScreenRectangle bounds;

   public GuiItemRenderState(String var1, Matrix3x2f var2, TrackingItemStackRenderState var3, int var4, int var5, @Nullable ScreenRectangle var6) {
      super();
      this.name = var1;
      this.pose = var2;
      this.itemStackRenderState = var3;
      this.x = var4;
      this.y = var5;
      this.scissorArea = var6;
      this.oversizedItemBounds = this.itemStackRenderState().isOversizedInGui() ? this.calculateOversizedItemBounds() : null;
      this.bounds = this.calculateBounds(this.oversizedItemBounds != null ? this.oversizedItemBounds : new ScreenRectangle(this.x, this.y, 16, 16));
   }

   private @Nullable ScreenRectangle calculateOversizedItemBounds() {
      AABB var1 = this.itemStackRenderState.getModelBoundingBox();
      int var2 = Mth.ceil(var1.getXsize() * 16.0);
      int var3 = Mth.ceil(var1.getYsize() * 16.0);
      if (var2 <= 16 && var3 <= 16) {
         return null;
      } else {
         float var4 = (float)(var1.minX * 16.0);
         float var5 = (float)(var1.maxY * 16.0);
         int var6 = Mth.floor(var4);
         int var7 = Mth.floor(var5);
         int var8 = this.x + var6 + 8;
         int var9 = this.y - var7 + 8;
         return new ScreenRectangle(var8, var9, var2, var3);
      }
   }

   private @Nullable ScreenRectangle calculateBounds(ScreenRectangle var1) {
      ScreenRectangle var2 = var1.transformMaxBounds(this.pose);
      return this.scissorArea != null ? this.scissorArea.intersection(var2) : var2;
   }

   public String name() {
      return this.name;
   }

   public Matrix3x2f pose() {
      return this.pose;
   }

   public TrackingItemStackRenderState itemStackRenderState() {
      return this.itemStackRenderState;
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public @Nullable ScreenRectangle scissorArea() {
      return this.scissorArea;
   }

   public @Nullable ScreenRectangle oversizedItemBounds() {
      return this.oversizedItemBounds;
   }

   public @Nullable ScreenRectangle bounds() {
      return this.bounds;
   }
}
