package com.nate.fakenetwork.commands.Punishments.Mutes;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

@SuppressWarnings("deprecation")
public class MuteManager extends Command {

    public MuteManager() {
        super("mute", "fakenetwork.mute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];

            TextComponent swearing = createReason("Swearing", playerName, "mswear");
            TextComponent disrespect = createReason("Disrespect", playerName, "mdisrespect");
            TextComponent spamming = createReason("Spamming", playerName, "mspamming");
            TextComponent harassment = createReason("Harassment", playerName, "mharassment");

            BaseComponent[] reasons = new BaseComponent[] { swearing, disrespect, spamming, harassment };
            BaseComponent[] message = new BaseComponent[] {
                    new TextComponent("-----------------------------------------------------\nMute " + playerName),
                    new TextComponent("\n"),
                    new TextComponent("Reasons:"),
                    new TextComponent("\n"),
                    new TextComponent("\n"),
                    new TextComponent("-----------------------------------------------------")
            };

            for (BaseComponent reason : reasons) {
                message[4].addExtra(reason);
                message[4].addExtra(new TextComponent(" "));
            }
            sender.sendMessage(message);
        } else {
            sender.sendMessage(new TextComponent("Usage: /mute <player>"));
        }
    }

    private TextComponent createReason(String reason, String playerName, String command) {
        TextComponent reasonComponent = new TextComponent(reason);
        reasonComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {
                new TextComponent("Mute " + playerName + " for " + reason)
        }));
        reasonComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command + " " + playerName));
        return reasonComponent;
    }
}
