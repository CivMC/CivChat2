package vg.civcraft.mc.civchat2.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import vg.civcraft.mc.civchat2.CivChat2;
import vg.civcraft.mc.civmodcore.players.settings.impl.LongSetting;
import vg.civcraft.mc.civmodcore.utilities.ConfigParsing;
import vg.civcraft.mc.civmodcore.utilities.TextUtil;
import vg.civcraft.mc.namelayer.NameAPI;

@CommandAlias("globalmute")
@CommandPermission("civchat2.globalmute")
public class GlobalMute extends BaseCommand {

	@Syntax("/globalmute <set|check|remove> <player> [time]")
	@Description("Applies, checks or removes a players global mute timer")
	public void execute(CommandSender sender, String getSetOrCheck, String targetPlayer, String muteTime) {
		UUID player = NameAPI.getUUID(targetPlayer);
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "The player " + targetPlayer + " does not exist");
			return;
		}
		LongSetting banSetting = CivChat2.getInstance().getCivChat2SettingsManager().getGlobalChatMuteSetting();
		switch (getSetOrCheck.toLowerCase()) {
		case "set":
			if (muteTime == null) {
				sender.sendMessage(ChatColor.RED + "You need to supply a ban timer");
				return;
			}
			long time = ConfigParsing.parseTime(muteTime);
			if (time <= 0) {
				sender.sendMessage(ChatColor.RED + "Invalid ban time frame");
				return;
			}
			banSetting.setValue(player, System.currentTimeMillis() + time);
			sender.sendMessage(ChatColor.GREEN + targetPlayer + " will be banned for " + TextUtil
					.formatDuration(time, TimeUnit.MILLISECONDS));
			return;
		case "remove":
			banSetting.setValue(player, 0L);
			return;
		case "check":
		case "get":
			long timeForUnban = banSetting.getValue(player);
			timeForUnban -= System.currentTimeMillis();
			if (timeForUnban <= 0) {
				sender.sendMessage(ChatColor.GREEN + targetPlayer + " is not chat muted");
				return;
			}
			sender.sendMessage(ChatColor.GREEN + targetPlayer + " is banned for another " + TextUtil.formatDuration(timeForUnban, TimeUnit.MILLISECONDS));
			return;
		default:
			sender.sendMessage(ChatColor.RED + getSetOrCheck + " is not a valid action");
		}
	}

	public List<String> tabComplete(CommandSender sender, String[] args) {
		switch (args.length) {
		case 0:
			//return doTabComplete("", Arrays.asList("set", "remove", "check", "get"), false);
		case 1:
			//return doTabComplete(args[0], Arrays.asList("set", "remove", "check", "get"), false);
		case 2:
			//return doTabComplete(args[1], Bukkit.getOnlinePlayers(), Player::getName, false);
		default:
			return Collections.emptyList();
		}
	}

}