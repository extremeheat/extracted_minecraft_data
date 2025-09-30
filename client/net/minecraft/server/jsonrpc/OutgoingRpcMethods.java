package net.minecraft.server.jsonrpc;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.methods.BanlistService;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.IpBanlistService;
import net.minecraft.server.jsonrpc.methods.OperatorService;
import net.minecraft.server.jsonrpc.methods.ServerStateService;

public class OutgoingRpcMethods {
   public static final Holder.Reference<OutgoingRpcMethod.ParmeterlessNotification> SERVER_STARTED = OutgoingRpcMethod.notification().description("Server started").register("server/started");
   public static final Holder.Reference<OutgoingRpcMethod.ParmeterlessNotification> SERVER_SHUTTING_DOWN = OutgoingRpcMethod.notification().description("Server shutting down").register("server/stopping");
   public static final Holder.Reference<OutgoingRpcMethod.ParmeterlessNotification> SERVER_SAVE_STARTED = OutgoingRpcMethod.notification().description("Server save started").register("server/saving");
   public static final Holder.Reference<OutgoingRpcMethod.ParmeterlessNotification> SERVER_SAVE_COMPLETED = OutgoingRpcMethod.notification().description("Server save completed").register("server/saved");
   public static final Holder.Reference<OutgoingRpcMethod.Notification<PlayerDto>> PLAYER_JOINED;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<PlayerDto>> PLAYER_LEFT;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<OperatorService.OperatorDto>> PLAYER_OPED;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<OperatorService.OperatorDto>> PLAYER_DEOPED;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<PlayerDto>> PLAYER_ADDED_TO_ALLOWLIST;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<PlayerDto>> PLAYER_REMOVED_FROM_ALLOWLIST;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<IpBanlistService.IpBanDto>> IP_BANNED;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<String>> IP_UNBANNED;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<BanlistService.UserBanDto>> PLAYER_BANNED;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<PlayerDto>> PLAYER_UNBANNED;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<GameRulesService.TypedRule>> GAMERULE_CHANGED;
   public static final Holder.Reference<OutgoingRpcMethod.Notification<ServerStateService.ServerState>> STATUS_HEARTBEAT;

   public OutgoingRpcMethods() {
      super();
   }

   static {
      PLAYER_JOINED = OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player joined").register("players/joined");
      PLAYER_LEFT = OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player left").register("players/left");
      PLAYER_OPED = OutgoingRpcMethod.notification(OperatorService.OperatorDto.CODEC.codec()).param(new ParamInfo("player", Schema.OPERATOR_SCHEMA.asRef())).description("Player was oped").register("operators/added");
      PLAYER_DEOPED = OutgoingRpcMethod.notification(OperatorService.OperatorDto.CODEC.codec()).param(new ParamInfo("player", Schema.OPERATOR_SCHEMA.asRef())).description("Player was deoped").register("operators/removed");
      PLAYER_ADDED_TO_ALLOWLIST = OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player was added to allowlist").register("allowlist/added");
      PLAYER_REMOVED_FROM_ALLOWLIST = OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player was removed from allowlist").register("allowlist/removed");
      IP_BANNED = OutgoingRpcMethod.notification(IpBanlistService.IpBanDto.CODEC.codec()).param(new ParamInfo("player", Schema.IP_BAN_SCHEMA.asRef())).description("Ip was added to ip ban list").register("ip_bans/added");
      IP_UNBANNED = OutgoingRpcMethod.notification(Codec.STRING).param(new ParamInfo("player", Schema.STRING_SCHEMA)).description("Ip was removed from ip ban list").register("ip_bans/removed");
      PLAYER_BANNED = OutgoingRpcMethod.notification(BanlistService.UserBanDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_BAN_SCHEMA.asRef())).description("Player was added to ban list").register("bans/added");
      PLAYER_UNBANNED = OutgoingRpcMethod.notification(PlayerDto.CODEC.codec()).param(new ParamInfo("player", Schema.PLAYER_SCHEMA.asRef())).description("Player was removed from ban list").register("bans/removed");
      GAMERULE_CHANGED = OutgoingRpcMethod.notification(GameRulesService.TypedRule.CODEC.codec()).param(new ParamInfo("gamerule", Schema.TYPED_GAME_RULE_SCHEMA.asRef())).description("Gamerule was changed").register("gamerules/updated");
      STATUS_HEARTBEAT = OutgoingRpcMethod.notification(ServerStateService.ServerState.CODEC).param(new ParamInfo("status", Schema.SERVER_STATE_SCHEMA.asRef())).description("Server status heartbeat").register("server/status");
   }
}
