package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
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
   private static final float TEXT_SCALE = 0.02F;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      this.doRender(var1, var2, var3, var5, var7, var9);
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void doRender(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      var9.forEachEntity(DebugSubscriptions.BRAINS, (var9x, var10) -> {
         if (this.minecraft.player.closerThan(var9x, 30.0)) {
            this.renderBrainInfo(var1, var2, var9x, var10, var3, var5, var7);
         }

      });
   }

   private void renderBrainInfo(PoseStack var1, MultiBufferSource var2, Entity var3, DebugBrainDump var4, double var5, double var7, double var9) {
      boolean var11 = this.isMobSelected(var3);
      int var12 = 0;
      DebugRenderer.renderTextOverMob(var1, var2, var3, var12, var4.name(), -1, 0.03F);
      ++var12;
      if (var11) {
         DebugRenderer.renderTextOverMob(var1, var2, var3, var12, var4.profession() + " " + var4.xp() + " xp", -1, 0.02F);
         ++var12;
      }

      if (var11) {
         int var13 = var4.health() < var4.maxHealth() ? -23296 : -1;
         String var10004 = String.format(Locale.ROOT, "%.1f", var4.health());
         DebugRenderer.renderTextOverMob(var1, var2, var3, var12, "health: " + var10004 + " / " + String.format(Locale.ROOT, "%.1f", var4.maxHealth()), var13, 0.02F);
         ++var12;
      }

      if (var11 && !var4.inventory().equals("")) {
         DebugRenderer.renderTextOverMob(var1, var2, var3, var12, var4.inventory(), -98404, 0.02F);
         ++var12;
      }

      if (var11) {
         for(String var14 : var4.behaviors()) {
            DebugRenderer.renderTextOverMob(var1, var2, var3, var12, var14, -16711681, 0.02F);
            ++var12;
         }
      }

      if (var11) {
         for(String var20 : var4.activities()) {
            DebugRenderer.renderTextOverMob(var1, var2, var3, var12, var20, -16711936, 0.02F);
            ++var12;
         }
      }

      if (var4.wantsGolem()) {
         DebugRenderer.renderTextOverMob(var1, var2, var3, var12, "Wants Golem", -23296, 0.02F);
         ++var12;
      }

      if (var11 && var4.angerLevel() != -1) {
         DebugRenderer.renderTextOverMob(var1, var2, var3, var12, "Anger Level: " + var4.angerLevel(), -98404, 0.02F);
         ++var12;
      }

      if (var11) {
         for(String var21 : var4.gossips()) {
            if (var21.startsWith(var4.name())) {
               DebugRenderer.renderTextOverMob(var1, var2, var3, var12, var21, -1, 0.02F);
            } else {
               DebugRenderer.renderTextOverMob(var1, var2, var3, var12, var21, -23296, 0.02F);
            }

            ++var12;
         }
      }

      if (var11) {
         for(String var22 : Lists.reverse(var4.memories())) {
            DebugRenderer.renderTextOverMob(var1, var2, var3, var12, var22, -3355444, 0.02F);
            ++var12;
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
