package net.minecraft.server.jsonrpc;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.methods.AllowlistService;
import net.minecraft.server.jsonrpc.methods.BanlistService;
import net.minecraft.server.jsonrpc.methods.DiscoveryService;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.IpBanlistService;
import net.minecraft.server.jsonrpc.methods.OperatorService;
import net.minecraft.server.jsonrpc.methods.PlayerService;
import net.minecraft.server.jsonrpc.methods.ServerSettingsService;
import net.minecraft.server.jsonrpc.methods.ServerStateService;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class IncomingRpcMethods {
   private static final String NAMESPACE = "minecraft";

   public IncomingRpcMethods() {
      super();
   }

   public static IncomingRpcMethod bootstrap(Registry<IncomingRpcMethod> var0) {
      registerAllowListService(var0);
      registerBanlistService(var0);
      registerIpBanlistService(var0);
      registerPlayerService(var0);
      registerOperatorService(var0);
      registerServerStateService(var0);
      registerServerSettingsService(var0);
      registerGameRuleService(var0);
      return IncomingRpcMethod.method((Function)((var0x) -> DiscoveryService.discover(Schema.getSchemaRegistry())), DiscoveryService.DiscoverResponse.CODEC.codec()).undiscoverable().notOnMainThread().register(var0, "minecraft", "rpc.discover");
   }

   private static void registerAllowListService(Registry<IncomingRpcMethod> var0) {
      IncomingRpcMethod.method(AllowlistService::get, PlayerDto.CODEC.codec().listOf()).description("Get the allowlist").response(new ResultInfo("allowlist", Schema.PLAYER_SCHEMA.asArray())).register(var0, "minecraft", "allowlist");
      IncomingRpcMethod.method(AllowlistService::set, PlayerDto.CODEC.codec().listOf(), PlayerDto.CODEC.codec().listOf()).description("Set the allowlist").param(new ParamInfo("players", Schema.PLAYER_SCHEMA.asArray())).response(new ResultInfo("allowlist", Schema.PLAYER_SCHEMA.asArray())).register(var0, "minecraft", "allowlist/set");
      IncomingRpcMethod.method(AllowlistService::add, PlayerDto.CODEC.codec().listOf(), PlayerDto.CODEC.codec().listOf()).description("Add players to allowlist").param(new ParamInfo("add", Schema.PLAYER_SCHEMA.asArray())).response(new ResultInfo("allowlist", Schema.PLAYER_SCHEMA.asArray())).register(var0, "minecraft", "allowlist/add");
      IncomingRpcMethod.method(AllowlistService::remove, PlayerDto.CODEC.codec().listOf(), PlayerDto.CODEC.codec().listOf()).description("Remove players from allowlist").param(new ParamInfo("remove", Schema.PLAYER_SCHEMA.asArray())).response(new ResultInfo("allowlist", Schema.PLAYER_SCHEMA.asArray())).register(var0, "minecraft", "allowlist/remove");
      IncomingRpcMethod.method(AllowlistService::clear, Codec.BOOL).description("Clear all players in allowlist").response(new ResultInfo("allowlist", Schema.PLAYER_SCHEMA.asArray())).register(var0, "minecraft", "allowlist/clear");
   }

   private static void registerBanlistService(Registry<IncomingRpcMethod> var0) {
      IncomingRpcMethod.method(BanlistService::get, BanlistService.UserBanDto.CODEC.codec().listOf()).description("Get the ban list").response(new ResultInfo("banlist", Schema.PLAYER_BAN_SCHEMA.asArray())).register(var0, "minecraft", "bans");
      IncomingRpcMethod.method(BanlistService::set, BanlistService.UserBanDto.CODEC.codec().listOf(), BanlistService.UserBanDto.CODEC.codec().listOf()).description("Set the banlist").param(new ParamInfo("bans", Schema.PLAYER_BAN_SCHEMA.asArray())).response(new ResultInfo("banlist", Schema.PLAYER_BAN_SCHEMA.asArray())).register(var0, "minecraft", "bans/set");
      IncomingRpcMethod.method(BanlistService::add, BanlistService.UserBanDto.CODEC.codec().listOf(), BanlistService.UserBanDto.CODEC.codec().listOf()).description("Add players to ban list").param(new ParamInfo("add", Schema.PLAYER_BAN_SCHEMA.asArray())).response(new ResultInfo("banlist", Schema.PLAYER_BAN_SCHEMA.asArray())).register(var0, "minecraft", "bans/add");
      IncomingRpcMethod.method(BanlistService::remove, PlayerDto.CODEC.codec().listOf(), BanlistService.UserBanDto.CODEC.codec().listOf()).description("Remove players from ban list").param(new ParamInfo("remove", Schema.PLAYER_SCHEMA.asArray())).response(new ResultInfo("banlist", Schema.PLAYER_BAN_SCHEMA.asArray())).register(var0, "minecraft", "bans/remove");
      IncomingRpcMethod.method(BanlistService::clear, Codec.BOOL).description("Clear all players in ban list").response(new ResultInfo("banlist", Schema.PLAYER_BAN_SCHEMA.asArray())).register(var0, "minecraft", "bans/clear");
   }

   private static void registerIpBanlistService(Registry<IncomingRpcMethod> var0) {
      IncomingRpcMethod.method(IpBanlistService::get, IpBanlistService.IpBanDto.CODEC.codec().listOf()).description("Get the ip ban list").response(new ResultInfo("banlist", Schema.PLAYER_SCHEMA.asArray())).register(var0, "minecraft", "ip_bans");
      IncomingRpcMethod.method(IpBanlistService::set, IpBanlistService.IpBanDto.CODEC.codec().listOf(), IpBanlistService.IpBanDto.CODEC.codec().listOf()).description("Set the ip banlist").param(new ParamInfo("banlist", Schema.IP_BAN_SCHEMA.asArray())).response(new ResultInfo("banlist", Schema.IP_BAN_SCHEMA.asArray())).register(var0, "minecraft", "ip_bans/set");
      IncomingRpcMethod.method(IpBanlistService::add, IpBanlistService.IncomingIpBanDto.CODEC.codec().listOf(), IpBanlistService.IpBanDto.CODEC.codec().listOf()).description("Add ip to ban list").param(new ParamInfo("add", Schema.INCOMING_IP_BAN_SCHEMA.asArray())).response(new ResultInfo("banlist", Schema.IP_BAN_SCHEMA.asArray())).register(var0, "minecraft", "ip_bans/add");
      IncomingRpcMethod.method(IpBanlistService::remove, Codec.STRING.listOf(), IpBanlistService.IpBanDto.CODEC.codec().listOf()).description("Remove ip from ban list").param(new ParamInfo("ip", Schema.STRING_SCHEMA.asArray())).response(new ResultInfo("banlist", Schema.IP_BAN_SCHEMA.asArray())).register(var0, "minecraft", "ip_bans/remove");
      IncomingRpcMethod.method(IpBanlistService::clear, Codec.BOOL).description("Clear all ips in ban list").response(new ResultInfo("banlist", Schema.IP_BAN_SCHEMA.asArray())).register(var0, "minecraft", "ip_bans/clear");
   }

   private static void registerPlayerService(Registry<IncomingRpcMethod> var0) {
      IncomingRpcMethod.method(PlayerService::get, PlayerDto.CODEC.codec().listOf()).description("Get all connected players").response(new ResultInfo("players", Schema.PLAYER_SCHEMA.asArray())).register(var0, "minecraft", "players");
      IncomingRpcMethod.method(PlayerService::kick, PlayerService.KickDto.CODEC, PlayerDto.CODEC.codec().listOf()).description("Kick players").param(new ParamInfo("kick", Schema.KICK_PLAYER_SCHEMA.asArray())).response(new ResultInfo("kicked", Schema.PLAYER_SCHEMA.asArray())).register(var0, "minecraft", "players/kick");
   }

   private static void registerOperatorService(Registry<IncomingRpcMethod> var0) {
      IncomingRpcMethod.method(OperatorService::get, OperatorService.OperatorDto.CODEC.codec().listOf()).description("Get all oped players").response(new ResultInfo("operators", Schema.OPERATOR_SCHEMA.asArray())).register(var0, "minecraft", "operators");
      IncomingRpcMethod.method(OperatorService::set, OperatorService.OperatorDto.CODEC.codec().listOf(), OperatorService.OperatorDto.CODEC.codec().listOf()).description("Set all oped players").param(new ParamInfo("operators", Schema.OPERATOR_SCHEMA.asArray())).response(new ResultInfo("operators", Schema.OPERATOR_SCHEMA.asArray())).register(var0, "minecraft", "operators/set");
      IncomingRpcMethod.method(OperatorService::add, OperatorService.OperatorDto.CODEC.codec().listOf(), OperatorService.OperatorDto.CODEC.codec().listOf()).description("Op players").param(new ParamInfo("add", Schema.OPERATOR_SCHEMA.asArray())).response(new ResultInfo("operators", Schema.OPERATOR_SCHEMA.asArray())).register(var0, "minecraft", "operators/add");
      IncomingRpcMethod.method(OperatorService::remove, PlayerDto.CODEC.codec().listOf(), OperatorService.OperatorDto.CODEC.codec().listOf()).description("Deop players").param(new ParamInfo("remove", Schema.PLAYER_SCHEMA.asArray())).response(new ResultInfo("operators", Schema.OPERATOR_SCHEMA.asArray())).register(var0, "minecraft", "operators/remove");
      IncomingRpcMethod.method(OperatorService::clear, Codec.BOOL).description("Deop all players").response(new ResultInfo("operators", Schema.OPERATOR_SCHEMA.asArray())).register(var0, "minecraft", "operators/clear");
   }

   private static void registerServerStateService(Registry<IncomingRpcMethod> var0) {
      IncomingRpcMethod.method(ServerStateService::status, ServerStateService.ServerState.CODEC).description("Get server status").response(new ResultInfo("status", Schema.SERVER_STATE_SCHEMA.asRef())).register(var0, "minecraft", "server/status");
      IncomingRpcMethod.method(ServerStateService::save, Codec.BOOL, Codec.BOOL).description("Save server state").param(new ParamInfo("flush", Schema.BOOL_SCHEMA)).response(new ResultInfo("saving", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "server/save");
      IncomingRpcMethod.method(ServerStateService::stop, Codec.BOOL).description("Stop server").response(new ResultInfo("stopping", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "server/stop");
      IncomingRpcMethod.method(ServerStateService::systemMessage, ServerStateService.SystemMessage.CODEC, Codec.BOOL).description("Send a system message").param(new ParamInfo("message", Schema.SYSTEM_MESSAGE_SCHEMA.asRef())).response(new ResultInfo("sent", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "server/system_message");
   }

   private static void registerServerSettingsService(Registry<IncomingRpcMethod> var0) {
      IncomingRpcMethod.method(ServerSettingsService::autosave, Codec.BOOL).description("Get whether automatic world saving is enabled on the server").response(new ResultInfo("enabled", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/autosave");
      IncomingRpcMethod.method(ServerSettingsService::setAutosave, Codec.BOOL, Codec.BOOL).description("Enable or disable automatic world saving on the server").param(new ParamInfo("enable", Schema.BOOL_SCHEMA)).response(new ResultInfo("enabled", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/autosave/set");
      IncomingRpcMethod.method(ServerSettingsService::difficulty, Difficulty.CODEC).description("Get the current difficulty level of the server").response(new ResultInfo("difficulty", Schema.DIFFICULTY_SCHEMA)).register(var0, "minecraft", "serversettings/difficulty");
      IncomingRpcMethod.method(ServerSettingsService::setDifficulty, Difficulty.CODEC, Difficulty.CODEC).description("Set the difficulty level of the server").param(new ParamInfo("difficulty", Schema.DIFFICULTY_SCHEMA)).response(new ResultInfo("difficulty", Schema.DIFFICULTY_SCHEMA)).register(var0, "minecraft", "serversettings/difficulty/set");
      IncomingRpcMethod.method(ServerSettingsService::enforceAllowlist, Codec.BOOL).description("Get whether allowlist enforcement is enabled (kicks players immediately when removed from allowlist)").response(new ResultInfo("enforced", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/enforce_allowlist");
      IncomingRpcMethod.method(ServerSettingsService::setEnforceAllowlist, Codec.BOOL, Codec.BOOL).description("Enable or disable allowlist enforcement (when enabled, players are kicked immediately upon removal from allowlist)").param(new ParamInfo("enforce", Schema.BOOL_SCHEMA)).response(new ResultInfo("enforced", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/enforce_allowlist/set");
      IncomingRpcMethod.method(ServerSettingsService::usingAllowlist, Codec.BOOL).description("Get whether the allowlist is enabled on the server").response(new ResultInfo("used", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/use_allowlist");
      IncomingRpcMethod.method(ServerSettingsService::setUsingAllowlist, Codec.BOOL, Codec.BOOL).description("Enable or disable the allowlist on the server (controls whether only allowlisted players can join)").param(new ParamInfo("use", Schema.BOOL_SCHEMA)).response(new ResultInfo("used", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/use_allowlist/set");
      IncomingRpcMethod.method(ServerSettingsService::maxPlayers, Codec.INT).description("Get the maximum number of players allowed to connect to the server").response(new ResultInfo("max", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/max_players");
      IncomingRpcMethod.method(ServerSettingsService::setMaxPlayers, Codec.INT, Codec.INT).description("Set the maximum number of players allowed to connect to the server").param(new ParamInfo("max", Schema.INT_SCHEMA)).response(new ResultInfo("max", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/max_players/set");
      IncomingRpcMethod.method(ServerSettingsService::pauseWhenEmpty, Codec.INT).description("Get the number of seconds before the game is automatically paused when no players are online").response(new ResultInfo("seconds", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/pause_when_empty_seconds");
      IncomingRpcMethod.method(ServerSettingsService::setPauseWhenEmpty, Codec.INT, Codec.INT).description("Set the number of seconds before the game is automatically paused when no players are online").param(new ParamInfo("seconds", Schema.INT_SCHEMA)).response(new ResultInfo("seconds", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/pause_when_empty_seconds/set");
      IncomingRpcMethod.method(ServerSettingsService::playerIdleTimeout, Codec.INT).description("Get the number of seconds before idle players are automatically kicked from the server").response(new ResultInfo("seconds", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/player_idle_timeout");
      IncomingRpcMethod.method(ServerSettingsService::setPlayerIdleTimeout, Codec.INT, Codec.INT).description("Set the number of seconds before idle players are automatically kicked from the server").param(new ParamInfo("seconds", Schema.INT_SCHEMA)).response(new ResultInfo("seconds", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/player_idle_timeout/set");
      IncomingRpcMethod.method(ServerSettingsService::allowFlight, Codec.BOOL).description("Get whether flight is allowed for players in Survival mode").response(new ResultInfo("allowed", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/allow_flight");
      IncomingRpcMethod.method(ServerSettingsService::setAllowFlight, Codec.BOOL, Codec.BOOL).description("Allow or disallow flight for players in Survival mode").param(new ParamInfo("allow", Schema.BOOL_SCHEMA)).response(new ResultInfo("allowed", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/allow_flight/set");
      IncomingRpcMethod.method(ServerSettingsService::motd, Codec.STRING).description("Get the server's message of the day displayed to players").response(new ResultInfo("message", Schema.STRING_SCHEMA)).register(var0, "minecraft", "serversettings/motd");
      IncomingRpcMethod.method(ServerSettingsService::setMotd, Codec.STRING, Codec.STRING).description("Set the server's message of the day displayed to players").param(new ParamInfo("message", Schema.STRING_SCHEMA)).response(new ResultInfo("message", Schema.STRING_SCHEMA)).register(var0, "minecraft", "serversettings/motd/set");
      IncomingRpcMethod.method(ServerSettingsService::spawnProtection, Codec.INT).description("Get the spawn protection radius in blocks (only operators can edit within this area)").response(new ResultInfo("radius", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/spawn_protection_radius");
      IncomingRpcMethod.method(ServerSettingsService::setSpawnProtection, Codec.INT, Codec.INT).description("Set the spawn protection radius in blocks (only operators can edit within this area)").param(new ParamInfo("radius", Schema.INT_SCHEMA)).response(new ResultInfo("radius", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/spawn_protection_radius/set");
      IncomingRpcMethod.method(ServerSettingsService::forceGameMode, Codec.BOOL).description("Get whether players are forced to use the server's default game mode").response(new ResultInfo("forced", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/force_game_mode");
      IncomingRpcMethod.method(ServerSettingsService::setForceGameMode, Codec.BOOL, Codec.BOOL).description("Enable or disable forcing players to use the server's default game mode").param(new ParamInfo("force", Schema.BOOL_SCHEMA)).response(new ResultInfo("forced", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/force_game_mode/set");
      IncomingRpcMethod.method(ServerSettingsService::gameMode, GameType.CODEC).description("Get the server's default game mode").response(new ResultInfo("mode", Schema.GAME_TYPE_SCHEMA)).register(var0, "minecraft", "serversettings/game_mode");
      IncomingRpcMethod.method(ServerSettingsService::setGameMode, GameType.CODEC, GameType.CODEC).description("Set the server's default game mode").param(new ParamInfo("mode", Schema.GAME_TYPE_SCHEMA)).response(new ResultInfo("mode", Schema.GAME_TYPE_SCHEMA)).register(var0, "minecraft", "serversettings/game_mode/set");
      IncomingRpcMethod.method(ServerSettingsService::viewDistance, Codec.INT).description("Get the server's view distance in chunks").response(new ResultInfo("distance", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/view_distance");
      IncomingRpcMethod.method(ServerSettingsService::setViewDistance, Codec.INT, Codec.INT).description("Set the server's view distance in chunks").param(new ParamInfo("distance", Schema.INT_SCHEMA)).response(new ResultInfo("distance", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/view_distance/set");
      IncomingRpcMethod.method(ServerSettingsService::simulationDistance, Codec.INT).description("Get the server's simulation distance in chunks").response(new ResultInfo("distance", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/simulation_distance");
      IncomingRpcMethod.method(ServerSettingsService::setSimulationDistance, Codec.INT, Codec.INT).description("Set the server's simulation distance in chunks").param(new ParamInfo("distance", Schema.INT_SCHEMA)).response(new ResultInfo("distance", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/simulation_distance/set");
      IncomingRpcMethod.method(ServerSettingsService::acceptTransfers, Codec.BOOL).description("Get whether the server accepts player transfers from other servers").response(new ResultInfo("accepted", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/accept_transfers");
      IncomingRpcMethod.method(ServerSettingsService::setAcceptTransfers, Codec.BOOL, Codec.BOOL).description("Enable or disable accepting player transfers from other servers").param(new ParamInfo("accept", Schema.BOOL_SCHEMA)).response(new ResultInfo("accepted", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/accept_transfers/set");
      IncomingRpcMethod.method(ServerSettingsService::statusHeartbeatInterval, Codec.INT).description("Get the interval in seconds between server status heartbeats").response(new ResultInfo("seconds", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/status_heartbeat_interval");
      IncomingRpcMethod.method(ServerSettingsService::setStatusHeartbeatInterval, Codec.INT, Codec.INT).description("Set the interval in seconds between server status heartbeats").param(new ParamInfo("seconds", Schema.INT_SCHEMA)).response(new ResultInfo("seconds", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/status_heartbeat_interval/set");
      IncomingRpcMethod.method(ServerSettingsService::operatorUserPermissionLevel, Codec.INT).description("Get the permission level required for operator commands").response(new ResultInfo("level", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/operator_user_permission_level");
      IncomingRpcMethod.method(ServerSettingsService::setOperatorUserPermissionLevel, Codec.INT, Codec.INT).description("Set the permission level required for operator commands").param(new ParamInfo("level", Schema.INT_SCHEMA)).response(new ResultInfo("level", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/operator_user_permission_level/set");
      IncomingRpcMethod.method(ServerSettingsService::hidesOnlinePlayers, Codec.BOOL).description("Get whether the server hides online player information from status queries").response(new ResultInfo("hidden", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/hide_online_players");
      IncomingRpcMethod.method(ServerSettingsService::setHidesOnlinePlayers, Codec.BOOL, Codec.BOOL).description("Enable or disable hiding online player information from status queries").param(new ParamInfo("hide", Schema.BOOL_SCHEMA)).response(new ResultInfo("hidden", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/hide_online_players/set");
      IncomingRpcMethod.method(ServerSettingsService::repliesToStatus, Codec.BOOL).description("Get whether the server responds to connection status requests").response(new ResultInfo("enabled", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/status_replies");
      IncomingRpcMethod.method(ServerSettingsService::setRepliesToStatus, Codec.BOOL, Codec.BOOL).description("Enable or disable the server responding to connection status requests").param(new ParamInfo("enable", Schema.BOOL_SCHEMA)).response(new ResultInfo("enabled", Schema.BOOL_SCHEMA)).register(var0, "minecraft", "serversettings/status_replies/set");
      IncomingRpcMethod.method(ServerSettingsService::entityBroadcastRangePercentage, Codec.INT).description("Get the entity broadcast range as a percentage").response(new ResultInfo("percentage_points", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/entity_broadcast_range");
      IncomingRpcMethod.method(ServerSettingsService::setEntityBroadcastRangePercentage, Codec.INT, Codec.INT).description("Set the entity broadcast range as a percentage").param(new ParamInfo("percentage_points", Schema.INT_SCHEMA)).response(new ResultInfo("percentage_points", Schema.INT_SCHEMA)).register(var0, "minecraft", "serversettings/entity_broadcast_range/set");
   }

   private static void registerGameRuleService(Registry<IncomingRpcMethod> var0) {
      IncomingRpcMethod.method(GameRulesService::get, GameRulesService.TypedRule.CODEC.codec().listOf()).description("Get the available game rule keys and their current values").response(new ResultInfo("gamerules", Schema.TYPED_GAME_RULE_SCHEMA.asRef().asArray())).register(var0, "minecraft", "gamerules");
      IncomingRpcMethod.method(GameRulesService::update, GameRulesService.UntypedRule.CODEC.codec(), GameRulesService.TypedRule.CODEC.codec()).description("Update game rule value").param(new ParamInfo("gamerule", Schema.UNTYPED_GAME_RULE_SCHEMA.asRef())).response(new ResultInfo("gamerule", Schema.TYPED_GAME_RULE_SCHEMA.asRef())).register(var0, "minecraft", "gamerules/update");
   }
}
