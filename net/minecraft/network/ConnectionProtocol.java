package net.minecraft.network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundAddPaintingPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerAckPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquippedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerAckPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import org.apache.logging.log4j.LogManager;

public enum ConnectionProtocol {
   HANDSHAKING(-1, protocol().addFlow(PacketFlow.SERVERBOUND, (new ConnectionProtocol.PacketSet()).addPacket(ClientIntentionPacket.class, ClientIntentionPacket::new))),
   PLAY(0, protocol().addFlow(PacketFlow.CLIENTBOUND, (new ConnectionProtocol.PacketSet()).addPacket(ClientboundAddEntityPacket.class, ClientboundAddEntityPacket::new).addPacket(ClientboundAddExperienceOrbPacket.class, ClientboundAddExperienceOrbPacket::new).addPacket(ClientboundAddGlobalEntityPacket.class, ClientboundAddGlobalEntityPacket::new).addPacket(ClientboundAddMobPacket.class, ClientboundAddMobPacket::new).addPacket(ClientboundAddPaintingPacket.class, ClientboundAddPaintingPacket::new).addPacket(ClientboundAddPlayerPacket.class, ClientboundAddPlayerPacket::new).addPacket(ClientboundAnimatePacket.class, ClientboundAnimatePacket::new).addPacket(ClientboundAwardStatsPacket.class, ClientboundAwardStatsPacket::new).addPacket(ClientboundBlockBreakAckPacket.class, ClientboundBlockBreakAckPacket::new).addPacket(ClientboundBlockDestructionPacket.class, ClientboundBlockDestructionPacket::new).addPacket(ClientboundBlockEntityDataPacket.class, ClientboundBlockEntityDataPacket::new).addPacket(ClientboundBlockEventPacket.class, ClientboundBlockEventPacket::new).addPacket(ClientboundBlockUpdatePacket.class, ClientboundBlockUpdatePacket::new).addPacket(ClientboundBossEventPacket.class, ClientboundBossEventPacket::new).addPacket(ClientboundChangeDifficultyPacket.class, ClientboundChangeDifficultyPacket::new).addPacket(ClientboundChatPacket.class, ClientboundChatPacket::new).addPacket(ClientboundChunkBlocksUpdatePacket.class, ClientboundChunkBlocksUpdatePacket::new).addPacket(ClientboundCommandSuggestionsPacket.class, ClientboundCommandSuggestionsPacket::new).addPacket(ClientboundCommandsPacket.class, ClientboundCommandsPacket::new).addPacket(ClientboundContainerAckPacket.class, ClientboundContainerAckPacket::new).addPacket(ClientboundContainerClosePacket.class, ClientboundContainerClosePacket::new).addPacket(ClientboundContainerSetContentPacket.class, ClientboundContainerSetContentPacket::new).addPacket(ClientboundContainerSetDataPacket.class, ClientboundContainerSetDataPacket::new).addPacket(ClientboundContainerSetSlotPacket.class, ClientboundContainerSetSlotPacket::new).addPacket(ClientboundCooldownPacket.class, ClientboundCooldownPacket::new).addPacket(ClientboundCustomPayloadPacket.class, ClientboundCustomPayloadPacket::new).addPacket(ClientboundCustomSoundPacket.class, ClientboundCustomSoundPacket::new).addPacket(ClientboundDisconnectPacket.class, ClientboundDisconnectPacket::new).addPacket(ClientboundEntityEventPacket.class, ClientboundEntityEventPacket::new).addPacket(ClientboundExplodePacket.class, ClientboundExplodePacket::new).addPacket(ClientboundForgetLevelChunkPacket.class, ClientboundForgetLevelChunkPacket::new).addPacket(ClientboundGameEventPacket.class, ClientboundGameEventPacket::new).addPacket(ClientboundHorseScreenOpenPacket.class, ClientboundHorseScreenOpenPacket::new).addPacket(ClientboundKeepAlivePacket.class, ClientboundKeepAlivePacket::new).addPacket(ClientboundLevelChunkPacket.class, ClientboundLevelChunkPacket::new).addPacket(ClientboundLevelEventPacket.class, ClientboundLevelEventPacket::new).addPacket(ClientboundLevelParticlesPacket.class, ClientboundLevelParticlesPacket::new).addPacket(ClientboundLightUpdatePacket.class, ClientboundLightUpdatePacket::new).addPacket(ClientboundLoginPacket.class, ClientboundLoginPacket::new).addPacket(ClientboundMapItemDataPacket.class, ClientboundMapItemDataPacket::new).addPacket(ClientboundMerchantOffersPacket.class, ClientboundMerchantOffersPacket::new).addPacket(ClientboundMoveEntityPacket.Pos.class, ClientboundMoveEntityPacket.Pos::new).addPacket(ClientboundMoveEntityPacket.PosRot.class, ClientboundMoveEntityPacket.PosRot::new).addPacket(ClientboundMoveEntityPacket.Rot.class, ClientboundMoveEntityPacket.Rot::new).addPacket(ClientboundMoveEntityPacket.class, ClientboundMoveEntityPacket::new).addPacket(ClientboundMoveVehiclePacket.class, ClientboundMoveVehiclePacket::new).addPacket(ClientboundOpenBookPacket.class, ClientboundOpenBookPacket::new).addPacket(ClientboundOpenScreenPacket.class, ClientboundOpenScreenPacket::new).addPacket(ClientboundOpenSignEditorPacket.class, ClientboundOpenSignEditorPacket::new).addPacket(ClientboundPlaceGhostRecipePacket.class, ClientboundPlaceGhostRecipePacket::new).addPacket(ClientboundPlayerAbilitiesPacket.class, ClientboundPlayerAbilitiesPacket::new).addPacket(ClientboundPlayerCombatPacket.class, ClientboundPlayerCombatPacket::new).addPacket(ClientboundPlayerInfoPacket.class, ClientboundPlayerInfoPacket::new).addPacket(ClientboundPlayerLookAtPacket.class, ClientboundPlayerLookAtPacket::new).addPacket(ClientboundPlayerPositionPacket.class, ClientboundPlayerPositionPacket::new).addPacket(ClientboundRecipePacket.class, ClientboundRecipePacket::new).addPacket(ClientboundRemoveEntitiesPacket.class, ClientboundRemoveEntitiesPacket::new).addPacket(ClientboundRemoveMobEffectPacket.class, ClientboundRemoveMobEffectPacket::new).addPacket(ClientboundResourcePackPacket.class, ClientboundResourcePackPacket::new).addPacket(ClientboundRespawnPacket.class, ClientboundRespawnPacket::new).addPacket(ClientboundRotateHeadPacket.class, ClientboundRotateHeadPacket::new).addPacket(ClientboundSelectAdvancementsTabPacket.class, ClientboundSelectAdvancementsTabPacket::new).addPacket(ClientboundSetBorderPacket.class, ClientboundSetBorderPacket::new).addPacket(ClientboundSetCameraPacket.class, ClientboundSetCameraPacket::new).addPacket(ClientboundSetCarriedItemPacket.class, ClientboundSetCarriedItemPacket::new).addPacket(ClientboundSetChunkCacheCenterPacket.class, ClientboundSetChunkCacheCenterPacket::new).addPacket(ClientboundSetChunkCacheRadiusPacket.class, ClientboundSetChunkCacheRadiusPacket::new).addPacket(ClientboundSetDisplayObjectivePacket.class, ClientboundSetDisplayObjectivePacket::new).addPacket(ClientboundSetEntityDataPacket.class, ClientboundSetEntityDataPacket::new).addPacket(ClientboundSetEntityLinkPacket.class, ClientboundSetEntityLinkPacket::new).addPacket(ClientboundSetEntityMotionPacket.class, ClientboundSetEntityMotionPacket::new).addPacket(ClientboundSetEquippedItemPacket.class, ClientboundSetEquippedItemPacket::new).addPacket(ClientboundSetExperiencePacket.class, ClientboundSetExperiencePacket::new).addPacket(ClientboundSetHealthPacket.class, ClientboundSetHealthPacket::new).addPacket(ClientboundSetObjectivePacket.class, ClientboundSetObjectivePacket::new).addPacket(ClientboundSetPassengersPacket.class, ClientboundSetPassengersPacket::new).addPacket(ClientboundSetPlayerTeamPacket.class, ClientboundSetPlayerTeamPacket::new).addPacket(ClientboundSetScorePacket.class, ClientboundSetScorePacket::new).addPacket(ClientboundSetSpawnPositionPacket.class, ClientboundSetSpawnPositionPacket::new).addPacket(ClientboundSetTimePacket.class, ClientboundSetTimePacket::new).addPacket(ClientboundSetTitlesPacket.class, ClientboundSetTitlesPacket::new).addPacket(ClientboundSoundEntityPacket.class, ClientboundSoundEntityPacket::new).addPacket(ClientboundSoundPacket.class, ClientboundSoundPacket::new).addPacket(ClientboundStopSoundPacket.class, ClientboundStopSoundPacket::new).addPacket(ClientboundTabListPacket.class, ClientboundTabListPacket::new).addPacket(ClientboundTagQueryPacket.class, ClientboundTagQueryPacket::new).addPacket(ClientboundTakeItemEntityPacket.class, ClientboundTakeItemEntityPacket::new).addPacket(ClientboundTeleportEntityPacket.class, ClientboundTeleportEntityPacket::new).addPacket(ClientboundUpdateAdvancementsPacket.class, ClientboundUpdateAdvancementsPacket::new).addPacket(ClientboundUpdateAttributesPacket.class, ClientboundUpdateAttributesPacket::new).addPacket(ClientboundUpdateMobEffectPacket.class, ClientboundUpdateMobEffectPacket::new).addPacket(ClientboundUpdateRecipesPacket.class, ClientboundUpdateRecipesPacket::new).addPacket(ClientboundUpdateTagsPacket.class, ClientboundUpdateTagsPacket::new)).addFlow(PacketFlow.SERVERBOUND, (new ConnectionProtocol.PacketSet()).addPacket(ServerboundAcceptTeleportationPacket.class, ServerboundAcceptTeleportationPacket::new).addPacket(ServerboundBlockEntityTagQuery.class, ServerboundBlockEntityTagQuery::new).addPacket(ServerboundChangeDifficultyPacket.class, ServerboundChangeDifficultyPacket::new).addPacket(ServerboundChatPacket.class, ServerboundChatPacket::new).addPacket(ServerboundClientCommandPacket.class, ServerboundClientCommandPacket::new).addPacket(ServerboundClientInformationPacket.class, ServerboundClientInformationPacket::new).addPacket(ServerboundCommandSuggestionPacket.class, ServerboundCommandSuggestionPacket::new).addPacket(ServerboundContainerAckPacket.class, ServerboundContainerAckPacket::new).addPacket(ServerboundContainerButtonClickPacket.class, ServerboundContainerButtonClickPacket::new).addPacket(ServerboundContainerClickPacket.class, ServerboundContainerClickPacket::new).addPacket(ServerboundContainerClosePacket.class, ServerboundContainerClosePacket::new).addPacket(ServerboundCustomPayloadPacket.class, ServerboundCustomPayloadPacket::new).addPacket(ServerboundEditBookPacket.class, ServerboundEditBookPacket::new).addPacket(ServerboundEntityTagQuery.class, ServerboundEntityTagQuery::new).addPacket(ServerboundInteractPacket.class, ServerboundInteractPacket::new).addPacket(ServerboundKeepAlivePacket.class, ServerboundKeepAlivePacket::new).addPacket(ServerboundLockDifficultyPacket.class, ServerboundLockDifficultyPacket::new).addPacket(ServerboundMovePlayerPacket.Pos.class, ServerboundMovePlayerPacket.Pos::new).addPacket(ServerboundMovePlayerPacket.PosRot.class, ServerboundMovePlayerPacket.PosRot::new).addPacket(ServerboundMovePlayerPacket.Rot.class, ServerboundMovePlayerPacket.Rot::new).addPacket(ServerboundMovePlayerPacket.class, ServerboundMovePlayerPacket::new).addPacket(ServerboundMoveVehiclePacket.class, ServerboundMoveVehiclePacket::new).addPacket(ServerboundPaddleBoatPacket.class, ServerboundPaddleBoatPacket::new).addPacket(ServerboundPickItemPacket.class, ServerboundPickItemPacket::new).addPacket(ServerboundPlaceRecipePacket.class, ServerboundPlaceRecipePacket::new).addPacket(ServerboundPlayerAbilitiesPacket.class, ServerboundPlayerAbilitiesPacket::new).addPacket(ServerboundPlayerActionPacket.class, ServerboundPlayerActionPacket::new).addPacket(ServerboundPlayerCommandPacket.class, ServerboundPlayerCommandPacket::new).addPacket(ServerboundPlayerInputPacket.class, ServerboundPlayerInputPacket::new).addPacket(ServerboundRecipeBookUpdatePacket.class, ServerboundRecipeBookUpdatePacket::new).addPacket(ServerboundRenameItemPacket.class, ServerboundRenameItemPacket::new).addPacket(ServerboundResourcePackPacket.class, ServerboundResourcePackPacket::new).addPacket(ServerboundSeenAdvancementsPacket.class, ServerboundSeenAdvancementsPacket::new).addPacket(ServerboundSelectTradePacket.class, ServerboundSelectTradePacket::new).addPacket(ServerboundSetBeaconPacket.class, ServerboundSetBeaconPacket::new).addPacket(ServerboundSetCarriedItemPacket.class, ServerboundSetCarriedItemPacket::new).addPacket(ServerboundSetCommandBlockPacket.class, ServerboundSetCommandBlockPacket::new).addPacket(ServerboundSetCommandMinecartPacket.class, ServerboundSetCommandMinecartPacket::new).addPacket(ServerboundSetCreativeModeSlotPacket.class, ServerboundSetCreativeModeSlotPacket::new).addPacket(ServerboundSetJigsawBlockPacket.class, ServerboundSetJigsawBlockPacket::new).addPacket(ServerboundSetStructureBlockPacket.class, ServerboundSetStructureBlockPacket::new).addPacket(ServerboundSignUpdatePacket.class, ServerboundSignUpdatePacket::new).addPacket(ServerboundSwingPacket.class, ServerboundSwingPacket::new).addPacket(ServerboundTeleportToEntityPacket.class, ServerboundTeleportToEntityPacket::new).addPacket(ServerboundUseItemOnPacket.class, ServerboundUseItemOnPacket::new).addPacket(ServerboundUseItemPacket.class, ServerboundUseItemPacket::new))),
   STATUS(1, protocol().addFlow(PacketFlow.SERVERBOUND, (new ConnectionProtocol.PacketSet()).addPacket(ServerboundStatusRequestPacket.class, ServerboundStatusRequestPacket::new).addPacket(ServerboundPingRequestPacket.class, ServerboundPingRequestPacket::new)).addFlow(PacketFlow.CLIENTBOUND, (new ConnectionProtocol.PacketSet()).addPacket(ClientboundStatusResponsePacket.class, ClientboundStatusResponsePacket::new).addPacket(ClientboundPongResponsePacket.class, ClientboundPongResponsePacket::new))),
   LOGIN(2, protocol().addFlow(PacketFlow.CLIENTBOUND, (new ConnectionProtocol.PacketSet()).addPacket(ClientboundLoginDisconnectPacket.class, ClientboundLoginDisconnectPacket::new).addPacket(ClientboundHelloPacket.class, ClientboundHelloPacket::new).addPacket(ClientboundGameProfilePacket.class, ClientboundGameProfilePacket::new).addPacket(ClientboundLoginCompressionPacket.class, ClientboundLoginCompressionPacket::new).addPacket(ClientboundCustomQueryPacket.class, ClientboundCustomQueryPacket::new)).addFlow(PacketFlow.SERVERBOUND, (new ConnectionProtocol.PacketSet()).addPacket(ServerboundHelloPacket.class, ServerboundHelloPacket::new).addPacket(ServerboundKeyPacket.class, ServerboundKeyPacket::new).addPacket(ServerboundCustomQueryPacket.class, ServerboundCustomQueryPacket::new)));

