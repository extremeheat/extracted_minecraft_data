package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import org.apache.logging.log4j.LogManager;

public enum EnumConnectionState {
   HANDSHAKING(-1) {
      {
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C00Handshake.class);
      }
   },
   PLAY(0) {
      {
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S00PacketKeepAlive.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S01PacketJoinGame.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S02PacketChat.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S03PacketTimeUpdate.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S04PacketEntityEquipment.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S05PacketSpawnPosition.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S06PacketUpdateHealth.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S07PacketRespawn.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S08PacketPlayerPosLook.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S09PacketHeldItemChange.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S0APacketUseBed.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S0BPacketAnimation.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S0CPacketSpawnPlayer.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S0DPacketCollectItem.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S0EPacketSpawnObject.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S0FPacketSpawnMob.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S10PacketSpawnPainting.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S11PacketSpawnExperienceOrb.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S12PacketEntityVelocity.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S13PacketDestroyEntities.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S14PacketEntity.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S14PacketEntity.S15PacketEntityRelMove.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S14PacketEntity.S16PacketEntityLook.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S14PacketEntity.S17PacketEntityLookMove.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S18PacketEntityTeleport.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S19PacketEntityHeadLook.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S19PacketEntityStatus.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S1BPacketEntityAttach.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S1CPacketEntityMetadata.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S1DPacketEntityEffect.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S1EPacketRemoveEntityEffect.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S1FPacketSetExperience.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S20PacketEntityProperties.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S21PacketChunkData.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S22PacketMultiBlockChange.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S23PacketBlockChange.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S24PacketBlockAction.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S25PacketBlockBreakAnim.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S26PacketMapChunkBulk.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S27PacketExplosion.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S28PacketEffect.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S29PacketSoundEffect.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S2APacketParticles.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S2BPacketChangeGameState.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S2CPacketSpawnGlobalEntity.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S2DPacketOpenWindow.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S2EPacketCloseWindow.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S2FPacketSetSlot.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S30PacketWindowItems.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S31PacketWindowProperty.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S32PacketConfirmTransaction.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S33PacketUpdateSign.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S34PacketMaps.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S35PacketUpdateTileEntity.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S36PacketSignEditorOpen.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S37PacketStatistics.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S38PacketPlayerListItem.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S39PacketPlayerAbilities.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S3APacketTabComplete.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S3BPacketScoreboardObjective.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S3CPacketUpdateScore.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S3DPacketDisplayScoreboard.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S3EPacketTeams.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S3FPacketCustomPayload.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S40PacketDisconnect.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S41PacketServerDifficulty.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S42PacketCombatEvent.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S43PacketCamera.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S44PacketWorldBorder.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S45PacketTitle.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S46PacketSetCompressionLevel.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S47PacketPlayerListHeaderFooter.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S48PacketResourcePackSend.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S49PacketUpdateEntityNBT.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C00PacketKeepAlive.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C01PacketChatMessage.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C02PacketUseEntity.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C03PacketPlayer.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C03PacketPlayer.C04PacketPlayerPosition.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C03PacketPlayer.C05PacketPlayerLook.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C03PacketPlayer.C06PacketPlayerPosLook.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C07PacketPlayerDigging.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C08PacketPlayerBlockPlacement.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C09PacketHeldItemChange.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C0APacketAnimation.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C0BPacketEntityAction.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C0CPacketInput.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C0DPacketCloseWindow.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C0EPacketClickWindow.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C0FPacketConfirmTransaction.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C10PacketCreativeInventoryAction.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C11PacketEnchantItem.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C12PacketUpdateSign.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C13PacketPlayerAbilities.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C14PacketTabComplete.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C15PacketClientSettings.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C16PacketClientStatus.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C17PacketCustomPayload.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C18PacketSpectate.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C19PacketResourcePackStatus.class);
      }
   },
   STATUS(1) {
      {
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C00PacketServerQuery.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S00PacketServerInfo.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C01PacketPing.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S01PacketPong.class);
      }
   },
   LOGIN(2) {
      {
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S00PacketDisconnect.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S01PacketEncryptionRequest.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S02PacketLoginSuccess.class);
         this.func_179245_a(EnumPacketDirection.CLIENTBOUND, S03PacketEnableCompression.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C00PacketLoginStart.class);
         this.func_179245_a(EnumPacketDirection.SERVERBOUND, C01PacketEncryptionResponse.class);
      }
   };

   private static int field_181136_e = -1;
   private static int field_181137_f = 2;
   private static final EnumConnectionState[] field_150764_e = new EnumConnectionState[field_181137_f - field_181136_e + 1];
   private static final Map<Class<? extends Packet>, EnumConnectionState> field_150761_f = Maps.newHashMap();
   private final int field_150762_g;
   private final Map<EnumPacketDirection, BiMap<Integer, Class<? extends Packet>>> field_179247_h;

   private EnumConnectionState(int var3) {
      this.field_179247_h = Maps.newEnumMap(EnumPacketDirection.class);
      this.field_150762_g = var3;
   }

   protected EnumConnectionState func_179245_a(EnumPacketDirection var1, Class<? extends Packet> var2) {
      Object var3 = (BiMap)this.field_179247_h.get(var1);
      if (var3 == null) {
         var3 = HashBiMap.create();
         this.field_179247_h.put(var1, var3);
      }

      if (((BiMap)var3).containsValue(var2)) {
         String var4 = var1 + " packet " + var2 + " is already known to ID " + ((BiMap)var3).inverse().get(var2);
         LogManager.getLogger().fatal(var4);
         throw new IllegalArgumentException(var4);
      } else {
         ((BiMap)var3).put(((BiMap)var3).size(), var2);
         return this;
      }
   }

   public Integer func_179246_a(EnumPacketDirection var1, Packet var2) {
      return (Integer)((BiMap)this.field_179247_h.get(var1)).inverse().get(var2.getClass());
   }

   public Packet func_179244_a(EnumPacketDirection var1, int var2) throws IllegalAccessException, InstantiationException {
      Class var3 = (Class)((BiMap)this.field_179247_h.get(var1)).get(var2);
      return var3 == null ? null : (Packet)var3.newInstance();
   }

   public int func_150759_c() {
      return this.field_150762_g;
   }

   public static EnumConnectionState func_150760_a(int var0) {
      return var0 >= field_181136_e && var0 <= field_181137_f ? field_150764_e[var0 - field_181136_e] : null;
   }

   public static EnumConnectionState func_150752_a(Packet var0) {
      return (EnumConnectionState)field_150761_f.get(var0.getClass());
   }

   // $FF: synthetic method
   EnumConnectionState(int var3, Object var4) {
      this(var3);
   }

   static {
      EnumConnectionState[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         EnumConnectionState var3 = var0[var2];
         int var4 = var3.func_150759_c();
         if (var4 < field_181136_e || var4 > field_181137_f) {
            throw new Error("Invalid protocol ID " + Integer.toString(var4));
         }

         field_150764_e[var4 - field_181136_e] = var3;
         Iterator var5 = var3.field_179247_h.keySet().iterator();

         while(var5.hasNext()) {
            EnumPacketDirection var6 = (EnumPacketDirection)var5.next();

            Class var8;
            for(Iterator var7 = ((BiMap)var3.field_179247_h.get(var6)).values().iterator(); var7.hasNext(); field_150761_f.put(var8, var3)) {
               var8 = (Class)var7.next();
               if (field_150761_f.containsKey(var8) && field_150761_f.get(var8) != var3) {
                  throw new Error("Packet " + var8 + " is already assigned to protocol " + field_150761_f.get(var8) + " - can't reassign to " + var3);
               }

               try {
                  var8.newInstance();
               } catch (Throwable var10) {
                  throw new Error("Packet " + var8 + " fails instantiation checks! " + var8);
               }
            }
         }
      }

   }
}
