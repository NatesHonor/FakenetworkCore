package com.nate.bungee.commands.Punishments.Mutes;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import com.nate.bungee.commands.Punishments.SQLStatements.Mutes;

import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class MuteManager extends Command {
    Mutes mutes = new Mutes();

    public MuteManager() {
        super("mute", "fakenetwork.mute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];

            if (mutes.isPlayerMuted(playerName)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "This player has already been muted."));
                return;
            }

            TextComponent swearing = createReason("Swearing", playerName, "mswear");
            TextComponent disrespect = createReason("Disrespect", playerName, "mdisrespect");
            TextComponent spamming = createReason("Spamming", playerName, "mspamming");
            TextComponent harassment = createReason("Harassment", playerName, "mharassment");

            BaseComponent[] reasons = new BaseComponent[] { swearing, disrespect, spamming, harassment };
            BaseComponent[] message = new BaseComponent[] {
                    new TextComponent(ChatColor.GOLD + "-----------------------------------------------------\nMute "
                            + ChatColor.BLUE + playerName),
                    new TextComponent("\n"),
                    new TextComponent(ChatColor.YELLOW + "Reasons:"),
                    new TextComponent("\n"),
                    new TextComponent("\n"),
                    new TextComponent(ChatColor.GOLD + "-----------------------------------------------------")
            };

            for (BaseComponent reason : reasons) {
                message[4].addExtra(reason);
                message[4].addExtra(new TextComponent(" "));
            }
            sender.sendMessage(message);
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /mute <player>"));
        }
    }

    private TextComponent createReason(String reason, String playerName, String command) {
        TextComponent reasonComponent = new TextComponent(ChatColor.GREEN + reason);
        reasonComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {
                new TextComponent(ChatColor.AQUA + "Mute " + playerName + " for " + reason)
        }));
        reasonComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command + " " + playerName));
        return reasonComponent;
    }

    public static class ChatListener implements Listener {

        @EventHandler
        public void onPlayerChat(ChatEvent event) {
            Mutes mutes = new Mutes();
            if (!(event.getSender() instanceof ProxiedPlayer)) {
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            if (event.getMessage().startsWith("/")) {
                return;
            }
            if (mutes.isPlayerMuted(player.getName())) {
                player.sendMessage(new TextComponent(ChatColor.RED + "You are currently muted and cannot chat."));
                event.setCancelled(true);
            }
        }
    }
}
