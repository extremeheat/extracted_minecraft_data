package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
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
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
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
   private static final int WHITE = -1;
   private static final int YELLOW = -256;
   private static final int CYAN = -16711681;
   private static final int GREEN = -16711936;
   private static final int GRAY = -3355444;
   private static final int PINK = -98404;
   private static final int RED = -65536;
   private static final int ORANGE = -23296;
   private final Minecraft minecraft;
   private final Map<BlockPos, BrainDebugRenderer.PoiInfo> pois = Maps.newHashMap();
   private final Map<UUID, BrainDebugRenderer.BrainDump> brainDumpsPerEntity = Maps.newHashMap();
   @Nullable
   private UUID lastLookedAtUuid;

   public BrainDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   @Override
   public void clear() {
      this.pois.clear();
      this.brainDumpsPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addPoi(BrainDebugRenderer.PoiInfo var1) {
      this.pois.put(var1.pos, var1);
   }

   public void removePoi(BlockPos var1) {
      this.pois.remove(var1);
   }

   public void setFreeTicketCount(BlockPos var1, int var2) {
      BrainDebugRenderer.PoiInfo var3 = this.pois.get(var1);
      if (var3 == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: {}", var1);
      } else {
         var3.freeTicketCount = var2;
      }
   }

   public void addOrUpdateBrainDump(BrainDebugRenderer.BrainDump var1) {
      this.brainDumpsPerEntity.put(var1.uuid, var1);
   }

   public void removeBrainDump(int var1) {
      this.brainDumpsPerEntity.values().removeIf(var1x -> var1x.id == var1);
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.clearRemovedEntities();
      this.doRender(var3, var5, var7);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }
   }

   private void clearRemovedEntities() {
      this.brainDumpsPerEntity.entrySet().removeIf(var1 -> {
         Entity var2 = this.minecraft.level.getEntity(var1.getValue().id);
         return var2 == null || var2.isRemoved();
      });
   }

   private void doRender(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      this.brainDumpsPerEntity.values().forEach(var7x -> {
         if (this.isPlayerCloseEnoughToMob(var7x)) {
            this.renderBrainInfo(var7x, var1, var3, var5);
         }
      });

      for(BlockPos var9 : this.pois.keySet()) {
         if (var7.closerThan(var9, 30.0)) {
            highlightPoi(var9);
         }
      }

      this.pois.values().forEach(var2 -> {
         if (var7.closerThan(var2.pos, 30.0)) {
            this.renderPoiInfo(var2);
         }
      });
      this.getGhostPois().forEach((var2, var3x) -> {
         if (var7.closerThan(var2, 30.0)) {
            this.renderGhostPoi(var2, var3x);
         }
      });
   }

   private static void highlightPoi(BlockPos var0) {
      float var1 = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(var0, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(BlockPos var1, List<String> var2) {
      float var3 = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(var1, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos(var2 + "", var1, 0, -256);
      renderTextOverPos("Ghost POI", var1, 1, -65536);
   }

   private void renderPoiInfo(BrainDebugRenderer.PoiInfo var1) {
      int var2 = 0;
      Set var3 = this.getTicketHolderNames(var1);
      if (var3.size() < 4) {
         renderTextOverPoi("Owners: " + var3, var1, var2, -256);
      } else {
         renderTextOverPoi(var3.size() + " ticket holders", var1, var2, -256);
      }

      ++var2;
      Set var4 = this.getPotentialTicketHolderNames(var1);
      if (var4.size() < 4) {
         renderTextOverPoi("Candidates: " + var4, var1, var2, -23296);
      } else {
         renderTextOverPoi(var4.size() + " potential owners", var1, var2, -23296);
      }

      renderTextOverPoi("Free tickets: " + var1.freeTicketCount, var1, ++var2, -256);
      renderTextOverPoi(var1.type, var1, ++var2, -1);
   }

   private void renderPath(BrainDebugRenderer.BrainDump var1, double var2, double var4, double var6) {
      if (var1.path != null) {
         PathfindingRenderer.renderPath(var1.path, 0.5F, false, false, var2, var4, var6);
      }
   }

   private void renderBrainInfo(BrainDebugRenderer.BrainDump var1, double var2, double var4, double var6) {
      boolean var8 = this.isMobSelected(var1);
      int var9 = 0;
      renderTextOverMob(var1.pos, var9, var1.name, -1, 0.03F);
      ++var9;
      if (var8) {
         renderTextOverMob(var1.pos, var9, var1.profession + " " + var1.xp + " xp", -1, 0.02F);
         ++var9;
      }

      if (var8) {
         int var10 = var1.health < var1.maxHealth ? -23296 : -1;
         renderTextOverMob(
            var1.pos,
            var9,
            "health: " + String.format(Locale.ROOT, "%.1f", var1.health) + " / " + String.format(Locale.ROOT, "%.1f", var1.maxHealth),
            var10,
            0.02F
         );
         ++var9;
      }

      if (var8 && !var1.inventory.equals("")) {
         renderTextOverMob(var1.pos, var9, var1.inventory, -98404, 0.02F);
         ++var9;
      }

      if (var8) {
         for(String var11 : var1.behaviors) {
            renderTextOverMob(var1.pos, var9, var11, -16711681, 0.02F);
            ++var9;
         }
      }

      if (var8) {
         for(String var17 : var1.activities) {
            renderTextOverMob(var1.pos, var9, var17, -16711936, 0.02F);
            ++var9;
         }
      }

      if (var1.wantsGolem) {
         renderTextOverMob(var1.pos, var9, "Wants Golem", -23296, 0.02F);
         ++var9;
      }

      if (var8 && var1.angerLevel != -1) {
         renderTextOverMob(var1.pos, var9, "Anger Level: " + var1.angerLevel, -98404, 0.02F);
         ++var9;
      }

      if (var8) {
         for(String var18 : var1.gossips) {
            if (var18.startsWith(var1.name)) {
               renderTextOverMob(var1.pos, var9, var18, -1, 0.02F);
            } else {
               renderTextOverMob(var1.pos, var9, var18, -23296, 0.02F);
            }

            ++var9;
         }
      }

      if (var8) {
         for(String var19 : Lists.reverse(var1.memories)) {
            renderTextOverMob(var1.pos, var9, var19, -3355444, 0.02F);
            ++var9;
         }
      }

      if (var8) {
         this.renderPath(var1, var2, var4, var6);
      }
   }

   private static void renderTextOverPoi(String var0, BrainDebugRenderer.PoiInfo var1, int var2, int var3) {
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

   private Set<String> getTicketHolderNames(BrainDebugRenderer.PoiInfo var1) {
      return this.getTicketHolders(var1.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private Set<String> getPotentialTicketHolderNames(BrainDebugRenderer.PoiInfo var1) {
      return this.getPotentialTicketHolders(var1.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
   }

   private boolean isMobSelected(BrainDebugRenderer.BrainDump var1) {
      return Objects.equals(this.lastLookedAtUuid, var1.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(BrainDebugRenderer.BrainDump var1) {
      LocalPlayer var2 = this.minecraft.player;
      BlockPos var3 = new BlockPos(var2.getX(), var1.pos.y(), var2.getZ());
      BlockPos var4 = new BlockPos(var1.pos);
      return var3.closerThan(var4, 30.0);
   }

   private Collection<UUID> getTicketHolders(BlockPos var1) {
      return this.brainDumpsPerEntity
         .values()
         .stream()
         .filter(var1x -> var1x.hasPoi(var1))
         .map(BrainDebugRenderer.BrainDump::getUuid)
         .collect(Collectors.toSet());
   }

   private Collection<UUID> getPotentialTicketHolders(BlockPos var1) {
      return this.brainDumpsPerEntity
         .values()
         .stream()
         .filter(var1x -> var1x.hasPotentialPoi(var1))
         .map(BrainDebugRenderer.BrainDump::getUuid)
         .collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostPois() {
      HashMap var1 = Maps.newHashMap();

      for(BrainDebugRenderer.BrainDump var3 : this.brainDumpsPerEntity.values()) {
         for(BlockPos var5 : Iterables.concat(var3.pois, var3.potentialPois)) {
            if (!this.pois.containsKey(var5)) {
               var1.computeIfAbsent(var5, var0 -> Lists.newArrayList()).add(var3.name);
            }
         }
      }

      return var1;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent(var1 -> this.lastLookedAtUuid = var1.getUUID());
   }

   public static class BrainDump {
      public final UUID uuid;
      public final int id;
      public final String name;
      public final String profession;
      public final int xp;
      public final float health;
      public final float maxHealth;
      public final Position pos;
      public final String inventory;
      public final Path path;
      public final boolean wantsGolem;
      public final int angerLevel;
      public final List<String> activities = Lists.newArrayList();
      public final List<String> behaviors = Lists.newArrayList();
      public final List<String> memories = Lists.newArrayList();
      public final List<String> gossips = Lists.newArrayList();
      public final Set<BlockPos> pois = Sets.newHashSet();
      public final Set<BlockPos> potentialPois = Sets.newHashSet();

      public BrainDump(
         UUID var1,
         int var2,
         String var3,
         String var4,
         int var5,
         float var6,
         float var7,
         Position var8,
         String var9,
         @Nullable Path var10,
         boolean var11,
         int var12
      ) {
         super();
         this.uuid = var1;
         this.id = var2;
         this.name = var3;
         this.profession = var4;
         this.xp = var5;
         this.health = var6;
         this.maxHealth = var7;
         this.pos = var8;
         this.inventory = var9;
         this.path = var10;
         this.wantsGolem = var11;
         this.angerLevel = var12;
      }

      boolean hasPoi(BlockPos var1) {
         return this.pois.stream().anyMatch(var1::equals);
      }

      boolean hasPotentialPoi(BlockPos var1) {
         return this.potentialPois.contains(var1);
      }

      public UUID getUuid() {
         return this.uuid;
      }
   }

   public static class PoiInfo {
      public final BlockPos pos;
      public String type;
      public int freeTicketCount;

      public PoiInfo(BlockPos var1, String var2, int var3) {
         super();
         this.pos = var1;
         this.type = var2;
         this.freeTicketCount = var3;
      }
   }
}
