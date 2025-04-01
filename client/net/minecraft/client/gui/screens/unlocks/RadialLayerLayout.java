package net.minecraft.client.gui.screens.unlocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.multiplayer.ClientPlayerUnlocks;
import net.minecraft.core.Holder;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class RadialLayerLayout {
   private static final float DISTANCE = 1.1F;
   private static final float ADJUSTMENT_FRACTION = 0.025F;
   private static final float MAX_ADJUSTMENT_ANGLE = 0.17453294F;
   private static final float MIN_ANGLE_PER_CHILD = 0.08726647F;
   private static final int MAX_ITERATIONS = 50;
   private final Map<Holder<PlayerUnlock>, LayoutData> layoutData = new HashMap();
   private final Holder<PlayerUnlock> root;
   private final PlayerUnlocksTree tree;
   private final ClientPlayerUnlocks unlocks;

   private RadialLayerLayout(PlayerUnlocksTree var1, Holder<PlayerUnlock> var2, ClientPlayerUnlocks var3) {
      super();
      this.root = var2;
      this.tree = var1;
      this.unlocks = var3;
      this.setupLayoutData(var2, 0);
   }

   private static float getDistance(float var0, int var1) {
      float var2 = 1.1F * Math.max(1.0F, (float)(var1 + 1) / var0);
      if (Float.isNaN(var2)) {
         throw new IllegalStateException("BLAH");
      } else {
         return var2;
      }
   }

   private LayoutData setupLayoutData(Holder<PlayerUnlock> var1, int var2) {
      LayoutData var3 = new LayoutData();
      var3.layer = var2;
      var3.startAngle = -3.1415927F;
      var3.endAngle = 3.1415927F;
      List var4 = this.tree.getChildren(var1);
      Stream var10001 = var4.stream();
      ClientPlayerUnlocks var10002 = this.unlocks;
      Objects.requireNonNull(var10002);
      var3.childCount = (int)var10001.filter(var10002::isVisibleAtAll).count();
      var3.distance = getDistance(6.2831855F, var3.childCount);
      this.layoutData.put(var1, var3);
      int var5 = 0;

      for(Holder var7 : var4) {
         if (this.unlocks.isVisibleAtAll(var7)) {
            LayoutData var8 = this.setupLayoutData(var7, var2 + 1);
            var8.index = var5++;
            if (var8.distance > var3.distance) {
               var3.distance = var8.distance;
            }
         }
      }

      return var3;
   }

   private void apply() {
      this.apply(this.root);
   }

   private void apply(Holder<PlayerUnlock> var1) {
      DisplayInfo var2 = ((PlayerUnlock)var1.value()).display();
      Optional var3 = ((PlayerUnlock)var1.value()).parent();
      LayoutData var4 = (LayoutData)this.layoutData.get(var1);
      if (var3.isPresent()) {
         Holder var5 = (Holder)var3.get();
         LayoutData var6 = (LayoutData)this.layoutData.get(var5);
         float var7 = ((PlayerUnlock)var5.value()).display().getX();
         float var8 = ((PlayerUnlock)var5.value()).display().getY();
         int var9 = var6.childCount;
         Vec2 var10;
         int var11;
         if (((PlayerUnlock)var5.value()).parent().isPresent()) {
            var10 = getCoordinates(var5).add(getCoordinates((Holder)((PlayerUnlock)var5.value()).parent().get()).negated()).normalized();
            var11 = var9 + 1;
         } else {
            var10 = new Vec2(1.0F, 0.0F);
            var11 = var9;
         }

         float var12 = (float)Math.acos((double)var10.x) * (float)(var10.y > 0.0F ? -1 : 1);
         float var13 = var6.endAngle - var6.startAngle;
         float var14 = var6.distance;
         float var15;
         if (var9 == 1) {
            var15 = var12 + var6.startAngle + var13 / 2.0F;
         } else {
            var15 = var12 + var6.startAngle + (float)(var4.index + 1) * var13 / (float)var11;
         }

         var2.setLocation(var7 + Mth.cos(var15) * var14, var8 - Mth.sin(var15) * var14);
      } else {
         var2.setLocation(0.0F, 0.0F);
      }

      for(Holder var17 : this.tree.getChildren(var1)) {
         if (this.unlocks.isVisibleAtAll(var17)) {
            this.apply(var17);
         }
      }

   }

   private static Vec2 getCoordinates(Holder<PlayerUnlock> var0) {
      DisplayInfo var1 = ((PlayerUnlock)var0.value()).display();
      return new Vec2(var1.getX(), var1.getY());
   }

   private boolean iterate(Holder<PlayerUnlock> var1) {
      float var2 = 1.21F;
      LayoutData var3 = (LayoutData)this.layoutData.get(var1);
      var3.done = true;
      Optional var4 = ((PlayerUnlock)var1.value()).parent();
      if (var4.isPresent()) {
         for(Holder var6 : this.tree.getChildren((Holder)var4.get())) {
            if (this.unlocks.isVisibleAtAll(var6)) {
               Vec2 var7 = getCoordinates(var6);

               for(Map.Entry var9 : this.layoutData.entrySet()) {
                  Holder var10 = (Holder)var9.getKey();
                  if (var6 != var10 && var6 != ((PlayerUnlock)var10.value()).parent().orElse((Object)null) && var1 != var10 && var1 != ((PlayerUnlock)var10.value()).parent().orElse((Object)null)) {
                     Vec2 var11 = getCoordinates(var10);
                     float var12 = var7.distanceToSqr(var11);
                     if (!(var12 > 1.21F)) {
                        var3.done = false;
                        this.squeeze(var1, var3, var11);
                        break;
                     }
                  }
               }
            }
         }
      }

      for(Holder var14 : this.tree.getChildren(var1)) {
         if (this.unlocks.isVisibleAtAll(var14) && !this.iterate(var14)) {
            var3.done = false;
         }
      }

      return var3.done;
   }

   private void squeeze(Holder<PlayerUnlock> var1, LayoutData var2, Vec2 var3) {
      Optional var4 = ((PlayerUnlock)var1.value()).parent();
      if (!var4.isEmpty()) {
         float var5 = var2.endAngle - var2.startAngle;
         float var6 = Math.min(var5 * 0.025F, 0.17453294F);
         if ((var5 - var6) / (float)var2.childCount < 0.08726647F) {
            Optional var7 = ((PlayerUnlock)var1.value()).parent();
            Map var10001 = this.layoutData;
            Objects.requireNonNull(var10001);

            Optional var8;
            for(var8 = var7.map(var10001::get); var7.isPresent() && var8.isPresent() && ((LayoutData)var8.get()).childCount == 1; var8 = var7.map(var10001::get)) {
               var7 = ((PlayerUnlock)((Holder)var7.get()).value()).parent();
               var10001 = this.layoutData;
               Objects.requireNonNull(var10001);
            }

            if (var7.isPresent() && ((PlayerUnlock)((Holder)var7.get()).value()).parent().isPresent()) {
               this.squeeze((Holder)var7.get(), (LayoutData)var8.get(), var3);
               return;
            }
         }

         Vec2 var12 = getCoordinates(var1);
         Vec2 var13 = getCoordinates((Holder)var4.get());
         Vec2 var9 = var12.add(var13.negated()).normalized();
         Vec2 var10 = var3.add(var12.negated()).normalized();
         float var11 = (new Vec2(var9.y, -var9.x)).dot(var10);
         if (var11 < 0.0F) {
            var2.startAngle += var6;
         } else {
            var2.endAngle -= var6;
         }

         var2.distance = getDistance(var5, var2.childCount);
      }
   }

   private void iterate() {
      this.iterate(this.root);
   }

   private boolean isDone() {
      return ((LayoutData)this.layoutData.get(this.root)).done;
   }

   private void finalizePositions() {
      float var1 = 3.4028235E38F;
      float var2 = 3.4028235E38F;

      for(Map.Entry var4 : this.layoutData.entrySet()) {
         DisplayInfo var5 = ((PlayerUnlock)((Holder)var4.getKey()).value()).display();
         var1 = Math.min(var1, var5.getX());
         var2 = Math.min(var2, var5.getY());
      }

      for(Map.Entry var7 : this.layoutData.entrySet()) {
         DisplayInfo var8 = ((PlayerUnlock)((Holder)var7.getKey()).value()).display();
         var8.setLocation(var8.getX() - var1, var8.getY() - var2);
      }

   }

   public static void run(PlayerUnlocksTree var0, Holder<PlayerUnlock> var1, ClientPlayerUnlocks var2) {
      RadialLayerLayout var3 = new RadialLayerLayout(var0, var1, var2);
      var3.apply();

      for(int var4 = 0; var4 < 50 && !var3.isDone(); ++var4) {
         var3.iterate();
         var3.apply();
      }

      var3.finalizePositions();
   }

   class LayoutData {
      public int layer;
      public float startAngle;
      public float endAngle;
      public int index;
      public int childCount;
      public float distance;
      public boolean done;

      LayoutData() {
         super();
      }
   }
}
