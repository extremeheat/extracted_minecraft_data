package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;

public interface ClientGamePacketListener extends PacketListener {
   void handleAddEntity(ClientboundAddEntityPacket var1);

   void handleAddExperienceOrb(ClientboundAddExperienceOrbPacket var1);

   void handleAddMob(ClientboundAddMobPacket var1);

   void handleAddObjective(ClientboundSetObjectivePacket var1);

   void handleAddPainting(ClientboundAddPaintingPacket var1);

   void handleAddPlayer(ClientboundAddPlayerPacket var1);

   void handleAnimate(ClientboundAnimatePacket var1);

   void handleAwardStats(ClientboundAwardStatsPacket var1);

   void handleAddOrRemoveRecipes(ClientboundRecipePacket var1);

   void handleBlockDestruction(ClientboundBlockDestructionPacket var1);

   void handleOpenSignEditor(ClientboundOpenSignEditorPacket var1);

   void handleBlockEntityData(ClientboundBlockEntityDataPacket var1);

   void handleBlockEvent(ClientboundBlockEventPacket var1);

   void handleBlockUpdate(ClientboundBlockUpdatePacket var1);

   void handleChat(ClientboundChatPacket var1);

   void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket var1);

   void handleMapItemData(ClientboundMapItemDataPacket var1);

   void handleContainerAck(ClientboundContainerAckPacket var1);

   void handleContainerClose(ClientboundContainerClosePacket var1);

   void handleContainerContent(ClientboundContainerSetContentPacket var1);

   void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket var1);

   void handleContainerSetData(ClientboundContainerSetDataPacket var1);

   void handleContainerSetSlot(ClientboundContainerSetSlotPacket var1);

   void handleCustomPayload(ClientboundCustomPayloadPacket var1);

   void handleDisconnect(ClientboundDisconnectPacket var1);

   void handleEntityEvent(ClientboundEntityEventPacket var1);

   void handleEntityLinkPacket(ClientboundSetEntityLinkPacket var1);

   void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket var1);

   void handleExplosion(ClientboundExplodePacket var1);

   void handleGameEvent(ClientboundGameEventPacket var1);

   void handleKeepAlive(ClientboundKeepAlivePacket var1);

   void handleLevelChunk(ClientboundLevelChunkPacket var1);

   void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket var1);

   void handleLevelEvent(ClientboundLevelEventPacket var1);

   void handleLogin(ClientboundLoginPacket var1);

   void handleMoveEntity(ClientboundMoveEntityPacket var1);

   void handleMovePlayer(ClientboundPlayerPositionPacket var1);

   void handleParticleEvent(ClientboundLevelParticlesPacket var1);

   void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket var1);

   void handlePlayerInfo(ClientboundPlayerInfoPacket var1);

   void handleRemoveEntity(ClientboundRemoveEntitiesPacket var1);

   void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket var1);

   void handleRespawn(ClientboundRespawnPacket var1);

   void handleRotateMob(ClientboundRotateHeadPacket var1);

   void handleSetCarriedItem(ClientboundSetCarriedItemPacket var1);

   void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket var1);

   void handleSetEntityData(ClientboundSetEntityDataPacket var1);

   void handleSetEntityMotion(ClientboundSetEntityMotionPacket var1);

   void handleSetEquipment(ClientboundSetEquipmentPacket var1);

   void handleSetExperience(ClientboundSetExperiencePacket var1);

   void handleSetHealth(ClientboundSetHealthPacket var1);

   void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket var1);

   void handleSetScore(ClientboundSetScorePacket var1);

   void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket var1);

   void handleSetTime(ClientboundSetTimePacket var1);

   void handleSoundEvent(ClientboundSoundPacket var1);

   void handleSoundEntityEvent(ClientboundSoundEntityPacket var1);

   void handleCustomSoundEvent(ClientboundCustomSoundPacket var1);

   void handleTakeItemEntity(ClientboundTakeItemEntityPacket var1);

   void handleTeleportEntity(ClientboundTeleportEntityPacket var1);

   void handleUpdateAttributes(ClientboundUpdateAttributesPacket var1);

   void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket var1);

   void handleUpdateTags(ClientboundUpdateTagsPacket var1);

   void handlePlayerCombat(ClientboundPlayerCombatPacket var1);

   void handleChangeDifficulty(ClientboundChangeDifficultyPacket var1);

   void handleSetCamera(ClientboundSetCameraPacket var1);

   void handleSetBorder(ClientboundSetBorderPacket var1);

   void handleSetTitles(ClientboundSetTitlesPacket var1);

   void handleTabListCustomisation(ClientboundTabListPacket var1);

   void handleResourcePack(ClientboundResourcePackPacket var1);

   void handleBossUpdate(ClientboundBossEventPacket var1);

   void handleItemCooldown(ClientboundCooldownPacket var1);

   void handleMoveVehicle(ClientboundMoveVehiclePacket var1);

   void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket var1);

   void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket var1);

   void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket var1);

   void handleCommands(ClientboundCommandsPacket var1);

   void handleStopSoundEvent(ClientboundStopSoundPacket var1);

   void handleCommandSuggestions(ClientboundCommandSuggestionsPacket var1);

   void handleUpdateRecipes(ClientboundUpdateRecipesPacket var1);

   void handleLookAt(ClientboundPlayerLookAtPacket var1);

   void handleTagQueryPacket(ClientboundTagQueryPacket var1);

   void handleLightUpdatePacked(ClientboundLightUpdatePacket var1);

   void handleOpenBook(ClientboundOpenBookPacket var1);

   void handleOpenScreen(ClientboundOpenScreenPacket var1);

   void handleMerchantOffers(ClientboundMerchantOffersPacket var1);

   void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket var1);

   void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket var1);

   void handleBlockBreakAck(ClientboundBlockBreakAckPacket var1);
}
