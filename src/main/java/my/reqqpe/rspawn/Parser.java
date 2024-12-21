package my.reqqpe.rspawn;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static String color(String from) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(from);
        while (matcher.find()) {
            String hexCode = from.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch)
                builder.append("&").append(c);
            from = from.replace(hexCode, builder.toString());
            matcher = pattern.matcher(from);
        }
        return ChatColor.translateAlternateColorCodes('&', from);
    }

    public static boolean CheckPermission(String permission_none, CommandSender sender, Main plugin) {

        String permission = permission_none;
        String getConfPerm = plugin.getConfig().getString("permission." + permission_none);
        if (getConfPerm != null) {
            permission = getConfPerm;
        }
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(color(plugin.getConfig().getString("messages.no-permission", "&cУ вас нет прав на использование данной команды")));
            return false;
        }
        return true;
    }
    public static void sendMessage(String confmessage, CommandSender sender, Main plugin) {

        String message = "Не удалось загрузить сообщение";
        String getConfMessage = plugin.getConfig().getString("messages." + confmessage);
        if (getConfMessage !=null) {
            message = getConfMessage;
        }
        sender.sendMessage(color(message));
    }
}