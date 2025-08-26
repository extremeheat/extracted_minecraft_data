package net.minecraft.server.jsonrpc;

import com.mojang.serialization.Codec;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.methods.BanlistService;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.IpBanlistService;
import net.minecraft.server.jsonrpc.methods.OperatorService;
import net.minecraft.server.jsonrpc.methods.ServerStateService;

public class OutgoingRpcMethods {
   private static final String NOTIFICATION_NAMESPACE = "notification";
   public static final OutgoingRpcMethod.ParmeterlessNotification SERVER_STARTED = (OutgoingRpcMethod.ParmeterlessNotification)OutgoingRpcMethod.notification().description("Server started").register("notification", "server/started");
   public static final OutgoingRpcMethod.ParmeterlessNotification SERVER_SHUTTING_DOWN = (OutgoingRpcMethod.ParmeterlessNotification)OutgoingRpcMethod.notification().description("Server shutting down").register("notification", "server/stopping");
   public static final OutgoingRpcMethod.ParmeterlessNotification SERVER_SAVE_STARTED = (OutgoingRpcMethod.ParmeterlessNotification)OutgoingRpcMethod.notification().description("Server save started").register("notification", "server/saving");
   public static final OutgoingRpcMethod.ParmeterlessNotification SERVER_SAVE_COMPLETED = (OutgoingRpcMethod.ParmeterlessNotification)OutgoingRpcMethod.notification().description("Server save completed").register("notification", "server/saved");
   public static final OutgoingRpcMethod.Notification<PlayerDto> PLAYER_JOINED;
   public static final OutgoingRpcMethod.Notification<PlayerDto> PLAYER_LEFT;
   public static final OutgoingRpcMethod.Notification<OperatorService.OperatorDto> PLAYER_OPED;
   public static final OutgoingRpcMethod.Notification<OperatorService.OperatorDto> PLAYER_DEOPED;
   public static final OutgoingRpcMethod.Notification<PlayerDto> PLAYER_ADDED_TO_ALLOWLIST;
   public static final OutgoingRpcMethod.Notification<PlayerDto> PLAYER_REMOVED_FROM_ALLOWLIST;
   public static final OutgoingRpcMethod.Notification<IpBanlistService.IpBanDto> IP_BANNED;
   public static final OutgoingRpcMethod.Notification<String> IP_UNBANNED;
   public static final OutgoingRpcMethod.Notification<BanlistService.UserBanDto> PLAYER_BANNED;
   public static final OutgoingRpcMethod.Notification<PlayerDto> PLAYER_UNBANNED;
   public static final OutgoingRpcMethod.Notification<GameRulesService.TypedRule> GAMERULE_CHANGED;
   public static final OutgoingRpcMethod.Notification<ServerStateService.ServerState> STATUS_HEARTBEAT;

   public OutgoingRpcMethods() {
      super();
   }

   static {
      PLAYER_JOINED = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player joined").register("notification", "players/joined");
      PLAYER_LEFT = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player left").register("notification", "players/left");
      PLAYER_OPED = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(OperatorService.OperatorDto.CODEC.codec()).param(new ParamInfo("player", Schema.OPERATOR_SCHEMA.asRef())).description("Player was oped").register("notification", "operators/added");
      PLAYER_DEOPED = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(OperatorService.OperatorDto.CODEC.codec()).param(new ParamInfo("player", Schema.OPERATOR_SCHEMA.asRef())).description("Player was deoped").register("notification", "operators/removed");
      PLAYER_ADDED_TO_ALLOWLIST = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player was added to allowlist").register("notification", "allowlist/added");
      PLAYER_REMOVED_FROM_ALLOWLIST = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player was removed from allowlist").register("notification", "allowlist/removed");
      IP_BANNED = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(IpBanlistService.IpBanDto.CODEC.codec()).param(new ParamInfo("player", Schema.IP_BAN_SCHEMA.asRef())).description("Ip was added to ip ban list").register("notification", "ip_bans/added");
      IP_UNBANNED = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(Codec.STRING).param(new ParamInfo("player", Schema.STRING_SCHEMA)).description("Ip was removed from ip ban list").register("notification", "ip_bans/removed");
      PLAYER_BANNED = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(BanlistService.UserBanDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_BAN_SCHEMA.asRef())).description("Player was added to ban list").register("notification", "bans/added");
      PLAYER_UNBANNED = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player was removed from ban list").register("notification", "bans/removed");
      GAMERULE_CHANGED = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(GameRulesService.TypedRule.CODEC.codec()).param(new ParamInfo("gamerule", Schema.TYPED_GAME_RULE_SCHEMA.asRef())).description("Gamerule was changed").register("notification", "gamerules/updated");
      STATUS_HEARTBEAT = (OutgoingRpcMethod.Notification)OutgoingRpcMethod.notification(ServerStateService.ServerState.CODEC).param(new ParamInfo("status", Schema.SERVER_STATE_SCHEMA.asRef())).description("Server status heartbeat").register("notification", "server/status");
   }
}