   private static final ConnectionProtocol[] LOOKUP = new ConnectionProtocol[4];
   private static final Map PROTOCOL_BY_PACKET = Maps.newHashMap();
   private final int id;
   private final Map flows;

   private static ConnectionProtocol.ProtocolBuilder protocol() {
      return new ConnectionProtocol.ProtocolBuilder();
   }

   private ConnectionProtocol(int var3, ConnectionProtocol.ProtocolBuilder var4) {
      this.id = var3;
      this.flows = var4.flows;
   }

   @Nullable
   public Integer getPacketId(PacketFlow var1, Packet var2) {
      return ((ConnectionProtocol.PacketSet)this.flows.get(var1)).getId(var2.getClass());
   }

   @Nullable
   public Packet createPacket(PacketFlow var1, int var2) {
      return ((ConnectionProtocol.PacketSet)this.flows.get(var1)).createPacket(var2);
   }

   public int getId() {
      return this.id;
   }

   @Nullable
   public static ConnectionProtocol getById(int var0) {
      return var0 >= -1 && var0 <= 2 ? LOOKUP[var0 - -1] : null;
   }

   public static ConnectionProtocol getProtocolForPacket(Packet var0) {
      return (ConnectionProtocol)PROTOCOL_BY_PACKET.get(var0.getClass());
   }

