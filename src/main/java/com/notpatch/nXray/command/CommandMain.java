package com.notpatch.nXray.command;

import com.notpatch.nXray.NXray;
import com.notpatch.nXray.framework.XrayScanner;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandMain implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender,Command cmd, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("nxray.admin")){
                switch (args.length){
                    case 0:
                        p.sendMessage("§eNXray§7: §a/nxray §7[§egive§7|§ereload§7]");
                        break;
                    case 1:
                        if(args[0].equalsIgnoreCase("give")){
                            p.sendMessage("§eNXray§7: §a/nxray give <player>");
                        }
                        if(args[0].equalsIgnoreCase("reload")){
                            NXray.getInstance().reloadConfig();
                            NXray.getInstance().saveDefaultConfig();
                            NXray.getInstance().saveConfig();
                            NXray.getInstance().getScannerManager().loadItemsFromConfig();
                            p.sendMessage("§eNXray§7: §aConfig reloaded.");
                        }
                        break;
                    case 3:
                        if(args[0].equalsIgnoreCase("give")) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target != null && target.isOnline()) {
                                String itemName = args[2];
                                for(XrayScanner xrayScanner : NXray.getInstance().getScannerManager().getXrayScanners()){
                                    if(xrayScanner.getId().equalsIgnoreCase(itemName)){
                                        target.getInventory().addItem(xrayScanner.toItemStack());
                                    }
                                }
                                p.sendMessage("§eNXray§7: §aItem given to " + target.getName());
                            }
                            break;
                        }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        switch (args.length){
            case 1:
                return List.of("give", "reload");
            case 2:
                if(args[0].equalsIgnoreCase("give")){
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                }
            case 3:
                if(args[0].equalsIgnoreCase("give")){
                    return NXray.getInstance().getScannerManager().getXrayScanners().stream().map(XrayScanner::getId).toList();
                }
        }
        return List.of();
    }

}
