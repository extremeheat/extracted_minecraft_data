package net.minecraft.server.jsonrpc;

import net.minecraft.core.Holder;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.methods.BanlistService;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.IpBanlistService;
import net.minecraft.server.jsonrpc.methods.OperatorService;
import net.minecraft.server.jsonrpc.methods.ServerStateService;

public class OutgoingRpcMethods {
   public static final Holder.Reference<OutgoingRpcMethod<Void, Void>> SERVER_STARTED = OutgoingRpcMethod.notification().description("Server started").register("server/started");
   public static final Holder.Reference<OutgoingRpcMethod<Void, Void>> SERVER_SHUTTING_DOWN = OutgoingRpcMethod.notification().description("Server shutting down").register("server/stopping");
   public static final Holder.Reference<OutgoingRpcMethod<Void, Void>> SERVER_SAVE_STARTED = OutgoingRpcMethod.notification().description("Server save started").register("server/saving");
   public static final Holder.Reference<OutgoingRpcMethod<Void, Void>> SERVER_SAVE_COMPLETED = OutgoingRpcMethod.notification().description("Server save completed").register("server/saved");
   public static final Holder.Reference<OutgoingRpcMethod<Void, Void>> SERVER_ACTIVITY_OCCURRED = OutgoingRpcMethod.notification().description("Server activity occurred. Rate limited to 1 notification per 30 seconds").register("server/activity");
   public static final Holder.Reference<OutgoingRpcMethod<PlayerDto, Void>> PLAYER_JOINED;
   public static final Holder.Reference<OutgoingRpcMethod<PlayerDto, Void>> PLAYER_LEFT;
   public static final Holder.Reference<OutgoingRpcMethod<OperatorService.OperatorDto, Void>> PLAYER_OPED;
   public static final Holder.Reference<OutgoingRpcMethod<OperatorService.OperatorDto, Void>> PLAYER_DEOPED;
   public static final Holder.Reference<OutgoingRpcMethod<PlayerDto, Void>> PLAYER_ADDED_TO_ALLOWLIST;
   public static final Holder.Reference<OutgoingRpcMethod<PlayerDto, Void>> PLAYER_REMOVED_FROM_ALLOWLIST;
   public static final Holder.Reference<OutgoingRpcMethod<IpBanlistService.IpBanDto, Void>> IP_BANNED;
   public static final Holder.Reference<OutgoingRpcMethod<String, Void>> IP_UNBANNED;
   public static final Holder.Reference<OutgoingRpcMethod<BanlistService.UserBanDto, Void>> PLAYER_BANNED;
   public static final Holder.Reference<OutgoingRpcMethod<PlayerDto, Void>> PLAYER_UNBANNED;
   public static final Holder.Reference<OutgoingRpcMethod<GameRulesService.GameRuleUpdate<?>, Void>> GAMERULE_CHANGED;
   public static final Holder.Reference<OutgoingRpcMethod<ServerStateService.ServerState, Void>> STATUS_HEARTBEAT;

   public OutgoingRpcMethods() {
      super();
   }

   static {
      PLAYER_JOINED = OutgoingRpcMethod.notificationWithParams().param("player", Schema.PLAYER_SCHEMA.asRef()).description("Player joined").register("players/joined");
      PLAYER_LEFT = OutgoingRpcMethod.notificationWithParams().param("player", Schema.PLAYER_SCHEMA.asRef()).description("Player left").register("players/left");
      PLAYER_OPED = OutgoingRpcMethod.notificationWithParams().param("player", Schema.OPERATOR_SCHEMA.asRef()).description("Player was oped").register("operators/added");
      PLAYER_DEOPED = OutgoingRpcMethod.notificationWithParams().param("player", Schema.OPERATOR_SCHEMA.asRef()).description("Player was deoped").register("operators/removed");
      PLAYER_ADDED_TO_ALLOWLIST = OutgoingRpcMethod.notificationWithParams().param("player", Schema.PLAYER_SCHEMA.asRef()).description("Player was added to allowlist").register("allowlist/added");
      PLAYER_REMOVED_FROM_ALLOWLIST = OutgoingRpcMethod.notificationWithParams().param("player", Schema.PLAYER_SCHEMA.asRef()).description("Player was removed from allowlist").register("allowlist/removed");
      IP_BANNED = OutgoingRpcMethod.notificationWithParams().param("player", Schema.IP_BAN_SCHEMA.asRef()).description("Ip was added to ip ban list").register("ip_bans/added");
      IP_UNBANNED = OutgoingRpcMethod.notificationWithParams().param("player", Schema.STRING_SCHEMA).description("Ip was removed from ip ban list").register("ip_bans/removed");
      PLAYER_BANNED = OutgoingRpcMethod.notificationWithParams().param("player", Schema.PLAYER_BAN_SCHEMA.asRef()).description("Player was added to ban list").register("bans/added");
      PLAYER_UNBANNED = OutgoingRpcMethod.notificationWithParams().param("player", Schema.PLAYER_SCHEMA.asRef()).description("Player was removed from ban list").register("bans/removed");
      GAMERULE_CHANGED = OutgoingRpcMethod.notificationWithParams().param("gamerule", Schema.TYPED_GAME_RULE_SCHEMA.asRef()).description("Gamerule was changed").register("gamerules/updated");
      STATUS_HEARTBEAT = OutgoingRpcMethod.notificationWithParams().param("status", Schema.SERVER_STATE_SCHEMA.asRef()).description("Server status heartbeat").register("server/status");
   }
}
