package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugBrainDump;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;

public class BrainDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final boolean SHOW_NAME_FOR_ALL = true;
   private static final boolean SHOW_PROFESSION_FOR_ALL = false;
   private static final boolean SHOW_BEHAVIORS_FOR_ALL = false;
   private static final boolean SHOW_ACTIVITIES_FOR_ALL = false;
   private static final boolean SHOW_INVENTORY_FOR_ALL = false;
   private static final boolean SHOW_GOSSIPS_FOR_ALL = false;
   private static final boolean SHOW_HEALTH_FOR_ALL = false;
   private static final boolean SHOW_WANTS_GOLEM_FOR_ALL = true;
   private static final boolean SHOW_ANGER_LEVEL_FOR_ALL = false;
   private static final boolean SHOW_NAME_FOR_SELECTED = true;
   private static final boolean SHOW_PROFESSION_FOR_SELECTED = true;
   private static final boolean SHOW_BEHAVIORS_FOR_SELECTED = true;
   private static final boolean SHOW_ACTIVITIES_FOR_SELECTED = true;
   private static final boolean SHOW_MEMORIES_FOR_SELECTED = true;
   private static final boolean SHOW_INVENTORY_FOR_SELECTED = true;
   private static final boolean SHOW_GOSSIPS_FOR_SELECTED = true;
   private static final boolean SHOW_HEALTH_FOR_SELECTED = true;
   private static final boolean SHOW_WANTS_GOLEM_FOR_SELECTED = true;
   private static final boolean SHOW_ANGER_LEVEL_FOR_SELECTED = true;
   private static final int MAX_RENDER_DIST_FOR_BRAIN_INFO = 30;
   private static final int MAX_TARGETING_DIST = 8;
   private static final float TEXT_SCALE = 0.32F;
   private static final int CYAN = -16711681;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private static final int ORANGE = -23296;
   private final Minecraft minecraft;
   @Nullable
   private UUID lastLookedAtUuid;

   public BrainDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      this.doRender(var7);
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void doRender(DebugValueAccess var1) {
      var1.forEachEntity(DebugSubscriptions.BRAINS, (var1x, var2) -> {
         if (this.minecraft.player.closerThan(var1x, 30.0)) {
            this.renderBrainInfo(var1x, var2);
         }

      });
   }

   private void renderBrainInfo(Entity var1, DebugBrainDump var2) {
      boolean var3 = this.isMobSelected(var1);
      int var4 = 0;
      Gizmos.billboardTextOverMob(var1, var4, var2.name(), -1, 0.48F);
      ++var4;
      if (var3) {
         Gizmos.billboardTextOverMob(var1, var4, var2.profession() + " " + var2.xp() + " xp", -1, 0.32F);
         ++var4;
      }

      if (var3) {
         int var5 = var2.health() < var2.maxHealth() ? -23296 : -1;
         String var10002 = String.format(Locale.ROOT, "%.1f", var2.health());
         Gizmos.billboardTextOverMob(var1, var4, "health: " + var10002 + " / " + String.format(Locale.ROOT, "%.1f", var2.maxHealth()), var5, 0.32F);
         ++var4;
      }

      if (var3 && !var2.inventory().equals("")) {
         Gizmos.billboardTextOverMob(var1, var4, var2.inventory(), -98404, 0.32F);
         ++var4;
      }

      if (var3) {
         for(String var6 : var2.behaviors()) {
            Gizmos.billboardTextOverMob(var1, var4, var6, -16711681, 0.32F);
            ++var4;
         }
      }

      if (var3) {
         for(String var12 : var2.activities()) {
            Gizmos.billboardTextOverMob(var1, var4, var12, -16711936, 0.32F);
            ++var4;
         }
      }

      if (var2.wantsGolem()) {
         Gizmos.billboardTextOverMob(var1, var4, "Wants Golem", -23296, 0.32F);
         ++var4;
      }

      if (var3 && var2.angerLevel() != -1) {
         Gizmos.billboardTextOverMob(var1, var4, "Anger Level: " + var2.angerLevel(), -98404, 0.32F);
         ++var4;
      }

      if (var3) {
         for(String var13 : var2.gossips()) {
            if (var13.startsWith(var2.name())) {
               Gizmos.billboardTextOverMob(var1, var4, var13, -1, 0.32F);
            } else {
               Gizmos.billboardTextOverMob(var1, var4, var13, -23296, 0.32F);
            }

            ++var4;
         }
      }

      if (var3) {
         for(String var14 : Lists.reverse(var2.memories())) {
            Gizmos.billboardTextOverMob(var1, var4, var14, -3355444, 0.32F);
            ++var4;
         }
      }

   }

   private boolean isMobSelected(Entity var1) {
      return Objects.equals(this.lastLookedAtUuid, var1.getUUID());
   }

   public Map<BlockPos, List<String>> getGhostPois(DebugValueAccess var1) {
      HashMap var2 = Maps.newHashMap();
      var1.forEachEntity(DebugSubscriptions.BRAINS, (var1x, var2x) -> {
         for(BlockPos var4 : Iterables.concat(var2x.pois(), var2x.potentialPois())) {
            ((List)var2.computeIfAbsent(var4, (var0) -> Lists.newArrayList())).add(var2x.name());
         }

      });
      return var2;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((var1) -> this.lastLookedAtUuid = var1.getUUID());
   }
}
