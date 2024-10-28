package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class BrainDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean SHOW_NAME_FOR_ALL = true;
   private static final boolean SHOW_PROFESSION_FOR_ALL = false;
   private static final boolean SHOW_BEHAVIORS_FOR_ALL = false;
   private static final boolean SHOW_ACTIVITIES_FOR_ALL = false;
   private static final boolean SHOW_INVENTORY_FOR_ALL = false;
   private static final boolean SHOW_GOSSIPS_FOR_ALL = false;
   private static final boolean SHOW_PATH_FOR_ALL = false;
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
   private static final boolean SHOW_PATH_FOR_SELECTED = true;
   private static final boolean SHOW_HEALTH_FOR_SELECTED = true;
   private static final boolean SHOW_WANTS_GOLEM_FOR_SELECTED = true;
   private static final boolean SHOW_ANGER_LEVEL_FOR_SELECTED = true;
   private static final boolean SHOW_POI_INFO = true;
   private static final int MAX_RENDER_DIST_FOR_BRAIN_INFO = 30;
   private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
   private static final int MAX_TARGETING_DIST = 8;
   private static final float TEXT_SCALE = 0.02F;
   private static final int CYAN = -16711681;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private static final int ORANGE = -23296;
   private final Minecraft minecraft;
   private final Map<BlockPos, PoiInfo> pois = Maps.newHashMap();
   private final Map<UUID, BrainDebugPayload.BrainDump> brainDumpsPerEntity = Maps.newHashMap();
   @Nullable
   private UUID lastLookedAtUuid;

   public BrainDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void clear() {
      this.pois.clear();
      this.brainDumpsPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addPoi(PoiInfo var1) {
      this.pois.put(var1.pos, var1);
   }

   public void removePoi(BlockPos var1) {
      this.pois.remove(var1);
   }

   public void setFreeTicketCount(BlockPos var1, int var2) {
      PoiInfo var3 = (PoiInfo)this.pois.get(var1);
      if (var3 == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", var1);
      } else {
         var3.freeTicketCount = var2;
      }
   }

   public void addOrUpdateBrainDump(BrainDebugPayload.BrainDump var1) {
      this.brainDumpsPerEntity.put(var1.uuid(), var1);
   }

   public void removeBrainDump(int var1) {
      this.brainDumpsPerEntity.values().removeIf((var1x) -> {
         return var1x.id() == var1;
      });
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      this.clearRemovedEntities();
      this.doRender(var1, var2, var3, var5, var7);
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void clearRemovedEntities() {
      this.brainDumpsPerEntity.entrySet().removeIf((var1) -> {
         Entity var2 = this.minecraft.level.getEntity(((BrainDebugPayload.BrainDump)var1.getValue()).id());
         return var2 == null || var2.isRemoved();
      });
   }

   private void doRender(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      BlockPos var9 = BlockPos.containing(var3, var5, var7);
      this.brainDumpsPerEntity.values().forEach((var9x) -> {
         if (this.isPlayerCloseEnoughToMob(var9x)) {
            this.renderBrainInfo(var1, var2, var9x, var3, var5, var7);
         }

      });
      Iterator var10 = this.pois.keySet().iterator();

      while(var10.hasNext()) {
         BlockPos var11 = (BlockPos)var10.next();
         if (var9.closerThan(var11, 30.0)) {
            highlightPoi(var1, var2, var11);
         }
      }

      this.pois.values().forEach((var4) -> {
         if (var9.closerThan(var4.pos, 30.0)) {
            this.renderPoiInfo(var1, var2, var4);
         }

      });
      this.getGhostPois().forEach((var4, var5x) -> {
         if (var9.closerThan(var4, 30.0)) {
            this.renderGhostPoi(var1, var2, var4, var5x);
         }

      });
   }

   private static void highlightPoi(PoseStack var0, MultiBufferSource var1, BlockPos var2) {
      float var3 = 0.05F;
      DebugRenderer.renderFilledBox(var0, var1, var2, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(PoseStack var1, MultiBufferSource var2, BlockPos var3, List<String> var4) {
      float var5 = 0.05F;
      DebugRenderer.renderFilledBox(var1, var2, var3, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos(var1, var2, "" + String.valueOf(var4), var3, 0, -256);
      renderTextOverPos(var1, var2, "Ghost POI", var3, 1, -65536);
   }

   private void renderPoiInfo(PoseStack var1, MultiBufferSource var2, PoiInfo var3) {
      int var4 = 0;
      Set var5 = this.getTicketHolderNames(var3);
      if (var5.size() < 4) {
         renderTextOverPoi(var1, var2, "Owners: " + String.valueOf(var5), var3, var4, -256);
      } else {
         renderTextOverPoi(var1, var2, var5.size() + " ticket holders", var3, var4, -256);
      }

      ++var4;
      Set var6 = this.getPotentialTicketHolderNames(var3);
      if (var6.size() < 4) {
         renderTextOverPoi(var1, var2, "Candidates: " + String.valueOf(var6), var3, var4, -23296);
      } else {
         renderTextOverPoi(var1, var2, var6.size() + " potential owners", var3, var4, -23296);
      }

      ++var4;
      renderTextOverPoi(var1, var2, "Free tickets: " + var3.freeTicketCount, var3, var4, -256);
      ++var4;
      renderTextOverPoi(var1, var2, var3.type, var3, var4, -1);
   }

   private void renderPath(PoseStack var1, MultiBufferSource var2, BrainDebugPayload.BrainDump var3, double var4, double var6, double var8) {
      if (var3.path() != null) {
         PathfindingRenderer.renderPath(var1, var2, var3.path(), 0.5F, false, false, var4, var6, var8);
      }

   }

   private void renderBrainInfo(PoseStack var1, MultiBufferSource var2, BrainDebugPayload.BrainDump var3, double var4, double var6, double var8) {
      boolean var10 = this.isMobSelected(var3);
      int var11 = 0;
      renderTextOverMob(var1, var2, var3.pos(), var11, var3.name(), -1, 0.03F);
      ++var11;
      if (var10) {
         renderTextOverMob(var1, var2, var3.pos(), var11, var3.profession() + " " + var3.xp() + " xp", -1, 0.02F);
         ++var11;
      }

      if (var10) {
         int var12 = var3.health() < var3.maxHealth() ? -23296 : -1;
         Vec3 var10002 = var3.pos();
         String var10004 = String.format(Locale.ROOT, "%.1f", var3.health());
         renderTextOverMob(var1, var2, var10002, var11, "health: " + var10004 + " / " + String.format(Locale.ROOT, "%.1f", var3.maxHealth()), var12, 0.02F);
         ++var11;
      }

      if (var10 && !var3.inventory().equals("")) {
         renderTextOverMob(var1, var2, var3.pos(), var11, var3.inventory(), -98404, 0.02F);
         ++var11;
      }

      String var13;
      Iterator var14;
      if (var10) {
         for(var14 = var3.behaviors().iterator(); var14.hasNext(); ++var11) {
            var13 = (String)var14.next();
            renderTextOverMob(var1, var2, var3.pos(), var11, var13, -16711681, 0.02F);
         }
      }

      if (var10) {
         for(var14 = var3.activities().iterator(); var14.hasNext(); ++var11) {
            var13 = (String)var14.next();
            renderTextOverMob(var1, var2, var3.pos(), var11, var13, -16711936, 0.02F);
         }
      }

      if (var3.wantsGolem()) {
         renderTextOverMob(var1, var2, var3.pos(), var11, "Wants Golem", -23296, 0.02F);
         ++var11;
      }

      if (var10 && var3.angerLevel() != -1) {
         renderTextOverMob(var1, var2, var3.pos(), var11, "Anger Level: " + var3.angerLevel(), -98404, 0.02F);
         ++var11;
      }

      if (var10) {
         for(var14 = var3.gossips().iterator(); var14.hasNext(); ++var11) {
            var13 = (String)var14.next();
            if (var13.startsWith(var3.name())) {
               renderTextOverMob(var1, var2, var3.pos(), var11, var13, -1, 0.02F);
            } else {
               renderTextOverMob(var1, var2, var3.pos(), var11, var13, -23296, 0.02F);
            }
         }
      }

      if (var10) {
         for(var14 = Lists.reverse(var3.memories()).iterator(); var14.hasNext(); ++var11) {
            var13 = (String)var14.next();
            renderTextOverMob(var1, var2, var3.pos(), var11, var13, -3355444, 0.02F);
         }
      }

      if (var10) {
         this.renderPath(var1, var2, var3, var4, var6, var8);
      }

   }

   private static void renderTextOverPoi(PoseStack var0, MultiBufferSource var1, String var2, PoiInfo var3, int var4, int var5) {
      renderTextOverPos(var0, var1, var2, var3.pos, var4, var5);
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

   private Set<String> getTicketHolderNames(PoiInfo var1) {
      return (Set)this.getTicketHolders(var1.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private Set<String> getPotentialTicketHolderNames(PoiInfo var1) {
      return (Set)this.getPotentialTicketHolders(var1.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private boolean isMobSelected(BrainDebugPayload.BrainDump var1) {
      return Objects.equals(this.lastLookedAtUuid, var1.uuid());
   }

   private boolean isPlayerCloseEnoughToMob(BrainDebugPayload.BrainDump var1) {
      LocalPlayer var2 = this.minecraft.player;
      BlockPos var3 = BlockPos.containing(((Player)var2).getX(), var1.pos().y(), ((Player)var2).getZ());
      BlockPos var4 = BlockPos.containing(var1.pos());
      return var3.closerThan(var4, 30.0);
   }

   private Collection<UUID> getTicketHolders(BlockPos var1) {
      return (Collection)this.brainDumpsPerEntity.values().stream().filter((var1x) -> {
         return var1x.hasPoi(var1);
      }).map(BrainDebugPayload.BrainDump::uuid).collect(Collectors.toSet());
   }

   private Collection<UUID> getPotentialTicketHolders(BlockPos var1) {
      return (Collection)this.brainDumpsPerEntity.values().stream().filter((var1x) -> {
         return var1x.hasPotentialPoi(var1);
      }).map(BrainDebugPayload.BrainDump::uuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostPois() {
      HashMap var1 = Maps.newHashMap();
      Iterator var2 = this.brainDumpsPerEntity.values().iterator();

      while(var2.hasNext()) {
         BrainDebugPayload.BrainDump var3 = (BrainDebugPayload.BrainDump)var2.next();
         Iterator var4 = Iterables.concat(var3.pois(), var3.potentialPois()).iterator();

         while(var4.hasNext()) {
            BlockPos var5 = (BlockPos)var4.next();
            if (!this.pois.containsKey(var5)) {
               ((List)var1.computeIfAbsent(var5, (var0) -> {
                  return Lists.newArrayList();
               })).add(var3.name());
            }
         }
      }

      return var1;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((var1) -> {
         this.lastLookedAtUuid = var1.getUUID();
      });
   }

   public static class PoiInfo {
      public final BlockPos pos;
      public final String type;
      public int freeTicketCount;

      public PoiInfo(BlockPos var1, String var2, int var3) {
         super();
         this.pos = var1;
         this.type = var2;
         this.freeTicketCount = var3;
      }
   }
}
