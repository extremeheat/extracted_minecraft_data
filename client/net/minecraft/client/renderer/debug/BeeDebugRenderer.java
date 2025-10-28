package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugBeeInfo;
import net.minecraft.util.debug.DebugGoalInfo;
import net.minecraft.util.debug.DebugHiveInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public class BeeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final boolean SHOW_GOAL_FOR_ALL_BEES = true;
   private static final boolean SHOW_NAME_FOR_ALL_BEES = true;
   private static final boolean SHOW_HIVE_FOR_ALL_BEES = true;
   private static final boolean SHOW_FLOWER_POS_FOR_ALL_BEES = true;
   private static final boolean SHOW_TRAVEL_TICKS_FOR_ALL_BEES = true;
   private static final boolean SHOW_GOAL_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_NAME_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_HIVE_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_FLOWER_POS_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_TRAVEL_TICKS_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_HIVE_MEMBERS = true;
   private static final boolean SHOW_BLACKLISTS = true;
   private static final int MAX_RENDER_DIST_FOR_HIVE_OVERLAY = 30;
   private static final int MAX_RENDER_DIST_FOR_BEE_OVERLAY = 30;
   private static final int MAX_TARGETING_DIST = 8;
   private static final float TEXT_SCALE = 0.32F;
   private static final int ORANGE = -23296;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private final Minecraft minecraft;
   private @Nullable UUID lastLookedAtUuid;

   public BeeDebugRenderer(Minecraft var1) {
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
      BlockPos var2 = this.getCamera().blockPosition();
      var1.forEachEntity(DebugSubscriptions.BEES, (var2x, var3x) -> {
         if (this.minecraft.player.closerThan(var2x, 30.0)) {
            DebugGoalInfo var4 = (DebugGoalInfo)var1.getEntityValue(DebugSubscriptions.GOAL_SELECTORS, var2x);
            this.renderBeeInfo(var2x, var3x, var4);
         }

      });
      this.renderFlowerInfos(var1);
      Map var3 = this.createHiveBlacklistMap(var1);
      var1.forEachBlock(DebugSubscriptions.BEE_HIVES, (var4, var5) -> {
         if (var2.closerThan(var4, 30.0)) {
            highlightHive(var4);
            Set var6 = (Set)var3.getOrDefault(var4, Set.of());
            this.renderHiveInfo(var4, var5, var6, var1);
         }

      });
      this.getGhostHives(var1).forEach((var2x, var3x) -> {
         if (var2.closerThan(var2x, 30.0)) {
            this.renderGhostHive(var2x, var3x);
         }

      });
   }

   private Map<BlockPos, Set<UUID>> createHiveBlacklistMap(DebugValueAccess var1) {
      HashMap var2 = new HashMap();
      var1.forEachEntity(DebugSubscriptions.BEES, (var1x, var2x) -> {
         for(BlockPos var4 : var2x.blacklistedHives()) {
            ((Set)var2.computeIfAbsent(var4, (var0) -> new HashSet())).add(var1x.getUUID());
         }

      });
      return var2;
   }

   private void renderFlowerInfos(DebugValueAccess var1) {
      HashMap var2 = new HashMap();
      var1.forEachEntity(DebugSubscriptions.BEES, (var1x, var2x) -> {
         if (var2x.flowerPos().isPresent()) {
            ((Set)var2.computeIfAbsent((BlockPos)var2x.flowerPos().get(), (var0) -> new HashSet())).add(var1x.getUUID());
         }

      });
      var2.forEach((var0, var1x) -> {
         Set var2 = (Set)var1x.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
         int var3 = 1;
         Gizmos.billboardTextOverBlock(var2.toString(), var0, var3++, -256, 0.32F);
         Gizmos.billboardTextOverBlock("Flower", var0, var3++, -1, 0.32F);
         Gizmos.cuboid(var0, 0.05F, GizmoStyle.fill(ARGB.colorFromFloat(0.3F, 0.8F, 0.8F, 0.0F)));
      });
   }

   private static String getBeeUuidsAsString(Collection<UUID> var0) {
      if (var0.isEmpty()) {
         return "-";
      } else {
         return var0.size() > 3 ? var0.size() + " bees" : ((Set)var0.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet())).toString();
      }
   }

   private static void highlightHive(BlockPos var0) {
      float var1 = 0.05F;
      Gizmos.cuboid(var0, 0.05F, GizmoStyle.fill(ARGB.colorFromFloat(0.3F, 0.2F, 0.2F, 1.0F)));
   }

   private void renderGhostHive(BlockPos var1, List<String> var2) {
      float var3 = 0.05F;
      Gizmos.cuboid(var1, 0.05F, GizmoStyle.fill(ARGB.colorFromFloat(0.3F, 0.2F, 0.2F, 1.0F)));
      Gizmos.billboardTextOverBlock(var2.toString(), var1, 0, -256, 0.32F);
      Gizmos.billboardTextOverBlock("Ghost Hive", var1, 1, -65536, 0.32F);
   }

   private void renderHiveInfo(BlockPos var1, DebugHiveInfo var2, Collection<UUID> var3, DebugValueAccess var4) {
      int var5 = 0;
      if (!var3.isEmpty()) {
         renderTextOverHive("Blacklisted by " + getBeeUuidsAsString(var3), var1, var5++, -65536);
      }

      renderTextOverHive("Out: " + getBeeUuidsAsString(this.getHiveMembers(var1, var4)), var1, var5++, -3355444);
      if (var2.occupantCount() == 0) {
         renderTextOverHive("In: -", var1, var5++, -256);
      } else if (var2.occupantCount() == 1) {
         renderTextOverHive("In: 1 bee", var1, var5++, -256);
      } else {
         renderTextOverHive("In: " + var2.occupantCount() + " bees", var1, var5++, -256);
      }

      int var12 = var2.honeyLevel();
      renderTextOverHive("Honey: " + var12, var1, var5++, -23296);
      renderTextOverHive(var2.type().getName().getString() + (var2.sedated() ? " (sedated)" : ""), var1, var5++, -1);
   }

   private void renderBeeInfo(Entity var1, DebugBeeInfo var2, @Nullable DebugGoalInfo var3) {
      this.isBeeSelected(var1);
      int var5 = 0;
      Gizmos.billboardTextOverMob(var1, var5++, var2.toString(), -1, 0.48F);
      if (var2.hivePos().isEmpty()) {
         Gizmos.billboardTextOverMob(var1, var5++, "No hive", -98404, 0.32F);
      } else {
         Gizmos.billboardTextOverMob(var1, var5++, "Hive: " + this.getPosDescription(var1, (BlockPos)var2.hivePos().get()), -256, 0.32F);
      }

      if (var2.flowerPos().isEmpty()) {
         Gizmos.billboardTextOverMob(var1, var5++, "No flower", -98404, 0.32F);
      } else {
         Gizmos.billboardTextOverMob(var1, var5++, "Flower: " + this.getPosDescription(var1, (BlockPos)var2.flowerPos().get()), -256, 0.32F);
      }

      if (var3 != null) {
         for(DebugGoalInfo.DebugGoal var7 : var3.goals()) {
            if (var7.isRunning()) {
               Gizmos.billboardTextOverMob(var1, var5++, var7.name(), -16711936, 0.32F);
            }
         }
      }

      if (var2.travelTicks() > 0) {
         int var12 = var2.travelTicks() < 2400 ? -3355444 : -23296;
         Gizmos.billboardTextOverMob(var1, var5++, "Travelling: " + var2.travelTicks() + " ticks", var12, 0.32F);
      }

   }

   private static void renderTextOverHive(String var0, BlockPos var1, int var2, int var3) {
      Gizmos.billboardTextOverBlock(var0, var1, var2, var3, 0.32F);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }

   private String getPosDescription(Entity var1, BlockPos var2) {
      double var3 = var2.distToCenterSqr(var1.position());
      double var5 = (double)Math.round(var3 * 10.0) / 10.0;
      String var10000 = var2.toShortString();
      return var10000 + " (dist " + var5 + ")";
   }

   private boolean isBeeSelected(Entity var1) {
      return Objects.equals(this.lastLookedAtUuid, var1.getUUID());
   }

   private Collection<UUID> getHiveMembers(BlockPos var1, DebugValueAccess var2) {
      HashSet var3 = new HashSet();
      var2.forEachEntity(DebugSubscriptions.BEES, (var2x, var3x) -> {
         if (var3x.hasHive(var1)) {
            var3.add(var2x.getUUID());
         }

      });
      return var3;
   }

   private Map<BlockPos, List<String>> getGhostHives(DebugValueAccess var1) {
      HashMap var2 = new HashMap();
      var1.forEachEntity(DebugSubscriptions.BEES, (var2x, var3) -> {
         if (var3.hivePos().isPresent() && var1.getBlockValue(DebugSubscriptions.BEE_HIVES, (BlockPos)var3.hivePos().get()) == null) {
            ((List)var2.computeIfAbsent((BlockPos)var3.hivePos().get(), (var0) -> Lists.newArrayList())).add(DebugEntityNameGenerator.getEntityName(var2x));
         }

      });
      return var2;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((var1) -> this.lastLookedAtUuid = var1.getUUID());
   }
}
