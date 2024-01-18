package com.nate.bungee.commands.Punishments.Bans;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BanCommand extends Command {

    private Plugin plugin;

    public BanCommand(Plugin plugin) {
        super("ban");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("This command can only be used by players."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission("fakenetwork.staff")) {
            player.sendMessage(new TextComponent("You do not have permission to use this command."));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(new TextComponent("Usage: /ban <player>"));
            return;
        }
        ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(new TextComponent("Player not found."));
            return;
        }

        if (target.hasPermission("fakenetwork.staff") && !player.hasPermission("fakenetwork.moderator")) {
            player.sendMessage(new TextComponent("You cannot ban this player."));
            return;
        }
        openBanGUI(player, target);
    }

    private void openBanGUI(ProxiedPlayer player, ProxiedPlayer target) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("OpenBanGUI");
        out.writeUTF(target.getUniqueId().toString());

        player.getServer().sendData("BungeeCord", out.toByteArray());
    }
}
