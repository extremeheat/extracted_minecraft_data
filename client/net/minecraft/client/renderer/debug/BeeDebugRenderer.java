package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.util.debug.DebugBeeInfo;
import net.minecraft.util.debug.DebugGoalInfo;
import net.minecraft.util.debug.DebugHiveInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;

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
   private static final float TEXT_SCALE = 0.02F;
   private static final int ORANGE = -23296;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private final Minecraft minecraft;
   @Nullable
   private UUID lastLookedAtUuid;

   public BeeDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void clear() {
      this.lastLookedAtUuid = null;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      this.doRender(var1, var2, var9);
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void doRender(PoseStack var1, MultiBufferSource var2, DebugValueAccess var3) {
      BlockPos var4 = this.getCamera().getBlockPosition();
      var3.forEachEntity(DebugSubscriptions.BEES, (var4x, var5x) -> {
         if (this.minecraft.player.closerThan(var4x, 30.0)) {
            DebugGoalInfo var6 = (DebugGoalInfo)var3.getEntityValue(DebugSubscriptions.GOAL_SELECTORS, var4x);
            this.renderBeeInfo(var1, var2, var4x, var5x, var6);
         }

      });
      this.renderFlowerInfos(var1, var2, var3);
      Map var5 = this.createHiveBlacklistMap(var3);
      var3.forEachBlock(DebugSubscriptions.BEE_HIVES, (var6, var7) -> {
         if (var4.closerThan(var6, 30.0)) {
            highlightHive(var1, var2, var6);
            Set var8 = (Set)var5.getOrDefault(var6, Set.of());
            this.renderHiveInfo(var1, var2, var6, var7, var8, var3);
         }

      });
      this.getGhostHives(var3).forEach((var4x, var5x) -> {
         if (var4.closerThan(var4x, 30.0)) {
            this.renderGhostHive(var1, var2, var4x, var5x);
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

   private void renderFlowerInfos(PoseStack var1, MultiBufferSource var2, DebugValueAccess var3) {
      HashMap var4 = new HashMap();
      var3.forEachEntity(DebugSubscriptions.BEES, (var1x, var2x) -> {
         if (var2x.flowerPos().isPresent()) {
            ((Set)var4.computeIfAbsent((BlockPos)var2x.flowerPos().get(), (var0) -> new HashSet())).add(var1x.getUUID());
         }

      });
      var4.forEach((var2x, var3x) -> {
         Set var4 = (Set)var3x.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
         int var5 = 1;
         DebugRenderer.renderTextOverBlock(var1, var2, var4.toString(), var2x, var5++, -256, 0.02F);
         DebugRenderer.renderTextOverBlock(var1, var2, "Flower", var2x, var5++, -1, 0.02F);
         float var6 = 0.05F;
         DebugRenderer.renderFilledBox(var1, var2, var2x, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
      });
   }

   private static String getBeeUuidsAsString(Collection<UUID> var0) {
      if (var0.isEmpty()) {
         return "-";
      } else {
         return var0.size() > 3 ? var0.size() + " bees" : ((Set)var0.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet())).toString();
      }
   }

   private static void highlightHive(PoseStack var0, MultiBufferSource var1, BlockPos var2) {
      float var3 = 0.05F;
      DebugRenderer.renderFilledBox(var0, var1, var2, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostHive(PoseStack var1, MultiBufferSource var2, BlockPos var3, List<String> var4) {
      float var5 = 0.05F;
      DebugRenderer.renderFilledBox(var1, var2, var3, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      DebugRenderer.renderTextOverBlock(var1, var2, var4.toString(), var3, 0, -256, 0.02F);
      DebugRenderer.renderTextOverBlock(var1, var2, "Ghost Hive", var3, 1, -65536, 0.02F);
   }

   private void renderHiveInfo(PoseStack var1, MultiBufferSource var2, BlockPos var3, DebugHiveInfo var4, Collection<UUID> var5, DebugValueAccess var6) {
      int var7 = 0;
      if (!var5.isEmpty()) {
         renderTextOverHive(var1, var2, "Blacklisted by " + getBeeUuidsAsString(var5), var3, var4, var7++, -65536);
      }

      renderTextOverHive(var1, var2, "Out: " + getBeeUuidsAsString(this.getHiveMembers(var3, var6)), var3, var4, var7++, -3355444);
      if (var4.occupantCount() == 0) {
         renderTextOverHive(var1, var2, "In: -", var3, var4, var7++, -256);
      } else if (var4.occupantCount() == 1) {
         renderTextOverHive(var1, var2, "In: 1 bee", var3, var4, var7++, -256);
      } else {
         renderTextOverHive(var1, var2, "In: " + var4.occupantCount() + " bees", var3, var4, var7++, -256);
      }

      int var14 = var4.honeyLevel();
      renderTextOverHive(var1, var2, "Honey: " + var14, var3, var4, var7++, -23296);
      renderTextOverHive(var1, var2, var4.type().getName().getString() + (var4.sedated() ? " (sedated)" : ""), var3, var4, var7++, -1);
   }

   private void renderBeeInfo(PoseStack var1, MultiBufferSource var2, Entity var3, DebugBeeInfo var4, @Nullable DebugGoalInfo var5) {
      this.isBeeSelected(var3);
      int var7 = 0;
      DebugRenderer.renderTextOverMob(var1, var2, var3, var7++, var4.toString(), -1, 0.03F);
      if (var4.hivePos().isEmpty()) {
         DebugRenderer.renderTextOverMob(var1, var2, var3, var7++, "No hive", -98404, 0.02F);
      } else {
         DebugRenderer.renderTextOverMob(var1, var2, var3, var7++, "Hive: " + this.getPosDescription(var3, (BlockPos)var4.hivePos().get()), -256, 0.02F);
      }

      if (var4.flowerPos().isEmpty()) {
         DebugRenderer.renderTextOverMob(var1, var2, var3, var7++, "No flower", -98404, 0.02F);
      } else {
         DebugRenderer.renderTextOverMob(var1, var2, var3, var7++, "Flower: " + this.getPosDescription(var3, (BlockPos)var4.flowerPos().get()), -256, 0.02F);
      }

      if (var5 != null) {
         for(DebugGoalInfo.DebugGoal var9 : var5.goals()) {
            if (var9.isRunning()) {
               DebugRenderer.renderTextOverMob(var1, var2, var3, var7++, var9.name(), -16711936, 0.02F);
            }
         }
      }

      if (var4.travelTicks() > 0) {
         int var14 = var4.travelTicks() < 2400 ? -3355444 : -23296;
         DebugRenderer.renderTextOverMob(var1, var2, var3, var7++, "Travelling: " + var4.travelTicks() + " ticks", var14, 0.02F);
      }

   }

   private static void renderTextOverHive(PoseStack var0, MultiBufferSource var1, String var2, BlockPos var3, DebugHiveInfo var4, int var5, int var6) {
      DebugRenderer.renderTextOverBlock(var0, var1, var2, var3, var5, var6, 0.02F);
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
