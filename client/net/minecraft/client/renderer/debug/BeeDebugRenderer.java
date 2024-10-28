package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.common.custom.BeeDebugPayload;
import net.minecraft.network.protocol.common.custom.HiveDebugPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.player.Player;

public class BeeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final boolean SHOW_GOAL_FOR_ALL_BEES = true;
   private static final boolean SHOW_NAME_FOR_ALL_BEES = true;
   private static final boolean SHOW_HIVE_FOR_ALL_BEES = true;
   private static final boolean SHOW_FLOWER_POS_FOR_ALL_BEES = true;
   private static final boolean SHOW_TRAVEL_TICKS_FOR_ALL_BEES = true;
   private static final boolean SHOW_PATH_FOR_ALL_BEES = false;
   private static final boolean SHOW_GOAL_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_NAME_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_HIVE_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_FLOWER_POS_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_TRAVEL_TICKS_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_PATH_FOR_SELECTED_BEE = true;
   private static final boolean SHOW_HIVE_MEMBERS = true;
   private static final boolean SHOW_BLACKLISTS = true;
   private static final int MAX_RENDER_DIST_FOR_HIVE_OVERLAY = 30;
   private static final int MAX_RENDER_DIST_FOR_BEE_OVERLAY = 30;
   private static final int MAX_TARGETING_DIST = 8;
   private static final int HIVE_TIMEOUT = 20;
   private static final float TEXT_SCALE = 0.02F;
   private static final int ORANGE = -23296;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private final Minecraft minecraft;
   private final Map<BlockPos, HiveDebugInfo> hives = new HashMap();
   private final Map<UUID, BeeDebugPayload.BeeInfo> beeInfosPerEntity = new HashMap();
   @Nullable
   private UUID lastLookedAtUuid;

   public BeeDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void clear() {
      this.hives.clear();
      this.beeInfosPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addOrUpdateHiveInfo(HiveDebugPayload.HiveInfo var1, long var2) {
      this.hives.put(var1.pos(), new HiveDebugInfo(var1, var2));
   }

   public void addOrUpdateBeeInfo(BeeDebugPayload.BeeInfo var1) {
      this.beeInfosPerEntity.put(var1.uuid(), var1);
   }

   public void removeBeeInfo(int var1) {
      this.beeInfosPerEntity.values().removeIf((var1x) -> {
         return var1x.id() == var1;
      });
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      this.clearRemovedHives();
      this.clearRemovedBees();
      this.doRender(var1, var2);
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void clearRemovedBees() {
      this.beeInfosPerEntity.entrySet().removeIf((var1) -> {
         return this.minecraft.level.getEntity(((BeeDebugPayload.BeeInfo)var1.getValue()).id()) == null;
      });
   }

   private void clearRemovedHives() {
      long var1 = this.minecraft.level.getGameTime() - 20L;
      this.hives.entrySet().removeIf((var2) -> {
         return ((HiveDebugInfo)var2.getValue()).lastSeen() < var1;
      });
   }

   private void doRender(PoseStack var1, MultiBufferSource var2) {
      BlockPos var3 = this.getCamera().getBlockPosition();
      this.beeInfosPerEntity.values().forEach((var3x) -> {
         if (this.isPlayerCloseEnoughToMob(var3x)) {
            this.renderBeeInfo(var1, var2, var3x);
         }

      });
      this.renderFlowerInfos(var1, var2);
      Iterator var4 = this.hives.keySet().iterator();

      while(var4.hasNext()) {
         BlockPos var5 = (BlockPos)var4.next();
         if (var3.closerThan(var5, 30.0)) {
            highlightHive(var1, var2, var5);
         }
      }

      Map var6 = this.createHiveBlacklistMap();
      this.hives.values().forEach((var5x) -> {
         if (var3.closerThan(var5x.info.pos(), 30.0)) {
            Set var6x = (Set)var6.get(var5x.info.pos());
            this.renderHiveInfo(var1, var2, var5x.info, (Collection)(var6x == null ? Sets.newHashSet() : var6x));
         }

      });
      this.getGhostHives().forEach((var4x, var5x) -> {
         if (var3.closerThan(var4x, 30.0)) {
            this.renderGhostHive(var1, var2, var4x, var5x);
         }

      });
   }

   private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
      HashMap var1 = Maps.newHashMap();
      this.beeInfosPerEntity.values().forEach((var1x) -> {
         var1x.blacklistedHives().forEach((var2) -> {
            ((Set)var1.computeIfAbsent(var2, (var0) -> {
               return Sets.newHashSet();
            })).add(var1x.uuid());
         });
      });
      return var1;
   }

   private void renderFlowerInfos(PoseStack var1, MultiBufferSource var2) {
      HashMap var3 = Maps.newHashMap();
      this.beeInfosPerEntity.values().forEach((var1x) -> {
         if (var1x.flowerPos() != null) {
            ((Set)var3.computeIfAbsent(var1x.flowerPos(), (var0) -> {
               return new HashSet();
            })).add(var1x.uuid());
         }

      });
      var3.forEach((var2x, var3x) -> {
         Set var4 = (Set)var3x.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
         int var5 = 1;
         renderTextOverPos(var1, var2, var4.toString(), var2x, var5++, -256);
         renderTextOverPos(var1, var2, "Flower", var2x, var5++, -1);
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
      renderTextOverPos(var1, var2, "" + String.valueOf(var4), var3, 0, -256);
      renderTextOverPos(var1, var2, "Ghost Hive", var3, 1, -65536);
   }

   private void renderHiveInfo(PoseStack var1, MultiBufferSource var2, HiveDebugPayload.HiveInfo var3, Collection<UUID> var4) {
      int var5 = 0;
      if (!var4.isEmpty()) {
         renderTextOverHive(var1, var2, "Blacklisted by " + getBeeUuidsAsString(var4), var3, var5++, -65536);
      }

      renderTextOverHive(var1, var2, "Out: " + getBeeUuidsAsString(this.getHiveMembers(var3.pos())), var3, var5++, -3355444);
      if (var3.occupantCount() == 0) {
         renderTextOverHive(var1, var2, "In: -", var3, var5++, -256);
      } else if (var3.occupantCount() == 1) {
         renderTextOverHive(var1, var2, "In: 1 bee", var3, var5++, -256);
      } else {
         renderTextOverHive(var1, var2, "In: " + var3.occupantCount() + " bees", var3, var5++, -256);
      }

      int var6 = var3.honeyLevel();
      renderTextOverHive(var1, var2, "Honey: " + var6, var3, var5++, -23296);
      renderTextOverHive(var1, var2, var3.hiveType() + (var3.sedated() ? " (sedated)" : ""), var3, var5++, -1);
   }

   private void renderPath(PoseStack var1, MultiBufferSource var2, BeeDebugPayload.BeeInfo var3) {
      if (var3.path() != null) {
         PathfindingRenderer.renderPath(var1, var2, var3.path(), 0.5F, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
      }

   }

   private void renderBeeInfo(PoseStack var1, MultiBufferSource var2, BeeDebugPayload.BeeInfo var3) {
      boolean var4 = this.isBeeSelected(var3);
      int var5 = 0;
      renderTextOverMob(var1, var2, var3.pos(), var5++, var3.toString(), -1, 0.03F);
      if (var3.hivePos() == null) {
         renderTextOverMob(var1, var2, var3.pos(), var5++, "No hive", -98404, 0.02F);
      } else {
         renderTextOverMob(var1, var2, var3.pos(), var5++, "Hive: " + this.getPosDescription(var3, var3.hivePos()), -256, 0.02F);
      }

      if (var3.flowerPos() == null) {
         renderTextOverMob(var1, var2, var3.pos(), var5++, "No flower", -98404, 0.02F);
      } else {
         renderTextOverMob(var1, var2, var3.pos(), var5++, "Flower: " + this.getPosDescription(var3, var3.flowerPos()), -256, 0.02F);
      }

      Iterator var6 = var3.goals().iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         renderTextOverMob(var1, var2, var3.pos(), var5++, var7, -16711936, 0.02F);
      }

      if (var4) {
         this.renderPath(var1, var2, var3);
      }

      if (var3.travelTicks() > 0) {
         int var8 = var3.travelTicks() < 2400 ? -3355444 : -23296;
         renderTextOverMob(var1, var2, var3.pos(), var5++, "Travelling: " + var3.travelTicks() + " ticks", var8, 0.02F);
      }

   }

   private static void renderTextOverHive(PoseStack var0, MultiBufferSource var1, String var2, HiveDebugPayload.HiveInfo var3, int var4, int var5) {
      renderTextOverPos(var0, var1, var2, var3.pos(), var4, var5);
   }

   private static void renderTextOverPos(PoseStack var0, MultiBufferSource var1, String var2, BlockPos var3, int var4, int var5) {
      double var6 = 1.3;
      double var8 = 0.2;
      double var10 = (double)var3.getX() + 0.5;
      double var12 = (double)var3.getY() + 1.3 + (double)var4 * 0.2;
      double var14 = (double)var3.getZ() + 0.5;
      DebugRenderer.renderFloatingText(var0, var1, var2, var10, var12, var14, var5, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(PoseStack var0, MultiBufferSource var1, Position var2, int var3, String var4, int var5, float var6) {
      double var7 = 2.4;
      double var9 = 0.25;
      BlockPos var11 = BlockPos.containing(var2);
      double var12 = (double)var11.getX() + 0.5;
      double var14 = var2.y() + 2.4 + (double)var3 * 0.25;
      double var16 = (double)var11.getZ() + 0.5;
      float var18 = 0.5F;
      DebugRenderer.renderFloatingText(var0, var1, var4, var12, var14, var16, var5, var6, false, 0.5F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }

   private Set<String> getHiveMemberNames(HiveDebugPayload.HiveInfo var1) {
      return (Set)this.getHiveMembers(var1.pos()).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private String getPosDescription(BeeDebugPayload.BeeInfo var1, BlockPos var2) {
      double var3 = Math.sqrt(var2.distToCenterSqr(var1.pos()));
      double var5 = (double)Math.round(var3 * 10.0) / 10.0;
      String var10000 = var2.toShortString();
      return var10000 + " (dist " + var5 + ")";
   }

   private boolean isBeeSelected(BeeDebugPayload.BeeInfo var1) {
      return Objects.equals(this.lastLookedAtUuid, var1.uuid());
   }

   private boolean isPlayerCloseEnoughToMob(BeeDebugPayload.BeeInfo var1) {
      LocalPlayer var2 = this.minecraft.player;
      BlockPos var3 = BlockPos.containing(((Player)var2).getX(), var1.pos().y(), ((Player)var2).getZ());
      BlockPos var4 = BlockPos.containing(var1.pos());
      return var3.closerThan(var4, 30.0);
   }

   private Collection<UUID> getHiveMembers(BlockPos var1) {
      return (Collection)this.beeInfosPerEntity.values().stream().filter((var1x) -> {
         return var1x.hasHive(var1);
      }).map(BeeDebugPayload.BeeInfo::uuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostHives() {
      HashMap var1 = Maps.newHashMap();
      Iterator var2 = this.beeInfosPerEntity.values().iterator();

      while(var2.hasNext()) {
         BeeDebugPayload.BeeInfo var3 = (BeeDebugPayload.BeeInfo)var2.next();
         if (var3.hivePos() != null && !this.hives.containsKey(var3.hivePos())) {
            ((List)var1.computeIfAbsent(var3.hivePos(), (var0) -> {
               return Lists.newArrayList();
            })).add(var3.generateName());
         }
      }

      return var1;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((var1) -> {
         this.lastLookedAtUuid = var1.getUUID();
      });
   }

   static record HiveDebugInfo(HiveDebugPayload.HiveInfo info, long lastSeen) {
      final HiveDebugPayload.HiveInfo info;

      HiveDebugInfo(HiveDebugPayload.HiveInfo var1, long var2) {
         super();
         this.info = var1;
         this.lastSeen = var2;
      }

      public HiveDebugPayload.HiveInfo info() {
         return this.info;
      }

      public long lastSeen() {
         return this.lastSeen;
      }
   }
}