   static {
      ConnectionProtocol[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         ConnectionProtocol var3 = var0[var2];
         int var4 = var3.getId();
         if (var4 < -1 || var4 > 2) {
            throw new Error("Invalid protocol ID " + Integer.toString(var4));
         }

         LOOKUP[var4 - -1] = var3;
         var3.flows.forEach((var1x, var2x) -> {
            var2x.getAllPackets().forEach((var1xx) -> {
               if (PROTOCOL_BY_PACKET.containsKey(var1xx) && PROTOCOL_BY_PACKET.get(var1xx) != var3) {
                  throw new IllegalStateException("Packet " + var1xx + " is already assigned to protocol " + PROTOCOL_BY_PACKET.get(var1xx) + " - can't reassign to " + var3);
               } else {
                  PROTOCOL_BY_PACKET.put(var1xx, var3);
               }
            });
         });
      }

   }

   static class ProtocolBuilder {
      private final Map flows;

      private ProtocolBuilder() {
         this.flows = Maps.newEnumMap(PacketFlow.class);
      }

      public ConnectionProtocol.ProtocolBuilder addFlow(PacketFlow var1, ConnectionProtocol.PacketSet var2) {
         this.flows.put(var1, var2);
         return this;
      }

      // $FF: synthetic method
      ProtocolBuilder(Object var1) {
         this();
      }
   }

   static class PacketSet {
      private final Object2IntMap classToId;
      private final List idToConstructor;

      private PacketSet() {
         this.classToId = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
            var0.defaultReturnValue(-1);
         });
         this.idToConstructor = Lists.newArrayList();
      }

      public ConnectionProtocol.PacketSet addPacket(Class var1, Supplier var2) {
         int var3 = this.idToConstructor.size();
         int var4 = this.classToId.put(var1, var3);
         if (var4 != -1) {
            String var5 = "Packet " + var1 + " is already registered to ID " + var4;
            LogManager.getLogger().fatal(var5);
            throw new IllegalArgumentException(var5);
         } else {
            this.idToConstructor.add(var2);
            return this;
         }
      }

      @Nullable
      public Integer getId(Class var1) {
         int var2 = this.classToId.getInt(var1);
         return var2 == -1 ? null : var2;
      }

      @Nullable
      public Packet createPacket(int var1) {
         Supplier var2 = (Supplier)this.idToConstructor.get(var1);
         return var2 != null ? (Packet)var2.get() : null;
      }

      public Iterable getAllPackets() {
         return Iterables.unmodifiableIterable(this.classToId.keySet());
      }

      // $FF: synthetic method
      PacketSet(Object var1) {
         this();
      }
   }
}
