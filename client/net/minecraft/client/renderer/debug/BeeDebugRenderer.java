package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.HashMap;
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
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.level.pathfinder.Path;

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
   private static final int WHITE = -1;
   private static final int YELLOW = -256;
   private static final int ORANGE = -23296;
   private static final int GREEN = -16711936;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private static final int RED = -65536;
   private final Minecraft minecraft;
   private final Map<BlockPos, BeeDebugRenderer.HiveInfo> hives = Maps.newHashMap();
   private final Map<UUID, BeeDebugRenderer.BeeInfo> beeInfosPerEntity = Maps.newHashMap();
   private UUID lastLookedAtUuid;

   public BeeDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   @Override
   public void clear() {
      this.hives.clear();
      this.beeInfosPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addOrUpdateHiveInfo(BeeDebugRenderer.HiveInfo var1) {
      this.hives.put(var1.pos, var1);
   }

   public void addOrUpdateBeeInfo(BeeDebugRenderer.BeeInfo var1) {
      this.beeInfosPerEntity.put(var1.uuid, var1);
   }

   public void removeBeeInfo(int var1) {
      this.beeInfosPerEntity.values().removeIf(var1x -> var1x.id == var1);
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      this.clearRemovedHives();
      this.clearRemovedBees();
      this.doRender();
      RenderSystem.disableBlend();
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }
   }

   private void clearRemovedBees() {
      this.beeInfosPerEntity.entrySet().removeIf(var1 -> this.minecraft.level.getEntity(var1.getValue().id) == null);
   }

   private void clearRemovedHives() {
      long var1 = this.minecraft.level.getGameTime() - 20L;
      this.hives.entrySet().removeIf(var2 -> var2.getValue().lastSeen < var1);
   }

   private void doRender() {
      BlockPos var1 = this.getCamera().getBlockPosition();
      this.beeInfosPerEntity.values().forEach(var1x -> {
         if (this.isPlayerCloseEnoughToMob(var1x)) {
            this.renderBeeInfo(var1x);
         }
      });
      this.renderFlowerInfos();

      for(BlockPos var3 : this.hives.keySet()) {
         if (var1.closerThan(var3, 30.0)) {
            highlightHive(var3);
         }
      }

      Map var4 = this.createHiveBlacklistMap();
      this.hives.values().forEach(var3x -> {
         if (var1.closerThan(var3x.pos, 30.0)) {
            Set var4x = (Set)var4.get(var3x.pos);
            this.renderHiveInfo(var3x, (Collection<UUID>)(var4x == null ? Sets.newHashSet() : var4x));
         }
      });
      this.getGhostHives().forEach((var2, var3x) -> {
         if (var1.closerThan(var2, 30.0)) {
            this.renderGhostHive(var2, var3x);
         }
      });
   }

   private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
      HashMap var1 = Maps.newHashMap();
      this.beeInfosPerEntity
         .values()
         .forEach(var1x -> var1x.blacklistedHives.forEach(var2 -> var1.computeIfAbsent(var2, var0xx -> Sets.newHashSet()).add(var1x.getUuid())));
      return var1;
   }

   private void renderFlowerInfos() {
      HashMap var1 = Maps.newHashMap();
      this.beeInfosPerEntity
         .values()
         .stream()
         .filter(BeeDebugRenderer.BeeInfo::hasFlower)
         .forEach(var1x -> var1.computeIfAbsent(var1x.flowerPos, var0x -> Sets.newHashSet()).add(var1x.getUuid()));
      var1.entrySet().forEach(var0 -> {
         BlockPos var1x = (BlockPos)var0.getKey();
         Set var2 = (Set)var0.getValue();
         Set var3 = var2.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
         int var4 = 1;
         renderTextOverPos(var3.toString(), var1x, var4++, -256);
         renderTextOverPos("Flower", var1x, var4++, -1);
         float var5 = 0.05F;
         renderTransparentFilledBox(var1x, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
      });
   }

   private static String getBeeUuidsAsString(Collection<UUID> var0) {
      if (var0.isEmpty()) {
         return "-";
      } else {
         return var0.size() > 3 ? var0.size() + " bees" : var0.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet()).toString();
      }
   }

   private static void highlightHive(BlockPos var0) {
      float var1 = 0.05F;
      renderTransparentFilledBox(var0, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostHive(BlockPos var1, List<String> var2) {
      float var3 = 0.05F;
      renderTransparentFilledBox(var1, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos(var2 + "", var1, 0, -256);
      renderTextOverPos("Ghost Hive", var1, 1, -65536);
   }

   private static void renderTransparentFilledBox(BlockPos var0, float var1, float var2, float var3, float var4, float var5) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(var0, var1, var2, var3, var4, var5);
   }

   private void renderHiveInfo(BeeDebugRenderer.HiveInfo var1, Collection<UUID> var2) {
      int var3 = 0;
      if (!var2.isEmpty()) {
         renderTextOverHive("Blacklisted by " + getBeeUuidsAsString(var2), var1, var3++, -65536);
      }

      renderTextOverHive("Out: " + getBeeUuidsAsString(this.getHiveMembers(var1.pos)), var1, var3++, -3355444);
      if (var1.occupantCount == 0) {
         renderTextOverHive("In: -", var1, var3++, -256);
      } else if (var1.occupantCount == 1) {
         renderTextOverHive("In: 1 bee", var1, var3++, -256);
      } else {
         renderTextOverHive("In: " + var1.occupantCount + " bees", var1, var3++, -256);
      }

      renderTextOverHive("Honey: " + var1.honeyLevel, var1, var3++, -23296);
      renderTextOverHive(var1.hiveType + (var1.sedated ? " (sedated)" : ""), var1, var3++, -1);
   }

   private void renderPath(BeeDebugRenderer.BeeInfo var1) {
      if (var1.path != null) {
         PathfindingRenderer.renderPath(
            var1.path, 0.5F, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z()
         );
      }
   }

   private void renderBeeInfo(BeeDebugRenderer.BeeInfo var1) {
      boolean var2 = this.isBeeSelected(var1);
      int var3 = 0;
      renderTextOverMob(var1.pos, var3++, var1.toString(), -1, 0.03F);
      if (var1.hivePos == null) {
         renderTextOverMob(var1.pos, var3++, "No hive", -98404, 0.02F);
      } else {
         renderTextOverMob(var1.pos, var3++, "Hive: " + this.getPosDescription(var1, var1.hivePos), -256, 0.02F);
      }

      if (var1.flowerPos == null) {
         renderTextOverMob(var1.pos, var3++, "No flower", -98404, 0.02F);
      } else {
         renderTextOverMob(var1.pos, var3++, "Flower: " + this.getPosDescription(var1, var1.flowerPos), -256, 0.02F);
      }

      for(String var5 : var1.goals) {
         renderTextOverMob(var1.pos, var3++, var5, -16711936, 0.02F);
      }

      if (var2) {
         this.renderPath(var1);
      }

      if (var1.travelTicks > 0) {
         int var10 = var1.travelTicks < 600 ? -3355444 : -23296;
         renderTextOverMob(var1.pos, var3++, "Travelling: " + var1.travelTicks + " ticks", var10, 0.02F);
      }
   }

   private static void renderTextOverHive(String var0, BeeDebugRenderer.HiveInfo var1, int var2, int var3) {
      BlockPos var4 = var1.pos;
      renderTextOverPos(var0, var4, var2, var3);
   }

   private static void renderTextOverPos(String var0, BlockPos var1, int var2, int var3) {
      double var4 = 1.3;
      double var6 = 0.2;
      double var8 = (double)var1.getX() + 0.5;
      double var10 = (double)var1.getY() + 1.3 + (double)var2 * 0.2;
      double var12 = (double)var1.getZ() + 0.5;
      DebugRenderer.renderFloatingText(var0, var8, var10, var12, var3, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(Position var0, int var1, String var2, int var3, float var4) {
      double var5 = 2.4;
      double var7 = 0.25;
      BlockPos var9 = new BlockPos(var0);
      double var10 = (double)var9.getX() + 0.5;
      double var12 = var0.y() + 2.4 + (double)var1 * 0.25;
      double var14 = (double)var9.getZ() + 0.5;
      float var16 = 0.5F;
      DebugRenderer.renderFloatingText(var2, var10, var12, var14, var3, var4, false, 0.5F, true);
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }

   private Set<String> getHiveMemberNames(BeeDebugRenderer.HiveInfo var1) {
      return this.getHiveMembers(var1.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private String getPosDescription(BeeDebugRenderer.BeeInfo var1, BlockPos var2) {
      double var3 = Math.sqrt(var2.distToCenterSqr(var1.pos));
      double var5 = (double)Math.round(var3 * 10.0) / 10.0;
      return var2.toShortString() + " (dist " + var5 + ")";
   }

   private boolean isBeeSelected(BeeDebugRenderer.BeeInfo var1) {
      return Objects.equals(this.lastLookedAtUuid, var1.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(BeeDebugRenderer.BeeInfo var1) {
      LocalPlayer var2 = this.minecraft.player;
      BlockPos var3 = new BlockPos(var2.getX(), var1.pos.y(), var2.getZ());
      BlockPos var4 = new BlockPos(var1.pos);
      return var3.closerThan(var4, 30.0);
   }

   private Collection<UUID> getHiveMembers(BlockPos var1) {
      return this.beeInfosPerEntity.values().stream().filter(var1x -> var1x.hasHive(var1)).map(BeeDebugRenderer.BeeInfo::getUuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostHives() {
      HashMap var1 = Maps.newHashMap();

      for(BeeDebugRenderer.BeeInfo var3 : this.beeInfosPerEntity.values()) {
         if (var3.hivePos != null && !this.hives.containsKey(var3.hivePos)) {
            var1.computeIfAbsent(var3.hivePos, var0 -> Lists.newArrayList()).add(var3.getName());
         }
      }

      return var1;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent(var1 -> this.lastLookedAtUuid = var1.getUUID());
   }

   public static class BeeInfo {
      public final UUID uuid;
      public final int id;
      public final Position pos;
      @Nullable
      public final Path path;
      @Nullable
      public final BlockPos hivePos;
      @Nullable
      public final BlockPos flowerPos;
      public final int travelTicks;
      public final List<String> goals = Lists.newArrayList();
      public final Set<BlockPos> blacklistedHives = Sets.newHashSet();

      public BeeInfo(UUID var1, int var2, Position var3, @Nullable Path var4, @Nullable BlockPos var5, @Nullable BlockPos var6, int var7) {
         super();
         this.uuid = var1;
         this.id = var2;
         this.pos = var3;
         this.path = var4;
         this.hivePos = var5;
         this.flowerPos = var6;
         this.travelTicks = var7;
      }

      public boolean hasHive(BlockPos var1) {
         return this.hivePos != null && this.hivePos.equals(var1);
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public String getName() {
         return DebugEntityNameGenerator.getEntityName(this.uuid);
      }

      @Override
      public String toString() {
         return this.getName();
      }

      public boolean hasFlower() {
         return this.flowerPos != null;
      }
   }

   public static class HiveInfo {
      public final BlockPos pos;
      public final String hiveType;
      public final int occupantCount;
      public final int honeyLevel;
      public final boolean sedated;
      public final long lastSeen;

      public HiveInfo(BlockPos var1, String var2, int var3, int var4, boolean var5, long var6) {
         super();
         this.pos = var1;
         this.hiveType = var2;
         this.occupantCount = var3;
         this.honeyLevel = var4;
         this.sedated = var5;
         this.lastSeen = var6;
      }
   }
}
