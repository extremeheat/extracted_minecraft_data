package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.DebugMobNameGenerator;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillageDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final Map pois = Maps.newHashMap();
   private final Set villageSections = Sets.newHashSet();
   private final Map brainDumpsPerEntity = Maps.newHashMap();
   private UUID lastLookedAtUuid;

   public VillageDebugRenderer(Minecraft var1) {
      this.minecraft = var1;
   }

   public void clear() {
      this.pois.clear();
      this.villageSections.clear();
      this.brainDumpsPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addPoi(VillageDebugRenderer.PoiInfo var1) {
      this.pois.put(var1.pos, var1);
   }

   public void removePoi(BlockPos var1) {
      this.pois.remove(var1);
   }

   public void setFreeTicketCount(BlockPos var1, int var2) {
      VillageDebugRenderer.PoiInfo var3 = (VillageDebugRenderer.PoiInfo)this.pois.get(var1);
      if (var3 == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + var1);
      } else {
         var3.freeTicketCount = var2;
      }
   }

   public void setVillageSection(SectionPos var1) {
      this.villageSections.add(var1);
   }

   public void setNotVillageSection(SectionPos var1) {
      this.villageSections.remove(var1);
   }

   public void addOrUpdateBrainDump(VillageDebugRenderer.BrainDump var1) {
      this.brainDumpsPerEntity.put(var1.uuid, var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.doRender(var3, var5, var7);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void doRender(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      this.villageSections.forEach((var1x) -> {
         if (var7.closerThan(var1x.center(), 60.0D)) {
            highlightVillageSection(var1x);
         }

      });
      this.brainDumpsPerEntity.values().forEach((var7x) -> {
         if (this.isPlayerCloseEnoughToMob(var7x)) {
            this.renderVillagerInfo(var7x, var1, var3, var5);
         }

      });
      Iterator var8 = this.pois.keySet().iterator();

      while(var8.hasNext()) {
         BlockPos var9 = (BlockPos)var8.next();
         if (var7.closerThan(var9, 30.0D)) {
            highlightPoi(var9);
         }
      }

      this.pois.values().forEach((var2) -> {
         if (var7.closerThan(var2.pos, 30.0D)) {
            this.renderPoiInfo(var2);
         }

      });
      this.getGhostPois().forEach((var2, var3x) -> {
         if (var7.closerThan(var2, 30.0D)) {
            this.renderGhostPoi(var2, var3x);
         }

      });
   }

   private static void highlightVillageSection(SectionPos var0) {
      float var1 = 1.0F;
      BlockPos var2 = var0.center();
      BlockPos var3 = var2.offset(-1.0D, -1.0D, -1.0D);
      BlockPos var4 = var2.offset(1.0D, 1.0D, 1.0D);
      DebugRenderer.renderFilledBox(var3, var4, 0.2F, 1.0F, 0.2F, 0.15F);
   }

   private static void highlightPoi(BlockPos var0) {
      float var1 = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(var0, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(BlockPos var1, List var2) {
      float var3 = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(var1, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos("" + var2, var1, 0, -256);
      renderTextOverPos("Ghost POI", var1, 1, -65536);
   }

   private void renderPoiInfo(VillageDebugRenderer.PoiInfo var1) {
      byte var2 = 0;
      if (this.getTicketHolderNames(var1).size() < 4) {
         renderTextOverPoi("" + this.getTicketHolderNames(var1), var1, var2, -256);
      } else {
         renderTextOverPoi("" + this.getTicketHolderNames(var1).size() + " ticket holders", var1, var2, -256);
      }

      int var3 = var2 + 1;
      renderTextOverPoi("Free tickets: " + var1.freeTicketCount, var1, var3, -256);
      ++var3;
      renderTextOverPoi(var1.type, var1, var3, -1);
   }

   private void renderPath(VillageDebugRenderer.BrainDump var1, double var2, double var4, double var6) {
      if (var1.path != null) {
         PathfindingRenderer.renderPath(var1.path, 0.5F, false, false, var2, var4, var6);
      }

   }

   private void renderVillagerInfo(VillageDebugRenderer.BrainDump var1, double var2, double var4, double var6) {
      boolean var8 = this.isVillagerSelected(var1);
      byte var9 = 0;
      renderTextOverMob(var1.pos, var9, var1.name, -1, 0.03F);
      int var12 = var9 + 1;
      if (var8) {
         renderTextOverMob(var1.pos, var12, var1.profession + " " + var1.xp + "xp", -1, 0.02F);
         ++var12;
      }

      if (var8 && !var1.inventory.equals("")) {
         renderTextOverMob(var1.pos, var12, var1.inventory, -98404, 0.02F);
         ++var12;
      }

      Iterator var10;
      String var11;
      if (var8) {
         for(var10 = var1.behaviors.iterator(); var10.hasNext(); ++var12) {
            var11 = (String)var10.next();
            renderTextOverMob(var1.pos, var12, var11, -16711681, 0.02F);
         }
      }

      if (var8) {
         for(var10 = var1.activities.iterator(); var10.hasNext(); ++var12) {
            var11 = (String)var10.next();
            renderTextOverMob(var1.pos, var12, var11, -16711936, 0.02F);
         }
      }

      if (var1.wantsGolem) {
         renderTextOverMob(var1.pos, var12, "Wants Golem", -23296, 0.02F);
         ++var12;
      }

      if (var8) {
         for(var10 = var1.gossips.iterator(); var10.hasNext(); ++var12) {
            var11 = (String)var10.next();
            if (var11.startsWith(var1.name)) {
               renderTextOverMob(var1.pos, var12, var11, -1, 0.02F);
            } else {
               renderTextOverMob(var1.pos, var12, var11, -23296, 0.02F);
            }
         }
      }

      if (var8) {
         for(var10 = Lists.reverse(var1.memories).iterator(); var10.hasNext(); ++var12) {
            var11 = (String)var10.next();
            renderTextOverMob(var1.pos, var12, var11, -3355444, 0.02F);
         }
      }

      if (var8) {
         this.renderPath(var1, var2, var4, var6);
      }

   }

   private static void renderTextOverPoi(String var0, VillageDebugRenderer.PoiInfo var1, int var2, int var3) {
      BlockPos var4 = var1.pos;
      renderTextOverPos(var0, var4, var2, var3);
   }

   private static void renderTextOverPos(String var0, BlockPos var1, int var2, int var3) {
      double var4 = 1.3D;
      double var6 = 0.2D;
      double var8 = (double)var1.getX() + 0.5D;
      double var10 = (double)var1.getY() + 1.3D + (double)var2 * 0.2D;
      double var12 = (double)var1.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(var0, var8, var10, var12, var3, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(Position var0, int var1, String var2, int var3, float var4) {
      double var5 = 2.4D;
      double var7 = 0.25D;
      BlockPos var9 = new BlockPos(var0);
      double var10 = (double)var9.getX() + 0.5D;
      double var12 = var0.y() + 2.4D + (double)var1 * 0.25D;
      double var14 = (double)var9.getZ() + 0.5D;
      float var16 = 0.5F;
      DebugRenderer.renderFloatingText(var2, var10, var12, var14, var3, var4, false, 0.5F, true);
   }

   private Set getTicketHolderNames(VillageDebugRenderer.PoiInfo var1) {
      return (Set)this.getTicketHolders(var1.pos).stream().map(DebugMobNameGenerator::getMobName).collect(Collectors.toSet());
   }

   private boolean isVillagerSelected(VillageDebugRenderer.BrainDump var1) {
      return Objects.equals(this.lastLookedAtUuid, var1.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(VillageDebugRenderer.BrainDump var1) {
      LocalPlayer var2 = this.minecraft.player;
      BlockPos var3 = new BlockPos(var2.getX(), var1.pos.y(), var2.getZ());
      BlockPos var4 = new BlockPos(var1.pos);
      return var3.closerThan(var4, 30.0D);
   }

   private Collection getTicketHolders(BlockPos var1) {
      return (Collection)this.brainDumpsPerEntity.values().stream().filter((var1x) -> {
         return var1x.hasPoi(var1);
      }).map(VillageDebugRenderer.BrainDump::getUuid).collect(Collectors.toSet());
   }

   private Map getGhostPois() {
      HashMap var1 = Maps.newHashMap();
      Iterator var2 = this.brainDumpsPerEntity.values().iterator();

      while(var2.hasNext()) {
         VillageDebugRenderer.BrainDump var3 = (VillageDebugRenderer.BrainDump)var2.next();
         Iterator var4 = var3.pois.iterator();

         while(var4.hasNext()) {
            BlockPos var5 = (BlockPos)var4.next();
            if (!this.pois.containsKey(var5)) {
               Object var6 = (List)var1.get(var5);
               if (var6 == null) {
                  var6 = Lists.newArrayList();
                  var1.put(var5, var6);
               }

               ((List)var6).add(var3.name);
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

   public static class BrainDump {
      public final UUID uuid;
      public final int id;
      public final String name;
      public final String profession;
      public final int xp;
      public final Position pos;
      public final String inventory;
      public final Path path;
      public final boolean wantsGolem;
      public final List activities = Lists.newArrayList();
      public final List behaviors = Lists.newArrayList();
      public final List memories = Lists.newArrayList();
      public final List gossips = Lists.newArrayList();
      public final Set pois = Sets.newHashSet();

      public BrainDump(UUID var1, int var2, String var3, String var4, int var5, Position var6, String var7, @Nullable Path var8, boolean var9) {
         this.uuid = var1;
         this.id = var2;
         this.name = var3;
         this.profession = var4;
         this.xp = var5;
         this.pos = var6;
         this.inventory = var7;
         this.path = var8;
         this.wantsGolem = var9;
      }

      private boolean hasPoi(BlockPos var1) {
         Stream var10000 = this.pois.stream();
         var1.getClass();
         return var10000.anyMatch(var1::equals);
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
         this.pos = var1;
         this.type = var2;
         this.freeTicketCount = var3;
      }
   }
}
