package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;

public interface ServerGamePacketListener extends PacketListener {
   void handleAnimate(ServerboundSwingPacket var1);

   void handleChat(ServerboundChatPacket var1);

   void handleClientCommand(ServerboundClientCommandPacket var1);

   void handleClientInformation(ServerboundClientInformationPacket var1);

   void handleContainerButtonClick(ServerboundContainerButtonClickPacket var1);

   void handleContainerClick(ServerboundContainerClickPacket var1);

   void handlePlaceRecipe(ServerboundPlaceRecipePacket var1);

   void handleContainerClose(ServerboundContainerClosePacket var1);

   void handleCustomPayload(ServerboundCustomPayloadPacket var1);

   void handleInteract(ServerboundInteractPacket var1);

   void handleKeepAlive(ServerboundKeepAlivePacket var1);

   void handleMovePlayer(ServerboundMovePlayerPacket var1);

   void handlePong(ServerboundPongPacket var1);

   void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket var1);

   void handlePlayerAction(ServerboundPlayerActionPacket var1);

   void handlePlayerCommand(ServerboundPlayerCommandPacket var1);

   void handlePlayerInput(ServerboundPlayerInputPacket var1);

   void handleSetCarriedItem(ServerboundSetCarriedItemPacket var1);

   void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket var1);

   void handleSignUpdate(ServerboundSignUpdatePacket var1);

   void handleUseItemOn(ServerboundUseItemOnPacket var1);

   void handleUseItem(ServerboundUseItemPacket var1);

   void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket var1);

   void handleResourcePackResponse(ServerboundResourcePackPacket var1);

   void handlePaddleBoat(ServerboundPaddleBoatPacket var1);

   void handleMoveVehicle(ServerboundMoveVehiclePacket var1);

   void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket var1);

   void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket var1);

   void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket var1);

   void handleSeenAdvancements(ServerboundSeenAdvancementsPacket var1);

   void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket var1);

   void handleSetCommandBlock(ServerboundSetCommandBlockPacket var1);

   void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket var1);

   void handlePickItem(ServerboundPickItemPacket var1);

   void handleRenameItem(ServerboundRenameItemPacket var1);

   void handleSetBeaconPacket(ServerboundSetBeaconPacket var1);

   void handleSetStructureBlock(ServerboundSetStructureBlockPacket var1);

   void handleSelectTrade(ServerboundSelectTradePacket var1);

   void handleEditBook(ServerboundEditBookPacket var1);

   void handleEntityTagQuery(ServerboundEntityTagQuery var1);

   void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery var1);

   void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket var1);

   void handleJigsawGenerate(ServerboundJigsawGeneratePacket var1);

   void handleChangeDifficulty(ServerboundChangeDifficultyPacket var1);

   void handleLockDifficulty(ServerboundLockDifficultyPacket var1);
}
